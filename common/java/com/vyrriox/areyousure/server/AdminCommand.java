package com.vyrriox.areyousure.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * {@code /areyousure} admin command (permission level 2).
 *
 * @author vyrriox
 */
public final class AdminCommand {
    private AdminCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Predicate<CommandSourceStack> permission) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("areyousure").requires(permission);

        root.then(Commands.literal("status")
                .executes(ctx -> status(ctx.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> playerStatus(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        root.then(Commands.literal("enable").executes(ctx -> global(ctx.getSource(), true)));
        root.then(Commands.literal("disable").executes(ctx -> global(ctx.getSource(), false)));

        root.then(Commands.literal("player")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("enable").executes(ctx -> override(ctx, Boolean.TRUE)))
                        .then(Commands.literal("disable").executes(ctx -> override(ctx, Boolean.FALSE)))
                        .then(Commands.literal("reset").executes(ctx -> override(ctx, null)))));

        root.then(Commands.literal("only")
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(AdminCommand::only)));

        root.then(Commands.literal("resetplayers").executes(ctx -> {
            ServerSettings.clearOverrides();
            return ok(ctx.getSource(), msg("resetplayers", "All player overrides cleared."));
        }));

        root.then(Commands.literal("codelength")
                .then(Commands.argument("length", IntegerArgumentType.integer(ServerSettings.MIN_CODE_LENGTH, ServerSettings.MAX_CODE_LENGTH))
                        .executes(ctx -> {
                            int length = IntegerArgumentType.getInteger(ctx, "length");
                            ServerSettings.setCodeLength(length);
                            return ok(ctx.getSource(), msg("codelength", "Captcha length set to %s.", length));
                        })));

        root.then(Commands.literal("timeout")
                .then(Commands.argument("seconds", IntegerArgumentType.integer(0, 3600))
                        .executes(ctx -> {
                            int seconds = IntegerArgumentType.getInteger(ctx, "seconds");
                            ServerSettings.setPassTimeout(seconds);
                            return ok(ctx.getSource(), msg("timeout", "Idle pass timeout set to %s seconds (0 = never).", seconds));
                        })));

        root.then(Commands.literal("challenge")
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(ctx -> {
                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
                            targets.forEach(p -> ServerSettings.sync(p, true));
                            return ok(ctx.getSource(), msg("challenge", "Captcha sent to %s player(s).", targets.size()));
                        })));

        dispatcher.register(root);
    }

    private static int status(CommandSourceStack source) {
        source.sendSuccess(() -> msg("status.header", "Are You Sure? status").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        source.sendSuccess(() -> msg("status.global", "Global: %s", onOff(ServerSettings.isGlobalEnabled())), false);
        source.sendSuccess(() -> msg("status.codelength", "Captcha length: %s", ServerSettings.codeLength()), false);
        source.sendSuccess(() -> msg("status.timeout", "Idle pass timeout: %s s", ServerSettings.passTimeout()), false);
        if (ServerSettings.overrides().isEmpty()) {
            source.sendSuccess(() -> msg("status.nooverride", "No player override."), false);
        }
        for (var entry : ServerSettings.overrides().entrySet()) {
            String name = nameOf(source, entry.getKey());
            source.sendSuccess(() -> Component.literal(" - " + name + ": ").append(onOff(entry.getValue())), false);
        }
        return 1;
    }

    private static int playerStatus(CommandSourceStack source, ServerPlayer player) {
        Boolean override = ServerSettings.override(player.getUUID());
        Component origin = override == null ? msg("status.followsglobal", "global") : msg("status.override", "override");
        return ok(source, msg("status.player", "%s: %s (%s)", player.getDisplayName(),
                onOff(ServerSettings.isEnabledFor(player.getUUID())), origin));
    }

    private static int global(CommandSourceStack source, boolean enabled) {
        ServerSettings.setGlobalEnabled(enabled);
        return ok(source, msg("global", "Are You Sure? is now %s for everyone without an override.", onOff(enabled)));
    }

    private static int override(CommandContext<CommandSourceStack> ctx, Boolean enabled) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
        targets.forEach(p -> ServerSettings.setOverride(p, enabled));
        Component state = enabled == null ? msg("status.followsglobal", "global") : onOff(enabled);
        return ok(ctx.getSource(), msg("player", "%s player(s) set to %s.", targets.size(), state));
    }

    /** Disables the mod globally and enables it only for the given players. */
    private static int only(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
        ServerSettings.clearOverrides();
        targets.forEach(p -> ServerSettings.setOverride(p, Boolean.TRUE));
        ServerSettings.setGlobalEnabled(false);
        return ok(ctx.getSource(), msg("only", "Are You Sure? now only targets %s player(s).", targets.size()));
    }

    private static int ok(CommandSourceStack source, Component message) {
        source.sendSuccess(() -> message, true);
        return 1;
    }

    private static String nameOf(CommandSourceStack source, UUID id) {
        ServerPlayer online = source.getServer().getPlayerList().getPlayer(id);
        return online != null ? online.getGameProfile().getName() : id.toString();
    }

    private static Component onOff(boolean on) {
        return on ? msg("on", "ON").withStyle(ChatFormatting.GREEN) : msg("off", "OFF").withStyle(ChatFormatting.RED);
    }

    private static MutableComponent msg(String key, String fallback, Object... args) {
        return Component.translatableWithFallback("areyousure.command." + key, fallback, args);
    }
}
