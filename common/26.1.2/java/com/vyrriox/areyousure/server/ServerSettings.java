package com.vyrriox.areyousure.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.vyrriox.areyousure.AreYouSureCommon;
import com.vyrriox.areyousure.network.SettingsPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Per-world settings managed by the admin command, stored in {@code <world>/areyousure.json}.
 *
 * @author vyrriox
 */
public final class ServerSettings {
    public static final int MIN_CODE_LENGTH = 3;
    public static final int MAX_CODE_LENGTH = 10;
    public static final int DEFAULT_CODE_LENGTH = 6;
    public static final int DEFAULT_PASS_TIMEOUT = 30;

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static MinecraftServer server;
    private static Path file;

    private static boolean globalEnabled = true;
    private static int codeLength = DEFAULT_CODE_LENGTH;
    private static int passTimeout = DEFAULT_PASS_TIMEOUT;
    private static final Map<UUID, Boolean> OVERRIDES = new TreeMap<>();

    private ServerSettings() {
    }

    public static void load(MinecraftServer minecraftServer) {
        server = minecraftServer;
        file = minecraftServer.getWorldPath(LevelResource.ROOT).resolve(AreYouSureCommon.MODID + ".json");
        globalEnabled = true;
        codeLength = DEFAULT_CODE_LENGTH;
        passTimeout = DEFAULT_PASS_TIMEOUT;
        OVERRIDES.clear();
        if (!Files.exists(file)) {
            return;
        }
        try {
            JsonObject json = GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), JsonObject.class);
            if (json.has("enabled")) {
                globalEnabled = json.get("enabled").getAsBoolean();
            }
            if (json.has("codeLength")) {
                codeLength = clamp(json.get("codeLength").getAsInt(), MIN_CODE_LENGTH, MAX_CODE_LENGTH);
            }
            if (json.has("passTimeoutSeconds")) {
                passTimeout = Math.max(0, json.get("passTimeoutSeconds").getAsInt());
            }
            if (json.has("players")) {
                json.getAsJsonObject("players").entrySet()
                        .forEach(e -> OVERRIDES.put(UUID.fromString(e.getKey()), e.getValue().getAsBoolean()));
            }
        } catch (RuntimeException | IOException e) {
            LOGGER.error("Could not read {}, using defaults", file, e);
        }
    }

    public static void unload() {
        server = null;
        file = null;
    }

    private static void save() {
        if (file == null) {
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("enabled", globalEnabled);
        json.addProperty("codeLength", codeLength);
        json.addProperty("passTimeoutSeconds", passTimeout);
        JsonObject players = new JsonObject();
        OVERRIDES.forEach((id, on) -> players.addProperty(id.toString(), on));
        json.add("players", players);
        try {
            Files.writeString(file, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Could not write {}", file, e);
        }
    }

    public static boolean isGlobalEnabled() {
        return globalEnabled;
    }

    public static int codeLength() {
        return codeLength;
    }

    public static int passTimeout() {
        return passTimeout;
    }

    public static Map<UUID, Boolean> overrides() {
        return OVERRIDES;
    }

    /** Null when the player follows the global setting. */
    public static Boolean override(UUID player) {
        return OVERRIDES.get(player);
    }

    public static boolean isEnabledFor(UUID player) {
        Boolean value = OVERRIDES.get(player);
        return value != null ? value : globalEnabled;
    }

    public static void setGlobalEnabled(boolean enabled) {
        globalEnabled = enabled;
        save();
        syncAll();
    }

    public static void setCodeLength(int length) {
        codeLength = clamp(length, MIN_CODE_LENGTH, MAX_CODE_LENGTH);
        save();
        syncAll();
    }

    public static void setPassTimeout(int seconds) {
        passTimeout = Math.max(0, seconds);
        save();
        syncAll();
    }

    public static void setOverride(ServerPlayer player, Boolean enabled) {
        if (enabled == null) {
            OVERRIDES.remove(player.getUUID());
        } else {
            OVERRIDES.put(player.getUUID(), enabled);
        }
        save();
        sync(player, false);
    }

    public static void clearOverrides() {
        OVERRIDES.clear();
        save();
        syncAll();
    }

    public static void sync(ServerPlayer player, boolean challenge) {
        AreYouSureCommon.send(player, new SettingsPayload(isEnabledFor(player.getUUID()), codeLength, passTimeout, challenge));
    }

    public static void syncAll() {
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(p -> sync(p, false));
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
