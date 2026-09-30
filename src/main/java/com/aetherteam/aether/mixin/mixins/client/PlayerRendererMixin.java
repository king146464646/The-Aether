package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.accessory.FirstPersonRendering;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.mixin.AetherMixinHooks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @Unique
    private static HumanoidArm currentArm = null;

    @Inject(method = "renderRightHand", at = @At("HEAD"))
    private void firstPersonRightAccessories(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, CallbackInfo ci) {
        currentArm = HumanoidArm.RIGHT;
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"))
    private void firstPersonLeftAccessories(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, CallbackInfo ci) {
        currentArm = HumanoidArm.LEFT;
    }

    /**
     * Renders accessories in first person alongside the player's hand, unless a player arm is already being rendered for an invisible wearer.
     *
     * @see com.aetherteam.aether.mixin.mixins.client.ItemInHandRendererMixin
     */
    @WrapMethod(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V")
    private void renderHand(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, ModelPart rendererArm, ModelPart rendererArmwear, Operation<Void> original) {
        PlayerModel<AbstractClientPlayer> playerModel = ((PlayerRenderer) (Object) this).getModel();
        original.call(poseStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
        HumanoidArm arm = currentArm;
        currentArm = null;
        if (arm != null && !AetherMixinHooks.RENDERING_ACCESSORY) {
            EquipmentUtil.getAccessories(player, (item) -> item.getItem() instanceof AccessoryItem).forEach((slotResult) -> {
                String identifier = slotResult.slotContext().identifier();
                int index = slotResult.slotContext().index();
                ItemStack itemStack = slotResult.stack();
                CuriosApi.getCuriosInventory(player).flatMap(handler -> handler.getStacksHandler(identifier)).ifPresent((stacksHandler) -> {
                    if (index < stacksHandler.getRenders().size() && stacksHandler.getRenders().get(index)) { // Check if the accessory is visible.
                        CuriosRendererRegistry.getRenderer(itemStack.getItem()).ifPresent((renderer) -> {
                            if (renderer instanceof FirstPersonRendering firstPersonRendering && firstPersonRendering.shouldRenderInFirstPerson(arm, itemStack, slotResult.slotContext())) {
                                poseStack.pushPose();
                                firstPersonRendering.renderOnFirstPerson(arm, itemStack, slotResult.slotContext(), poseStack, playerModel, buffer, combinedLight);
                                poseStack.popPose();
                            }
                        });
                    }
                });
            });
        }
    }
}
