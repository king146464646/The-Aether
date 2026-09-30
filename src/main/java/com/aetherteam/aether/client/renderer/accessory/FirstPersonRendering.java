package com.aetherteam.aether.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

/**
 * Handled by the Aether itself, since Curios has no first-person rendering support of its own.
 * Implemented by curio renderers that should also be drawn in the player's hands.
 */
public interface FirstPersonRendering {
    /**
     * Whether the curio should be rendered in first person for the given arm.
     *
     * @param arm         The {@link HumanoidArm} being rendered.
     * @param stack       The accessory {@link ItemStack}.
     * @param slotContext The {@link SlotContext} of the accessory.
     * @return Whether to render in first person, as a {@link Boolean}.
     */
    default boolean shouldRenderInFirstPerson(HumanoidArm arm, ItemStack stack, SlotContext slotContext) {
        return false;
    }

    /**
     * Renders the accessory in the player's hand in first person.
     *
     * @param arm           The {@link HumanoidArm} being rendered.
     * @param stack         The accessory {@link ItemStack}.
     * @param slotContext   The {@link SlotContext} of the accessory.
     * @param matrices      The rendering {@link PoseStack}.
     * @param model         The {@link EntityModel} for the renderer.
     * @param buffer        The rendering {@link MultiBufferSource}.
     * @param light         The {@link Integer} for the packed lighting for rendering.
     */
    <M extends LivingEntity> void renderOnFirstPerson(HumanoidArm arm, ItemStack stack, SlotContext slotContext, PoseStack matrices, EntityModel<M> model, MultiBufferSource buffer, int light);
}
