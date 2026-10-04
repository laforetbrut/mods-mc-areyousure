package com.vyrriox.areyousure.forge;

import com.vyrriox.areyousure.client.ActionGuard;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
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
        MinecraftForge.EVENT_BUS.addListener(AreYouSureClient::onInteraction);
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                ActionGuard.onClientTick();
            }
        });
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
