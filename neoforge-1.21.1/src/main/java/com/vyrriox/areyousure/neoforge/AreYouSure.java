package com.vyrriox.areyousure.neoforge;

import com.vyrriox.areyousure.AreYouSureCommon;
import com.vyrriox.areyousure.client.ClientState;
import com.vyrriox.areyousure.network.SettingsPayload;
import com.vyrriox.areyousure.server.AdminCommand;
import com.vyrriox.areyousure.server.ServerSettings;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * NeoForge entry point.
 *
 * @author vyrriox
 */
@Mod(AreYouSureCommon.MODID)
public final class AreYouSure {

    public AreYouSure(IEventBus modBus, Dist dist) {
        modBus.addListener(AreYouSure::registerPayloads);
        AreYouSureCommon.setSender((player, payload) -> {
            if (player.connection.hasChannel(SettingsPayload.TYPE)) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        });
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> AdminCommand.register(e.getDispatcher(), s -> s.hasPermission(2)));
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent e) -> ServerSettings.load(e.getServer()));
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> ServerSettings.unload());
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                ServerSettings.sync(player, false);
            }
        });
        if (dist.isClient()) {
            AreYouSureClient.init();
        }
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional().playToClient(SettingsPayload.TYPE, SettingsPayload.STREAM_CODEC,
                (payload, context) -> ClientState.apply(payload));
    }
}
