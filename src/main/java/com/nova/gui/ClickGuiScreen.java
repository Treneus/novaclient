package com.nova.gui;

import com.nova.config.ConfigManager;
import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.module.ModuleManager;
import com.nova.modules.client.ClickGuiModule;
import com.nova.setting.BoolSetting;
import com.nova.setting.ModeSetting;
import com.nova.setting.NumberSetting;
import com.nova.setting.Setting;
import com.nova.util.Gfx;
import com.nova.util.Keys;
import com.nova.util.Theme;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

/**
 * Tabbed ClickGUI. Everything is drawn in immediate mode and mouse/keyboard state is polled from GLFW,
 * so the screen does not rely on Minecraft's input-event classes.
 *
 * Left click: toggle module / change setting.  Right click: expand settings (or step a mode backwards).
 */
public class ClickGuiScreen extends Screen {
    private static final int W = 470, H = 300, SIDE = 100, HEAD = 30, FOOT = 18, ROW = 22;

    private Category selected = Category.COMBAT;
    private final Map<Category, float[]> scrolls = new EnumMap<>(Category.class);
    private final float[] tabHover = new float[Category.values().length];

    private Module hovered;
    private Module binding;
    private NumberSetting dragging;
    private int dragX, dragW, maxScroll;
    private boolean prevL, prevR;
    private long lastNs;
    private float open;

    public ClickGuiScreen() {
        super(Component.literal("Nova"));
    }

    public boolean isListening() {
        return binding != null;
    }

    /** Called every client tick: captures the next key press when binding a module. */
    public void handleKeys() {
        if (binding == null) return;
        for (int k = GLFW.GLFW_KEY_SPACE; k <= GLFW.GLFW_KEY_LAST; k++) {
            if (k == GLFW.GLFW_KEY_RIGHT_SHIFT || !Keys.justPressed(k)) continue;
            if (k == GLFW.GLFW_KEY_BACKSPACE || k == GLFW.GLFW_KEY_DELETE) binding.key = -1;
            else if (k != GLFW.GLFW_KEY_ESCAPE) binding.key = k;
            binding = null;
            return;
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void removed() {
        ConfigManager.save();
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        float[] sc = scrolls.computeIfAbsent(selected, k -> new float[2]);
        sc[0] = Mth.clamp(sc[0] - (float) scrollY * 26f, 0f, (float) maxScroll);
        return true;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        long now = System.nanoTime();
        float dt = lastNs == 0 ? 0.016f : Math.min(0.1f, (now - lastNs) / 1_000_000_000f);
        lastNs = now;
        open = Math.min(1f, open + dt * 7f);
        float alpha = Gfx.ease(open);

        boolean left = Keys.mouseDown(0), right = Keys.mouseDown(1);
        boolean clickL = left && !prevL, clickR = right && !prevR;
        prevL = left;
        prevR = right;
        if (!left) dragging = null;

        ClickGuiModule cfg = Theme.cfg;
        int accent = Theme.accent(0f);
        int sw = g.guiWidth(), sh = g.guiHeight();
        int w = Math.min(W, sw - 16), h = Math.min(H, sh - 16);
        int x = (sw - w) / 2, y = (sh - h) / 2 + Math.round((1f - alpha) * 16f);

        if (cfg.dim.get()) g.fill(0, 0, sw, sh, Gfx.a(0x000000, 0.45f * alpha));
        for (int i = 1; i <= 5; i++) {
            Gfx.round(g, x - i, y - i, w + i * 2, h + i * 2, 6 + i, Gfx.a(0x000000, 0.07f * alpha));
        }
        Gfx.round(g, x, y, w, h, 6, Gfx.a(0x0E0F13, cfg.opacity.getFloat() * alpha));

        // ---- header ----
        Gfx.bold(g, "NOVA", x + 14, y + 11, Gfx.a(accent, alpha));
        Gfx.text(g, "client", x + 50, y + 11, Gfx.a(0x6C6D78, alpha));
        String hint = "RSHIFT close  |  right-click expands";
        Gfx.text(g, hint, x + w - 14 - Gfx.width(hint), y + 11, Gfx.a(0x5E5F6A, alpha));
        g.fill(x + 1, y + HEAD, x + w - 1, y + HEAD + 1, Gfx.a(0xFFFFFF, 0.06f * alpha));

        // ---- sidebar tabs ----
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            Category c = cats[i];
            int tx = x + 8, ty = y + HEAD + 10 + i * 24, tw = SIDE - 16, th = 20;
            boolean hov = Gfx.inside(mx, my, tx, ty, tw, th);
            tabHover[i] = Gfx.smooth(tabHover[i], (hov || c == selected) ? 1f : 0f, dt * 12f);
            if (c == selected) {
                Gfx.round(g, tx, ty, tw, th, 4, Gfx.a(accent, 0.16f * alpha));
                Gfx.round(g, tx, ty + 4, 2, th - 8, 1, Gfx.a(accent, alpha));
            } else if (tabHover[i] > 0.01f) {
                Gfx.round(g, tx, ty, tw, th, 4, Gfx.a(0xFFFFFF, 0.05f * tabHover[i] * alpha));
            }
            int col = Gfx.lerp(0xFF8A8B96, 0xFFFFFFFF, tabHover[i]);
            Gfx.text(g, c.label, tx + 10, ty + 6, Gfx.mulA(col, alpha));
            if (hov && clickL) selected = c;
        }
        g.fill(x + SIDE, y + HEAD + 1, x + SIDE + 1, y + h - FOOT, Gfx.a(0xFFFFFF, 0.06f * alpha));

        // ---- module list ----
        int cx = x + SIDE + 1, cy = y + HEAD + 1, cw = w - SIDE - 2, ch = h - HEAD - FOOT - 1;
        float[] sc = scrolls.computeIfAbsent(selected, k -> new float[2]);
        sc[0] = Mth.clamp(sc[0], 0f, (float) maxScroll);
        sc[1] += (sc[0] - sc[1]) * Math.min(1f, dt * 16f);

        int rx = cx + 8, rw = cw - 20;
        int yy = cy + 8 - Math.round(sc[1]);
        boolean inList = Gfx.inside(mx, my, cx, cy, cw, ch);
        hovered = null;

        g.enableScissor(cx, cy, cx + cw, cy + ch);
        for (Module m : ModuleManager.byCategory(selected)) {
            boolean hov = inList && Gfx.inside(mx, my, rx, yy, rw, ROW);
            m.hoverAnim = Gfx.smooth(m.hoverAnim, hov ? 1f : 0f, dt * 12f);
            m.enabledAnim = Gfx.smooth(m.enabledAnim, m.isEnabled() ? 1f : 0f, dt * 10f);
            m.expandAnim = Gfx.smooth(m.expandAnim, m.expanded ? 1f : 0f, dt * 12f);
            if (hov) hovered = m;

            Gfx.round(g, rx, yy, rw, ROW, 4, Gfx.mulA(Gfx.lerp(0xFF17181D, 0xFF1F2027, m.hoverAnim), alpha));
            if (m.toggleable && m.enabledAnim > 0.01f) {
                Gfx.round(g, rx, yy + 5, 2, ROW - 10, 1, Gfx.a(accent, alpha * m.enabledAnim));
            }
            int nameCol = Gfx.lerp(0xFF9798A3, 0xFFFFFFFF, Math.max(m.enabledAnim, m.hoverAnim * 0.7f));
            Gfx.text(g, m.name, rx + 10, yy + 7, Gfx.mulA(nameCol, alpha));

            int edge = rx + rw - 8;
            if (m.toggleable) {
                drawSwitch(g, edge - 22, yy + 6, 22, 10, m.enabledAnim, accent, alpha);
                edge -= 30;
            }
            if (m.hasSettings()) Gfx.text(g, m.expanded ? "-" : "+", edge - 4, yy + 7, Gfx.a(0x9798A3, alpha));

            if (hov) {
                if (clickL) {
                    if (m.toggleable) m.toggle();
                    else if (m.hasSettings()) m.expanded = !m.expanded;
                } else if (clickR && m.hasSettings()) {
                    m.expanded = !m.expanded;
                }
            }
            yy += ROW;

            int full = settingsHeight(m);
            int vis = Math.round(full * m.expandAnim);
            if (vis > 0 && yy + vis >= cy && yy <= cy + ch) {
                g.enableScissor(rx, yy, rx + rw, yy + vis);
                Gfx.round(g, rx, yy, rw, full, 4, Gfx.mulA(0xFF121318, alpha));
                boolean live = m.expanded && m.expandAnim > 0.97f && inList;
                int sy = yy + 4;
                for (Setting s : m.settings) {
                    if (!s.isVisible()) continue;
                    if (s instanceof BoolSetting b) {
                        boolean hs = live && Gfx.inside(mx, my, rx, sy, rw, 18);
                        b.anim = Gfx.smooth(b.anim, b.get() ? 1f : 0f, dt * 12f);
                        Gfx.text(g, b.name, rx + 10, sy + 5, Gfx.mulA(hs ? 0xFFFFFFFF : 0xFFB4B5C0, alpha));
                        drawSwitch(g, rx + rw - 8 - 18, sy + 4, 18, 9, b.anim, accent, alpha);
                        if (hs && clickL) b.toggle();
                        sy += 18;
                    } else if (s instanceof ModeSetting ms) {
                        boolean hs = live && Gfx.inside(mx, my, rx, sy, rw, 18);
                        Gfx.text(g, ms.name, rx + 10, sy + 5, Gfx.mulA(hs ? 0xFFFFFFFF : 0xFFB4B5C0, alpha));
                        String v = ms.get();
                        Gfx.text(g, v, rx + rw - 10 - Gfx.width(v), sy + 5, Gfx.a(accent, alpha));
                        if (hs && clickL) ms.next();
                        else if (hs && clickR) ms.prev();
                        sy += 18;
                    } else if (s instanceof NumberSetting ns) {
                        int trackX = rx + 10, trackW = rw - 20, trackY = sy + 17;
                        boolean hs = live && Gfx.inside(mx, my, trackX - 4, sy + 11, trackW + 8, 14);
                        if (hs && clickL) {
                            dragging = ns;
                            dragX = trackX;
                            dragW = trackW;
                        }
                        if (dragging == ns && left) {
                            ns.set(ns.min + Mth.clamp((mx - dragX) / (double) dragW, 0.0, 1.0) * (ns.max - ns.min));
                        }
                        ns.anim = Gfx.smooth(ns.anim, (float) ns.fraction(), dt * 18f);
                        Gfx.text(g, ns.name, rx + 10, sy + 3, Gfx.mulA(hs || dragging == ns ? 0xFFFFFFFF : 0xFFB4B5C0, alpha));
                        String v = ns.display();
                        Gfx.text(g, v, rx + rw - 10 - Gfx.width(v), sy + 3, Gfx.a(accent, alpha));
                        Gfx.round(g, trackX, trackY, trackW, 3, 1, Gfx.mulA(0xFF2A2B34, alpha));
                        int fillW = Math.round(trackW * ns.anim);
                        if (fillW > 0) Gfx.round(g, trackX, trackY, Math.max(2, fillW), 3, 1, Gfx.a(accent, alpha));
                        Gfx.round(g, trackX + fillW - 3, trackY - 2, 7, 7, 3, Gfx.a(0xFFFFFF, alpha));
                        sy += 26;
                    }
                }
                if (m.toggleable) {
                    boolean hs = live && Gfx.inside(mx, my, rx, sy, rw, 18);
                    boolean listening = binding == m;
                    Gfx.text(g, "Keybind", rx + 10, sy + 5, Gfx.mulA(hs ? 0xFFFFFFFF : 0xFFB4B5C0, alpha));
                    String v = listening ? "press a key..." : Keys.name(m.key);
                    Gfx.text(g, v, rx + rw - 10 - Gfx.width(v), sy + 5, listening ? Gfx.a(0xFFD166, alpha) : Gfx.a(accent, alpha));
                    if (hs && clickL) binding = listening ? null : m;
                    else if (hs && clickR) {
                        m.key = -1;
                        if (listening) binding = null;
                    }
                }
                g.disableScissor();
            }
            yy += vis + 5;
        }
        g.disableScissor();

        int total = (yy + Math.round(sc[1])) - cy + 4;
        maxScroll = Math.max(0, total - ch);
        if (maxScroll > 0) {
            int thumbH = Math.max(16, ch * ch / total);
            int thumbY = cy + Math.round((ch - thumbH) * (sc[1] / maxScroll));
            Gfx.round(g, cx + cw - 5, thumbY, 2, thumbH, 1, Gfx.a(0xFFFFFF, 0.22f * alpha));
        }

        // ---- footer ----
        g.fill(x + 1, y + h - FOOT, x + w - 1, y + h - FOOT + 1, Gfx.a(0xFFFFFF, 0.06f * alpha));
        if (hovered != null && cfg.tooltips.get()) {
            Gfx.text(g, hovered.description, x + 12, y + h - FOOT + 6, Gfx.a(0x8A8B96, alpha));
        }
        int on = 0;
        for (Module m : ModuleManager.all()) if (m.toggleable && m.isEnabled()) on++;
        String count = on + " enabled";
        Gfx.text(g, count, x + w - 12 - Gfx.width(count), y + h - FOOT + 6, Gfx.a(0x6C6D78, alpha));
    }

    private static int settingsHeight(Module m) {
        int h = 8;
        for (Setting s : m.settings) {
            if (!s.isVisible()) continue;
            h += s instanceof NumberSetting ? 26 : 18;
        }
        if (m.toggleable) h += 18;
        return h;
    }

    private static void drawSwitch(GuiGraphics g, int x, int y, int w, int h, float t, int accent, float alpha) {
        Gfx.round(g, x, y, w, h, h / 2, Gfx.mulA(Gfx.lerp(0xFF343540, accent, t), alpha));
        int k = h - 4;
        int kx = x + 2 + Math.round((w - 4 - k) * t);
        Gfx.round(g, kx, y + 2, k, k, k / 2, Gfx.a(0xFFFFFF, alpha));
    }
}
