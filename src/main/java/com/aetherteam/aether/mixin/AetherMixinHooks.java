package com.aetherteam.aether.mixin;

import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.aetherteam.aether.mixin.mixins.common.accessor.MinecraftServerAccessor;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class AetherMixinHooks {
    /**
     * Set while a player arm is being rendered for an invisible wearer, to avoid rendering accessories on it twice.
     *
     * @see com.aetherteam.aether.mixin.mixins.client.ItemInHandRendererMixin
     */
    @ApiStatus.Internal
    public static boolean RENDERING_ACCESSORY = false;
    /**
     * Checks whether a cape accessory is visible.
     *
     * @param livingEntity The {@link LivingEntity} wearing the cape.
     * @return Whether the cape is visible, as a {@link Boolean}.
     * @see com.aetherteam.aether.mixin.mixins.client.PlayerSkinMixin
     */
    public static ItemStack isCapeVisible(LivingEntity livingEntity) {
        Optional<SlotResult> slotResult = EquipmentUtil.findFirstAccessory(livingEntity, (item) -> item.getItem() instanceof CapeItem);
        if (slotResult.isPresent()) {
            SlotResult result = slotResult.get();
            String identifier = result.slotContext().identifier();
            int index = result.slotContext().index();
            Optional<ICuriosItemHandler> itemHandler = CuriosApi.getCuriosInventory(livingEntity);
            if (itemHandler.isPresent()) {
                Optional<ICurioStacksHandler> stacksHandler = itemHandler.get().getStacksHandler(identifier);
                if (stacksHandler.isPresent()) {
                    ICurioStacksHandler handler = stacksHandler.get();
                    if (index < handler.getRenders().size() && handler.getRenders().get(index)) {
                        ItemStack cosmeticStack = handler.getCosmeticStacks().getStackInSlot(index);
                        if (handler.hasCosmetic() && !cosmeticStack.isEmpty()) {
                            return cosmeticStack;
                        }
                        return result.stack();
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Gets the cape texture from a {@link CapeItem}.
     *
     * @param stack The {@link ItemStack}.
     * @return The {@link ResourceLocation} texture from the cape.
     */
    public static ResourceLocation getCapeTexture(ItemStack stack) {
        if (stack.getItem() instanceof CapeItem capeItem) {
            for (Map.Entry<Predicate<ItemStack>, ResourceLocation> entry : AetherClient.CAPE_SECRETS.entrySet()) {
                if (entry.getKey().test(stack)) {
                    return entry.getValue();
                }
            }
            return capeItem.getCapeTexture();
        }
        return null;
    }

    /**
     * Checks whether the {@link SelectWorldScreen} is open and the level that the lock belongs to is the same one as the level loaded by the world preview.
     *
     * @param basePath The {@link Path} for the level directory.
     * @return Whether the level can be unlocked, as a {@link Boolean}.
     * @see com.aetherteam.aether.mixin.mixins.common.DirectoryLockMixin
     */
    public static boolean canUnlockLevel(Path basePath) {
        if (Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof SelectWorldScreen && Minecraft.getInstance().getSingleplayerServer() != null) {
            return basePath.getFileName().toString().equals(((MinecraftServerAccessor) Minecraft.getInstance().getSingleplayerServer()).aether$getStorageSource().getLevelId());
        }
        return false;
    }

    /**
     * Whether an accessory can be equipped or replace an already equipped accessory.
     *
     * @param mob       The {@link Mob} to equip the accessory to.
     * @param candidate The {@link ItemStack} to try to equip.
     * @param existing  The {@link ItemStack} already equipped.
     * @return Whether the accessory can be equipped or replaced, as a {@link Boolean}.
     */
    public static boolean canReplaceCurrentAccessory(Mob mob, ItemStack candidate, ItemStack existing) {
        if (EnchantmentHelper.hasAnyEnchantments(existing)) {
            return false;
        } else {
            if (candidate.getItem() instanceof GlovesItem candidateGloves) {
                if (!(existing.getItem() instanceof GlovesItem existingGloves)) {
                    return true;
                } else {
                    if (candidateGloves.getDamage() != existingGloves.getDamage()) {
                        return candidateGloves.getDamage() > existingGloves.getDamage();
                    } else {
                        return mob.canReplaceEqualItem(candidate, existing);
                    }
                }
            } else if (candidate.getItem() instanceof PendantItem) {
                if (!(existing.getItem() instanceof PendantItem)) {
                    return true;
                } else {
                    return mob.canReplaceEqualItem(candidate, existing);
                }
            }
        }
        return false;
    }

    /**
     * Gets the corresponding slot identifier for an accessory item.
     *
     * @param livingEntity The {@link LivingEntity} to get the accessory from.
     * @param stack        The accessory {@link ItemStack}.
     * @return The slot identifier {@link String}.
     */
    public static String getIdentifierForItem(LivingEntity livingEntity, ItemStack stack) {
        if (stack.getItem() instanceof GlovesItem glovesItem) {
            return glovesItem.getIdentifier();
        } else if (stack.getItem() instanceof PendantItem pendantItem && (livingEntity.getType() == EntityType.PIGLIN || livingEntity.getType() == EntityType.ZOMBIFIED_PIGLIN)) {
            return pendantItem.getIdentifier();
        }
        return null;
    }

    /**
     * Gets an accessory from an entity.
     *
     * @param livingEntity The {@link LivingEntity} to get the accessory from.
     * @param identifier   The {@link String} for the slot identifier.
     * @return The accessory {@link ItemStack} gotten from the entity.
     */
    public static ItemStack getItemByIdentifier(LivingEntity livingEntity, String identifier) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .flatMap(handler -> handler.getStacksHandler(identifier))
                .map(ICurioStacksHandler::getStacks)
                .filter(stackHandler -> 0 < stackHandler.getSlots())
                .map(stackHandler -> stackHandler.getStackInSlot(0))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Equips an accessory to an entity.
     *
     * @param livingEntity The {@link LivingEntity} to equip to.
     * @param itemStack    The {@link ItemStack} to equip.
     * @param identifier   The {@link String} for the slot identifier.
     */
    public static void setItemByIdentifier(LivingEntity livingEntity, ItemStack itemStack, String identifier) {
        CuriosApi.getCuriosInventory(livingEntity)
                .flatMap(handler -> handler.getStacksHandler(identifier))
                .map(ICurioStacksHandler::getStacks)
                .filter(stackHandler -> 0 < stackHandler.getSlots())
                .ifPresent(stackHandler -> stackHandler.setStackInSlot(0, itemStack));
    }
}
