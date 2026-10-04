package com.vyrriox.areyousure.client;

import com.vyrriox.areyousure.network.SettingsPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Settings received from the server. Falls back to defaults on servers without the mod.
 *
 * @author vyrriox
 */
public final class ClientState {
    private static final boolean DEFAULT_ENABLED = true;
    private static final int DEFAULT_CODE_LENGTH = 6;
    private static final int DEFAULT_PASS_TIMEOUT = 30;

    private static boolean enabled = DEFAULT_ENABLED;
    private static int codeLength = DEFAULT_CODE_LENGTH;
    private static int passTimeoutSeconds = DEFAULT_PASS_TIMEOUT;
    private static Object syncedConnection;

    private ClientState() {
    }

    public static void apply(SettingsPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        syncedConnection = mc.getConnection();
        enabled = payload.enabled();
        codeLength = payload.codeLength();
        passTimeoutSeconds = payload.passTimeoutSeconds();
        if (!enabled) {
            ActionGuard.clearPass();
        }
        if (payload.challenge() && mc.player != null) {
            mc.setScreen(new SureScreen(Component.translatable("areyousure.challenge"), null, null));
        }
    }

    /** Resets to defaults when the client joins a different server. */
    static void checkConnection(Minecraft mc) {
        Object connection = mc.getConnection();
        if (connection != null && connection != syncedConnection) {
            syncedConnection = connection;
            enabled = DEFAULT_ENABLED;
            codeLength = DEFAULT_CODE_LENGTH;
            passTimeoutSeconds = DEFAULT_PASS_TIMEOUT;
            ActionGuard.clearPass();
        }
    }

    public static boolean enabled() {
        return enabled;
    }

    public static int codeLength() {
        return codeLength;
    }

    public static int passTimeoutSeconds() {
        return passTimeoutSeconds;
    }
}
