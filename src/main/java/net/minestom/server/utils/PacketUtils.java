package net.minestom.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.player.NettyPlayerConnection;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Utils class for packets. Including writing a {@link ServerPacket} into a {@link ByteBuf}
 * for network processing.
 */
public final class PacketUtils {

    private static final PacketListenerManager PACKET_LISTENER_MANAGER = MinecraftServer.getPacketListenerManager();

    private PacketUtils() {

    }

    /**
     * Sends a {@link ServerPacket} to multiple players.
     * <p>
     * Can drastically improve performance since the packet will not have to be processed as much.
     *
     * @param players the players to send the packet to
     * @param packet  the packet to send to the players
     */
    public static void sendGroupedPacket(@NotNull Collection<Player> players, @NotNull ServerPacket packet) {
        final ByteBuf buffer = writePacket(packet);
        final boolean success = PACKET_LISTENER_MANAGER.processServerPacket(packet, players);
        if (success) {
            for (Player player : players) {
                final PlayerConnection playerConnection = player.getPlayerConnection();
                if (playerConnection instanceof NettyPlayerConnection) {
                    ((NettyPlayerConnection) playerConnection).getChannel().write(buffer.retainedSlice());
                } else {
                    playerConnection.sendPacket(packet);
                }
            }
        }
    }

    /**
     * Writes a {@link ServerPacket} into a {@link ByteBuf}.
     *
     * @param buf    the recipient of {@code packet}
     * @param packet the packet to write into {@code buf}
     */
    public static void writePacket(@NotNull ByteBuf buf, @NotNull ServerPacket packet) {
        final ByteBuf packetBuffer = getPacketBuffer(packet);

        writePacket(buf, packetBuffer, packet.getId());
    }

    /**
     * Writes a {@link ServerPacket} into a newly created {@link ByteBuf}.
     *
     * @param packet the packet to write
     * @return a {@link ByteBuf} containing {@code packet}
     */
    @NotNull
    public static ByteBuf writePacket(@NotNull ServerPacket packet) {
        final ByteBuf packetBuffer = getPacketBuffer(packet);

        // Add 5 for the packet id and for the packet size
        final int size = packetBuffer.writerIndex() + 5 + 5;
        ByteBuf buffer = Unpooled.buffer(size);

        writePacket(buffer, packetBuffer, packet.getId());

        return buffer;
    }

    /**
     * Writes a packet buffer into {@code buf}.
     *
     * @param buf          the buffer which will receive the packet id/data
     * @param packetBuffer the buffer containing the raw packet data
     * @param packetId     the packet id
     */
    private static void writePacket(@NotNull ByteBuf buf, @NotNull ByteBuf packetBuffer, int packetId) {
        Utils.writeVarIntBuf(buf, packetId);
        buf.writeBytes(packetBuffer);
        packetBuffer.release();
    }

    /**
     * Gets the buffer representing the raw packet data.
     *
     * @param packet the packet to write
     * @return the {@link ByteBuf} containing the raw packet data
     */
    @NotNull
    private static ByteBuf getPacketBuffer(@NotNull ServerPacket packet) {
        BinaryWriter writer = new BinaryWriter();
        packet.write(writer);

        return writer.getBuffer();
    }

}
