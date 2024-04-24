package net.minestom.server.network.player;

import net.minestom.server.network.packet.client.ClientPacket;
import org.jetbrains.annotations.NotNull;

/**
 * Hooked into the {@link PlayerSocketConnection} and called for each client (inbound) packet processed
 */
public interface InboundPacketListener {
    void onInboundPacketProcessed(@NotNull ClientPacket packet);
}