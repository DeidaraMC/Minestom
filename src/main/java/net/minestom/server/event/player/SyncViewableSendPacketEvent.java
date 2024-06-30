package net.minestom.server.event.player;

import net.minestom.server.Viewable;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.ServerPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Listen to outgoing packets sync, only fires once
 * <p>
 * Currently, do not support viewable packets.
 */
@ApiStatus.Experimental
public class SyncViewableSendPacketEvent implements CancellableEvent {
    private final Viewable entity;
    private final SendablePacket packet;
    private SendablePacket sentPacket;
    private boolean cancelled;

    public SyncViewableSendPacketEvent(Viewable entity, SendablePacket packet) {
        this.entity = entity;
        this.packet = packet;
        this.sentPacket = packet;
    }

    public @NotNull Viewable getSender() {
        return entity;
    }

    public @NotNull SendablePacket getPacket() {
        return packet;
    }
    public @NotNull SendablePacket getSentPacket() {
        return sentPacket;
    }

    public void changeSentPacket(@NotNull SendablePacket packet) {
        this.sentPacket = packet;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
