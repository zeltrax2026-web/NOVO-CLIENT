package com.nova.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

/** NOVA's first usable NEXUS control panel. */
public final class NovaScreen extends Screen {
    private static final int PURPLE = 0xFF9B7CFF;
    private static final int CYAN = 0xFF55DDF5;
    private static final int PANEL = 0xEE101321;
    private static final int PANEL_ALT = 0xFF191D31;
    private static final String[] CATEGORIES = {"OVERVIEW", "COMBAT", "VISUALS", "MOVEMENT", "HUD", "SYSTEM"};
    private static final String[][] MODULES = {
            {"FPS Monitor", "CPS Counter", "Coordinates", "Potion Tracker"},
            {"Hit Feedback", "Combo Counter", "Armor Status", "Cooldown Display"},
            {"Cosmic Particles", "Motion Trails", "Hit Sparks", "Item Glint"},
            {"Sprint Indicator", "Speedometer", "Jump Counter", "Direction HUD"},
            {"Compact HUD", "Target Info", "Status Panel", "Notifications"},
            {"Reduced Effects", "Low Animation Mode", "Performance Preset", "Profile Manager"}
    };
    private final boolean[][] enabled = new boolean[MODULES.length];
    private final List<HitBox> hitBoxes = new ArrayList<>();
    private int category = 0;
    private int panelX, panelY, panelW, panelH;

    public NovaScreen() { super(Component.literal("NOVA NEXUS")); }

    @Override
    protected void init() {
        panelW = Math.min(560, width - 24);
        panelH = Math.min(340, height - 24);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // The game already draws the screen background before render() (1.21.6+); calling it again crashes.
        hitBoxes.clear();
        // Subtle animated starfield; deliberately capped for mobile GPUs.
        long t = System.currentTimeMillis() / 35L;
        for (int i = 0; i < 34; i++) {
            int x = (int)((i * 97L + t * (i % 3 + 1)) % Math.max(1, width));
            int y = (int)((i * 53L + (t / 2) * (i % 2 + 1)) % Math.max(1, height));
            g.fill(x, y, x + 1, y + 1, (i % 4 == 0) ? 0xAA8B78FF : 0x6676CFFF);
        }
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);
        g.fill(panelX, panelY, panelX + panelW, panelY + 2, PURPLE);
        g.fill(panelX, panelY + 2, panelX + panelW, panelY + 42, 0xFF15192A);
        g.drawString(font, "N O V A", panelX + 16, panelY + 13, 0xFFFFFFFF, false);
        g.drawString(font, "NEXUS CONTROL CENTER  /  BUILD 0.1", panelX + 100, panelY + 15, CYAN, false);
        g.drawString(font, "×", panelX + panelW - 18, panelY + 14, 0xFFFFFFFF, false);
        addHit(panelX + panelW - 28, panelY + 4, 26, 32, -1, -1);

        int navY = panelY + 55;
        int navW = Math.max(66, (panelW - 32) / CATEGORIES.length);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int x = panelX + 16 + i * navW;
            int color = i == category ? 0xFF343052 : PANEL_ALT;
            g.fill(x, navY, x + navW - 4, navY + 25, color);
            if (i == category) g.fill(x, navY + 23, x + navW - 4, navY + 25, CYAN);
            g.drawCenteredString(font, CATEGORIES[i], x + (navW - 4) / 2, navY + 9, 0xFFE8E9FF);
            addHit(x, navY, navW - 4, 25, i, -2);
        }

        int contentY = navY + 43;
        g.drawString(font, category == 0 ? "SYSTEM OVERVIEW" : CATEGORIES[category] + " MODULES", panelX + 18, contentY, PURPLE, false);
        g.drawString(font, "Tap a module to toggle its client-side setting", panelX + 18, contentY + 14, 0xFF9AA3C2, false);
        int cols = panelW < 430 ? 1 : 2;
        int gap = 10;
        int cardW = (panelW - 48 - (cols - 1) * gap) / cols;
        for (int i = 0; i < MODULES[category].length; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = panelX + 18 + col * (cardW + gap);
            int y = contentY + 39 + row * 42;
            boolean on = enabled[category][i];
            g.fill(x, y, x + cardW, y + 34, 0xFF1D2238);
            g.fill(x, y, x + 2, y + 34, on ? CYAN : 0xFF424963);
            g.drawString(font, MODULES[category][i], x + 10, y + 7, 0xFFE9EBFF, false);
            int sw = 34;
            int sx = x + cardW - sw - 8;
            g.fill(sx, y + 9, sx + sw, y + 25, on ? 0xFF285D6B : 0xFF34394E);
            g.fill(on ? sx + 19 : sx + 3, y + 11, on ? sx + 31 : sx + 15, y + 23, on ? CYAN : 0xFF9299B0);
            addHit(x, y, cardW, 34, i, category);
        }
        g.drawString(font, "RIGHT SHIFT  •  TOGGLE MENU", panelX + 18, panelY + panelH - 18, 0xFF8790B0, false);
        g.drawString(font, "NOVA  /  VISUALS  ×  COMBAT  ×  CONTROL", panelX + panelW - 230, panelY + panelH - 18, 0xFF8790B0, false);
        super.render(g, mouseX, mouseY, delta);
    }

    private void addHit(int x, int y, int w, int h, int a, int b) { hitBoxes.add(new HitBox(x, y, w, h, a, b)); }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            double mouseX = event.x();
            double mouseY = event.y();
            for (HitBox h : hitBoxes) {
                if (mouseX >= h.x && mouseX <= h.x + h.w && mouseY >= h.y && mouseY <= h.y + h.h) {
                    if (h.b == -1) { onClose(); return true; }
                    if (h.b == -2) { category = h.a; return true; }
                    if (h.b >= 0) { enabled[h.b][h.a] = !enabled[h.b][h.a]; return true; }
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override public boolean isPauseScreen() { return false; }
    private record HitBox(int x, int y, int w, int h, int a, int b) {}
              }
