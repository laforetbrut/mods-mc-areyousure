package com.vyrriox.areyousure.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * Two-step confirmation: "are you sure?" then a captcha to type back.
 *
 * @author vyrriox
 */
public final class SureScreen extends Screen {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int[] COLORS = {0xFFFF6B6B, 0xFF6BFF8E, 0xFF6BA8FF, 0xFFFFE36B, 0xFFE66BFF, 0xFF6BF0FF, 0xFFFFA94D};
    private static final int PANEL_W = 280;
    private static final int PANEL_H = 168;
    private static final int ACCENT = 0xFFE0453A;
    private static final int PANEL_BG = 0xF0141419;
    private static final int PANEL_BORDER = 0xFF3A3A46;
    private static final int TEXT = 0xFFF2F2F2;
    private static final int MUTED = 0xFF9A9AA8;
    private static final long SHAKE_MS = 450L;

    private final Component action;
    @Nullable
    private final String context;
    private final ItemStack icon;
    private final RandomSource random = RandomSource.create();

    private boolean captchaStep;
    private String code = "";
    private int[] charOffsets = new int[0];
    private int[] charColors = new int[0];
    private int[] noise = new int[0];
    private EditBox input;
    private int attempts;
    private long shakeStart;

    /**
     * @param context the action unlocked on success, or null for an admin challenge that unlocks nothing
     */
    public SureScreen(Component action, @Nullable String context, @Nullable ItemStack icon) {
        super(Component.translatable("areyousure.title"));
        this.action = action;
        this.context = context;
        this.icon = icon == null ? ItemStack.EMPTY : icon;
        this.captchaStep = context == null;
        if (captchaStep) {
            newCode();
        }
    }

    private int left() {
        return (width - PANEL_W) / 2;
    }

    private int top() {
        return (height - PANEL_H) / 2;
    }

    @Override
    protected void init() {
        int x = left();
        int y = top();
        if (!captchaStep) {
            addRenderableWidget(Button.builder(Component.translatable("areyousure.yes"), b -> startCaptcha())
                    .bounds(x + 16, y + PANEL_H - 32, 120, 20).build());
            addRenderableWidget(Button.builder(Component.translatable("areyousure.no"), b -> onClose())
                    .bounds(x + PANEL_W - 136, y + PANEL_H - 32, 120, 20).build());
            return;
        }
        String previous = input != null ? input.getValue() : "";
        input = new EditBox(font, x + 16, y + PANEL_H - 58, PANEL_W - 32, 18, Component.translatable("areyousure.captcha.input"));
        input.setMaxLength(code.length());
        input.setHint(Component.translatable("areyousure.captcha.hint"));
        input.setValue(previous);
        addRenderableWidget(input);
        setInitialFocus(input);
        int bw = (PANEL_W - 32 - 8) / 3;
        addRenderableWidget(Button.builder(Component.translatable("areyousure.captcha.verify"), b -> verify())
                .bounds(x + 16, y + PANEL_H - 32, bw, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("areyousure.captcha.refresh"), b -> newCode())
                .bounds(x + 16 + bw + 4, y + PANEL_H - 32, bw, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(context == null ? "areyousure.captcha.giveup" : "areyousure.no"), b -> onClose())
                .bounds(x + 16 + (bw + 4) * 2, y + PANEL_H - 32, bw, 20).build());
    }

    private void startCaptcha() {
        captchaStep = true;
        newCode();
        rebuildWidgets();
    }

    private void newCode() {
        int length = ClientState.codeLength();
        StringBuilder sb = new StringBuilder();
        charOffsets = new int[length];
        charColors = new int[length];
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            charOffsets[i] = random.nextInt(7) - 3;
            charColors[i] = COLORS[random.nextInt(COLORS.length)];
        }
        code = sb.toString();
        noise = new int[60 * 3];
        for (int i = 0; i < noise.length; i += 3) {
            noise[i] = random.nextInt(PANEL_W - 34);
            noise[i + 1] = random.nextInt(30);
            noise[i + 2] = COLORS[random.nextInt(COLORS.length)] & 0x66FFFFFF;
        }
        if (input != null) {
            input.setMaxLength(length);
            input.setValue("");
        }
    }

    private void verify() {
        if (input.getValue().trim().equalsIgnoreCase(code)) {
            playSound(SoundEvents.PLAYER_LEVELUP, 1.6F);
            if (context != null) {
                ActionGuard.grant(context);
            }
            onClose();
            return;
        }
        attempts++;
        shakeStart = System.currentTimeMillis();
        playSound(SoundEvents.VILLAGER_NO, 1.0F);
        newCode();
    }

    private void playSound(SoundEvent sound, float pitch) {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (captchaStep && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            verify();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int x = left();
        int y = top();

        // Panel with an accent header.
        graphics.fill(x - 1, y - 1, x + PANEL_W + 1, y + PANEL_H + 1, PANEL_BORDER);
        graphics.fill(x, y, x + PANEL_W, y + PANEL_H, PANEL_BG);
        graphics.fill(x, y, x + PANEL_W, y + 22, ACCENT);
        graphics.drawString(font, title, x + 10, y + 7, 0xFFFFFFFF, true);
        Component step = Component.translatable("areyousure.step", captchaStep ? 2 : 1, 2);
        graphics.drawString(font, step, x + PANEL_W - 10 - font.width(step), y + 7, 0xFFFFD6D2, false);

        // Target icon and question.
        int textX = x + 16;
        if (!icon.isEmpty()) {
            graphics.fill(x + 14, y + 30, x + 36, y + 52, 0xFF24242C);
            graphics.renderItem(icon, x + 17, y + 33);
            textX = x + 44;
        }
        Component question = context == null ? action : Component.translatable("areyousure.question", action);
        graphics.drawWordWrap(font, question, textX, y + 32, x + PANEL_W - 14 - textX, TEXT);

        if (!captchaStep) {
            graphics.drawWordWrap(font, Component.translatable("areyousure.warning"), x + 16, y + 66, PANEL_W - 32, MUTED);
            graphics.drawString(font, Component.translatable("areyousure.passinfo"), x + 16, y + 102, MUTED, false);
            return;
        }

        // Captcha box, shaking after a wrong answer.
        long elapsed = System.currentTimeMillis() - shakeStart;
        int shake = elapsed < SHAKE_MS ? (int) (Mth.sin(elapsed / 25.0F) * 4.0F * (1.0F - elapsed / (float) SHAKE_MS)) : 0;
        int boxX = x + 16 + shake;
        int boxY = y + 62;
        int boxW = PANEL_W - 32;
        graphics.fill(boxX - 1, boxY - 1, boxX + boxW + 1, boxY + 33, elapsed < SHAKE_MS ? ACCENT : PANEL_BORDER);
        graphics.fill(boxX, boxY, boxX + boxW, boxY + 32, 0xFF0B0B0F);
        for (int i = 0; i < noise.length; i += 3) {
            graphics.fill(boxX + 1 + noise[i], boxY + 1 + noise[i + 1], boxX + 3 + noise[i], boxY + 3 + noise[i + 1], noise[i + 2]);
        }
        float time = (System.currentTimeMillis() % 100000L) / 300.0F;
        int spacing = 18;
        int startX = boxX + (boxW - spacing * code.length()) / 2 + 6;
        for (int i = 0; i < code.length(); i++) {
            int wobble = Math.round(Mth.sin(time + i) * 1.5F);
            graphics.drawString(font, String.valueOf(code.charAt(i)), startX + i * spacing, boxY + 12 + charOffsets[i] + wobble, charColors[i], true);
        }
        graphics.fill(boxX + 6, boxY + 16, boxX + boxW - 6, boxY + 17, 0x88FFFFFF);

        Component status = attempts > 0
                ? Component.translatable("areyousure.captcha.wrong", attempts)
                : Component.translatable("areyousure.captcha.prompt");
        graphics.drawString(font, status, x + 16, y + PANEL_H - 70, attempts > 0 ? 0xFFFF7A70 : MUTED, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
