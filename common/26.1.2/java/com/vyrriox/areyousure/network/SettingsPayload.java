package com.vyrriox.areyousure.network;

import com.vyrriox.areyousure.AreYouSureCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server to client: the effective settings for this player, optionally forcing a captcha now.
 *
 * @author vyrriox
 */
public record SettingsPayload(boolean enabled, int codeLength, int passTimeoutSeconds, boolean challenge)
        implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(AreYouSureCommon.MODID, "settings");
    public static final Type<SettingsPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SettingsPayload> STREAM_CODEC =
            StreamCodec.of((buf, p) -> p.encode(buf), SettingsPayload::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
        buf.writeVarInt(codeLength);
        buf.writeVarInt(passTimeoutSeconds);
        buf.writeBoolean(challenge);
    }

    public static SettingsPayload decode(FriendlyByteBuf buf) {
        return new SettingsPayload(buf.readBoolean(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
