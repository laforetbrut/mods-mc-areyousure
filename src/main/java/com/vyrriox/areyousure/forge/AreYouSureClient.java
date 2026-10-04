package com.vyrriox.areyousure.forge;

import com.vyrriox.areyousure.client.ActionGuard;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;

/**
 * Client-only listeners.
 *
 * @author vyrriox
 */
final class AreYouSureClient {
    private AreYouSureClient() {
    }

    static void init() {
        InputEvent.InteractionKeyMappingTriggered.BUS.addListener(AreYouSureClient::onInteraction);
        TickEvent.ClientTickEvent.Post.BUS.addListener(e -> ActionGuard.onClientTick());
    }

    /** Returns true to cancel the vanilla action. */
    private static boolean onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        ActionGuard.Kind kind = event.isAttack() ? ActionGuard.Kind.ATTACK
                : event.isPickBlock() ? ActionGuard.Kind.PICK : ActionGuard.Kind.USE;
        if (ActionGuard.intercept(kind, event.getHand())) {
            event.setSwingHand(false);
            return true;
        }
        return false;
    }
}
