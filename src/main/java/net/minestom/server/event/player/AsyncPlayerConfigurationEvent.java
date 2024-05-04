package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.configuration.ResetChatPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player enters the configuration state (either on first connection, or if they are
 * sent back to configuration later). The player is moved to the play state as soon as all event
 * handles finish processing this event.
 *
 * <p>The spawning instance <b>must</b> be set for the player to join.</p>
 *
 * <p>The event is called off the tick threads, so it is safe to block here</p>
 */
public class AsyncPlayerConfigurationEvent implements PlayerEvent {
    private final Player player;
    private final boolean isFirstConfig;

    private boolean hardcore;
    private boolean clearChat;
    private boolean sendRegistryData;
    private Instance spawningInstance;

    public AsyncPlayerConfigurationEvent(@NotNull Player player, boolean isFirstConfig) {
        this.player = player;
        this.isFirstConfig = isFirstConfig;

        this.hardcore = false;
        this.clearChat = false;
        this.sendRegistryData = isFirstConfig;
        this.spawningInstance = null;
    }

    @Override
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * Returns true if this is the first time the player is in the configuration phase (they are joining), false otherwise.
     */
    public boolean isFirstConfig() {
        return isFirstConfig;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    /**
     * If true, the player's chat will be cleared when exiting the configuration state, otherwise
     * it will be preserved. The default is not to clear the chat.
     *
     * @return true if the chat will be cleared, false otherwise
     *
     * @see ResetChatPacket
     */
    public boolean willClearChat() {
        return clearChat;
    }

    /**
     * Set whether the player's chat will be cleared when exiting the configuration state.
     *
     * @param clearChat true to clear the chat, false otherwise
     *
     * @see ResetChatPacket
     */
    public void setClearChat(boolean clearChat) {
        this.clearChat = clearChat;
    }

    public boolean willSendRegistryData() {
        return sendRegistryData;
    }

    public void setSendRegistryData(boolean sendRegistryData) {
        this.sendRegistryData = sendRegistryData;
    }

    public @Nullable Instance getSpawningInstance() {
        return spawningInstance;
    }

    public void setSpawningInstance(@Nullable Instance spawningInstance) {
        this.spawningInstance = spawningInstance;
    }
}
