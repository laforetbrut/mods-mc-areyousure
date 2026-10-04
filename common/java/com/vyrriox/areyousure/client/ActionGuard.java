package com.vyrriox.areyousure.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Decides whether an interaction may go through.
 * <p>
 * A solved captcha unlocks one <em>kind</em> of action (for example "attack Pig" or "break Oak Log"),
 * which can then be repeated freely. Switching to any other kind of action asks again. Actions with no
 * target and no item (swinging at the air, using an empty hand) never change the current pass.
 *
 * @author vyrriox
 */
public final class ActionGuard {
    public enum Kind { ATTACK, USE, PICK }

    /** What the player is about to do, plus a human readable description. */
    public record Action(String context, Component description, ItemStack icon) {
    }

    private static String passContext;
    private static long lastUse;

    private ActionGuard() {
    }

    /**
     * Called by the loader for every attack / use / pick-block input.
     *
     * @return true when the vanilla action must be cancelled
     */
    public static boolean intercept(Kind kind, InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.isSpectator() || !ClientState.enabled()) {
            return false;
        }
        Action action = describe(mc, kind, hand);
        if (action == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (passContext != null && isExpired(now)) {
            passContext = null;
        }
        if (action.context().equals(passContext)) {
            lastUse = now;
            return false;
        }
        if (mc.screen == null) {
            mc.setScreen(new SureScreen(action.description(), action.context(), action.icon()));
        }
        return true;
    }

    public static void onClientTick() {
        ClientState.checkConnection(Minecraft.getInstance());
    }

    static void grant(String context) {
        passContext = context;
        lastUse = System.currentTimeMillis();
    }

    static void clearPass() {
        passContext = null;
    }

    private static boolean isExpired(long now) {
        int timeout = ClientState.passTimeoutSeconds();
        return timeout > 0 && now - lastUse > timeout * 1000L;
    }

    /** Returns null for neutral actions that never require a captcha. */
    private static Action describe(Minecraft mc, Kind kind, InteractionHand hand) {
        HitResult hit = mc.hitResult;
        String verb = switch (kind) {
            case ATTACK -> "attack";
            case USE -> "use";
            case PICK -> "pick";
        };
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK && mc.level != null) {
            Block block = mc.level.getBlockState(blockHit.getBlockPos()).getBlock();
            ItemStack held = mc.player.getItemInHand(hand);
            if (kind == Kind.USE && held.getItem() instanceof BlockItem && !(block instanceof EntityBlock)) {
                // Building: one pass per placed block type, wherever it goes.
                String itemId = BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
                return new Action("place:" + itemId,
                        Component.translatable("areyousure.action.place", held.getHoverName()),
                        held.copyWithCount(1));
            }
            String id = BuiltInRegistries.BLOCK.getKey(block).toString();
            return new Action(verb + ":block:" + id,
                    Component.translatable("areyousure.action." + verb + ".block", block.getName()),
                    new ItemStack(block.asItem()));
        }
        if (hit instanceof EntityHitResult entityHit && hit.getType() == HitResult.Type.ENTITY) {
            Entity entity = entityHit.getEntity();
            String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
            return new Action(verb + ":entity:" + id,
                    Component.translatable("areyousure.action." + verb + ".entity", entity.getType().getDescription()),
                    ItemStack.EMPTY);
        }
        if (kind == Kind.USE) {
            ItemStack held = mc.player.getItemInHand(hand);
            if (!held.isEmpty()) {
                String id = BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
                return new Action("use:item:" + id,
                        Component.translatable("areyousure.action.use.item", held.getHoverName()),
                        held.copyWithCount(1));
            }
        }
        return null;
    }
}
