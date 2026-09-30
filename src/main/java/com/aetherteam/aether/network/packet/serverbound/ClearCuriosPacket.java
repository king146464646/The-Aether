package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;

/**
 * Empties every curio slot of the player's curios inventory. This is the equivalent of clearing the vanilla inventory from the creative mode trash slot,
 * which vanilla's own packets do not cover, and is only accepted from creative players.
 *
 * @see com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen#slotClicked(net.minecraft.world.inventory.Slot, int, int, net.minecraft.world.inventory.ClickType)
 */
public record ClearCuriosPacket() implements CustomPacketPayload {
    public static final Type<ClearCuriosPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Aether.MODID, "clear_curios"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClearCuriosPacket> STREAM_CODEC = StreamCodec.unit(new ClearCuriosPacket());

    @Override
    public Type<ClearCuriosPacket> type() {
        return TYPE;
    }

    public static void execute(ClearCuriosPacket payload, IPayloadContext context) {
        Player player = context.player();
        if (player != null && player.getAbilities().instabuild) {
            CuriosApi.getCuriosInventory(player).ifPresent((handler) -> {
                for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                    IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                    for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
                        stackHandler.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                }
                player.containerMenu.broadcastChanges();
            });
        }
    }
}
