package com.aetherteam.aether.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherTags;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Holds the identifiers for the Aether's own curio slots, which are defined by the built-in
 * {@code packs/accessories} data pack (loaded only when {@link com.aetherteam.aether.AetherConfig.Common#use_curios_menu}
 * is disabled) rather than being registered from code.
 * <p>
 * Also registers the item predicates referenced by those slots' {@code validators}.
 */
public class AetherAccessorySlots {
    public static final String GLOVES_SLOT = "aether_gloves";
    public static final String RING_SLOT = "aether_ring";
    public static final String PENDANT_SLOT = "aether_pendant";
    public static final String CAPE_SLOT = "aether_cape";
    public static final String SHIELD_SLOT = "aether_shield";
    public static final String ACCESSORY_SLOT = "aether_accessory";

    private static final ResourceLocation GLOVES_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "gloves_items");
    private static final ResourceLocation RING_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "ring_items");
    private static final ResourceLocation PENDANT_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "pendant_items");
    private static final ResourceLocation CAPE_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "cape_items");
    private static final ResourceLocation SHIELD_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "shield_items");
    private static final ResourceLocation ACCESSORY_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "accessory_items");

    private AetherAccessorySlots() {
    }

    /**
     * Registers the item predicates for the Aether's curio slots. This has to run during mod construction,
     * as the predicates are resolved when the slot data packs are reloaded.
     */
    public static void registerPredicates() {
        CuriosApi.registerCurioPredicate(GLOVES_PREDICATE, (slotResult) -> slotResult.stack().is(AetherTags.Items.ACCESSORIES_GLOVES));
        CuriosApi.registerCurioPredicate(RING_PREDICATE, (slotResult) -> slotResult.stack().is(AetherTags.Items.ACCESSORIES_RINGS));
        CuriosApi.registerCurioPredicate(PENDANT_PREDICATE, (slotResult) -> slotResult.stack().is(AetherTags.Items.ACCESSORIES_PENDANTS));
        CuriosApi.registerCurioPredicate(CAPE_PREDICATE, (slotResult) -> slotResult.stack().is(AetherTags.Items.ACCESSORIES_CAPES));
        CuriosApi.registerCurioPredicate(SHIELD_PREDICATE, (slotResult) -> slotResult.stack().is(AetherTags.Items.ACCESSORIES_SHIELDS));
        CuriosApi.registerCurioPredicate(ACCESSORY_PREDICATE, (slotResult) -> slotResult.stack().is(AetherTags.Items.ACCESSORIES_MISCELLANEOUS));
    }

    public static String getGlovesSlotType() {
        return GLOVES_SLOT;
    }

    public static String getRingSlotType() {
        return RING_SLOT;
    }

    public static String getPendantSlotType() {
        return PENDANT_SLOT;
    }

    public static String getCapeSlotType() {
        return CAPE_SLOT;
    }

    public static String getShieldSlotType() {
        return SHIELD_SLOT;
    }

    public static String getAccessorySlotType() {
        return ACCESSORY_SLOT;
    }
}
