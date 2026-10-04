package com.vyrriox.areyousure;

import com.vyrriox.areyousure.network.SettingsPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Loader-independent entry points shared by every loader project.
 *
 * @author vyrriox
 */
public final class AreYouSureCommon {
    public static final String MODID = "areyousure";

    /** Implemented by each loader to deliver a payload to one player. */
    public interface PacketSender {
        void send(ServerPlayer player, SettingsPayload payload);
    }

    private static PacketSender sender = (player, payload) -> { };

    private AreYouSureCommon() {
    }

    public static void setSender(PacketSender packetSender) {
        sender = packetSender;
    }

    public static void send(ServerPlayer player, SettingsPayload payload) {
        sender.send(player, payload);
    }
}
