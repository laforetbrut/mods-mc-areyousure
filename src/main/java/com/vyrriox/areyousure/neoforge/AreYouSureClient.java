package com.vyrriox.areyousure.neoforge;

import com.vyrriox.areyousure.client.ActionGuard;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-only listeners.
 *
 * @author vyrriox
 */
final class AreYouSureClient {
    private AreYouSureClient() {
    }

    static void init() {
        NeoForge.EVENT_BUS.addListener(AreYouSureClient::onInteraction);
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> ActionGuard.onClientTick());
    }

    private static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        ActionGuard.Kind kind = event.isAttack() ? ActionGuard.Kind.ATTACK
                : event.isPickBlock() ? ActionGuard.Kind.PICK : ActionGuard.Kind.USE;
        if (ActionGuard.intercept(kind, event.getHand())) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }
}
