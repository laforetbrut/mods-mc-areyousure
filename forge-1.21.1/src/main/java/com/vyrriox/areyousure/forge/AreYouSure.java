package com.vyrriox.areyousure.forge;

import com.vyrriox.areyousure.AreYouSureCommon;
import com.vyrriox.areyousure.client.ClientState;
import com.vyrriox.areyousure.network.SettingsPayload;
import com.vyrriox.areyousure.server.AdminCommand;
import com.vyrriox.areyousure.server.ServerSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/**
 * Forge entry point.
 *
 * @author vyrriox
 */
@Mod(AreYouSureCommon.MODID)
public final class AreYouSure {
    private static final SimpleChannel CHANNEL = ChannelBuilder.named(SettingsPayload.ID)
            .networkProtocolVersion(1)
            .optional()
            .simpleChannel()
            .messageBuilder(SettingsPayload.class, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SettingsPayload::encode)
            .decoder(SettingsPayload::decode)
            .consumerMainThread((payload, context) -> ClientState.apply(payload))
            .add();

    public AreYouSure() {
        AreYouSureCommon.setSender((player, payload) -> {
            if (CHANNEL.isRemotePresent(player.connection.getConnection())) {
                CHANNEL.send(payload, PacketDistributor.PLAYER.with(player));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> AdminCommand.register(e.getDispatcher(), s -> s.hasPermission(2)));
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent e) -> ServerSettings.load(e.getServer()));
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> ServerSettings.unload());
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                ServerSettings.sync(player, false);
            }
        });
        if (FMLEnvironment.dist.isClient()) {
            AreYouSureClient.init();
        }
    }
}
