package com.aetherteam.aether.item.accessories.cape;

import com.aetherteam.aether.Aether;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class AgilityCapeItem extends CapeItem {
    /**
     * The unique identifier for the item's step height modifier.
     */
    private static final ResourceLocation STEP_HEIGHT_ID = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "agility_cape_step_height");

    public AgilityCapeItem(String capeLocation, Properties properties) {
        super(capeLocation, properties);
    }

    /**
     * Applies a step height modifier to the wearer as long as they aren't holding shift. If they are, the modifier is removed until they stop holding shift.
     *
     * @param slotContext The {@link SlotContext} of the accessory.
     * @param stack       The accessory {@link ItemStack}.
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null) {
            if (!stepHeight.hasModifier(this.getStepHeightModifier().id()) && !livingEntity.isShiftKeyDown()) {
                stepHeight.addTransientModifier(this.getStepHeightModifier());
            }
            if (livingEntity.isShiftKeyDown()) {
                stepHeight.removeModifier(this.getStepHeightModifier().id());
            }
        }
    }

    /**
     * Removes the step height modifier when the Agility Cape is unequipped.
     *
     * @param slotContext The {@link SlotContext} of the accessory.
     * @param newStack    The new {@link ItemStack} in the slot.
     * @param stack       The accessory {@link ItemStack}.
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null) {
            if (stepHeight.hasModifier(this.getStepHeightModifier().id())) {
                stepHeight.removeModifier(this.getStepHeightModifier().id());
            }
        }
    }

    /**
     * @return The step height {@link AttributeModifier}. The default step height is 0.5, so this is an additional 0.5 to give the wearer a full block of step height.
     */
    public AttributeModifier getStepHeightModifier() {
        return new AttributeModifier(STEP_HEIGHT_ID, 0.5, AttributeModifier.Operation.ADD_VALUE);
    }
}
