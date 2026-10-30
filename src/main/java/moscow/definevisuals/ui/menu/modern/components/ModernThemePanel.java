package moscow.definevisuals.ui.menu.modern.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.notifications.NotificationType;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.ui.components.ColorPicker;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.gui.ScrollHandler;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.render.ScissorUtility;
import moscow.definevisuals.utility.render.obj.CustomSprite;
import moscow.definevisuals.utility.sounds.ClientSounds;

public class ModernThemePanel implements IMinecraft {

    public static class ThemePreset {
        private final String name;
        private ColorRGBA color1;
        private ColorRGBA color2;
        private final Animation hoverAnim = new Animation(200L, Easing.QUAD_OUT);
        private final Animation selectAnim = new Animation(250L, Easing.FIGMA_EASE_IN_OUT);

        public ThemePreset(String name, ColorRGBA color1, ColorRGBA color2) {
            this.name = name;
            this.color1 = color1;
            this.color2 = color2;
        }

        public String getName() {
            return this.name;
        }

        public ColorRGBA getColor1() {
            return this.color1;
        }

        public void setColor1(ColorRGBA color1) {
            this.color1 = color1;
        }

        public ColorRGBA getColor2() {
            return this.color2;
        }

        public void setColor2(ColorRGBA color2) {
            this.color2 = color2;
        }

        public Animation getHoverAnim() {
            return this.hoverAnim;
        }

        public Animation getSelectAnim() {
            return this.selectAnim;
        }
    }

    private final List<ThemePreset> presets = new ArrayList<>(List.of(
            new ThemePreset("Клиентская", new ColorRGBA(155.0f, 163.0f, 255.0f), new ColorRGBA(184.0f, 150.0f, 255.0f)),
            new ThemePreset("Голубо-розовая", new ColorRGBA(0.0f, 229.0f, 255.0f), new ColorRGBA(224.0f, 64.0f, 251.0f)),
            new ThemePreset("Сине-зелёная", new ColorRGBA(0.0f, 230.0f, 118.0f), new ColorRGBA(0.0f, 112.0f, 243.0f)),
            new ThemePreset("Тёмно-синяя", new ColorRGBA(61.0f, 90.0f, 254.0f), new ColorRGBA(26.0f, 35.0f, 126.0f)),
            new ThemePreset("Чёрно-белая", new ColorRGBA(255.0f, 255.0f, 255.0f), new ColorRGBA(117.0f, 117.0f, 117.0f)),
            new ThemePreset("Водяная", new ColorRGBA(66.0f, 165.0f, 245.0f), new ColorRGBA(21.0f, 101.0f, 192.0f)),
            new ThemePreset("Фиолетовая", new ColorRGBA(156.0f, 39.0f, 176.0f), new ColorRGBA(74.0f, 20.0f, 140.0f)),
            new ThemePreset("Лавандовая", new ColorRGBA(206.0f, 147.0f, 216.0f), new ColorRGBA(123.0f, 31.0f, 162.0f)),
            new ThemePreset("Рассветная", new ColorRGBA(255.0f, 112.0f, 67.0f), new ColorRGBA(236.0f, 64.0f, 122.0f)),
            new ThemePreset("Закатная", new ColorRGBA(255.0f, 152.0f, 0.0f), new ColorRGBA(126.0f, 87.0f, 194.0f)),
            new ThemePreset("Весенняя", new ColorRGBA(192.0f, 202.0f, 51.0f), new ColorRGBA(124.0f, 179.0f, 66.0f)),
            new ThemePreset("Летняя", new ColorRGBA(255.0f, 238.0f, 88.0f), new ColorRGBA(255.0f, 167.0f, 38.0f)),
            new ThemePreset("Зимняя", new ColorRGBA(255.0f, 255.0f, 255.0f), new ColorRGBA(207.0f, 216.0f, 220.0f)),
            new ThemePreset("Полуночная", new ColorRGBA(38.0f, 50.0f, 56.0f), new ColorRGBA(69.0f, 90.0f, 100.0f))
    ));

    private ThemePreset selectedPreset = this.presets.get(0);
    private final ScrollHandler scrollHandler = new ScrollHandler();

    private ColorPicker colorPicker;
    private ThemePreset editingPreset;
    private int editingColorIndex = 1; // 1 for color1, 2 for color2

    public ModernThemePanel() {
        this.selectedPreset = this.presets.get(0);
    }

    public boolean isPickerOpen() {
        return this.colorPicker != null && this.colorPicker.isShowing();
    }

    public void render(UIContext context, float x, float y, float width, float height, float menuAlpha) {
        this.scrollHandler.update();

        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        ColorRGBA accent = Colors.getAccent();
        ColorRGBA textColor = Colors.getTextColor();

        // 1. Header Bar
        float headerY = y + 14.0f;
        float headerX = x + 12.0f;

        // Header Title: [Palette Icon] Темы
        context.drawSprite(CustomSprite.THEMES, headerX, headerY + 1.0f, 9.0f, 9.0f, textColor);
        context.drawText(Fonts.SEMIBOLD.getFont(9.0f), "Темы", headerX + 13.0f, headerY + 1.0f, textColor);

        // Header Action: Theme Mode Toggle Button (Тёмная / Светлая)
        float modeBtnW = 54.0f;
        float modeBtnH = 14.0f;
        float modeBtnX = x + width - 14.0f - modeBtnW;
        float modeBtnY = headerY - 1.0f;
        boolean modeHov = GuiUtility.isHovered((double)modeBtnX, (double)modeBtnY, modeBtnW, modeBtnH, (double)context.getMouseX(), (double)context.getMouseY());
        if (modeHov && !this.isPickerOpen()) CursorUtility.set(CursorType.HAND);

        ColorRGBA modeBg = modeHov ? accent.mulAlpha(0.3f) : Colors.getBackgroundColor().mulAlpha(dark ? 0.4f : 0.25f);
        context.drawSquircle(modeBtnX, modeBtnY, modeBtnW, modeBtnH, 3.0f, BorderRadius.all(3.0f), modeBg);
        String modeText = dark ? "Тёмная" : "Светлая";
        context.drawCenteredText(Fonts.MEDIUM.getFont(5.5f), modeText, modeBtnX + modeBtnW / 2.0f, modeBtnY + 4.5f, modeHov ? accent : textColor.mulAlpha(0.85f));

        // 2. Subheader: "Цветовые палитры"
        float subHeaderY = headerY + 20.0f;
        context.drawText(Fonts.MEDIUM.getFont(6.0f), "Цветовые палитры", headerX, subHeaderY, textColor.mulAlpha(0.35f));

        // 3. Grid of Theme Cards (2 Columns)
        float listY = subHeaderY + 11.0f;
        float listHeight = height - (listY - y) - 10.0f;
        float totalWidth = width - 24.0f;
        float colGap = 6.0f;
        float cardWidth = (totalWidth - colGap) / 2.0f;
        float cardHeight = 28.0f;
        float rowGap = 5.0f;

        ScissorUtility.push(context.getMatrices(), headerX - 2.0f, listY, totalWidth + 4.0f, listHeight);

        float currentScroll = (float)this.scrollHandler.getValue();

        for (int i = 0; i < this.presets.size(); i++) {
            ThemePreset preset = this.presets.get(i);
            int col = i % 2;
            int row = i / 2;

            float cardX = headerX + col * (cardWidth + colGap);
            float cardY = listY - currentScroll + row * (cardHeight + rowGap);

            boolean isHovered = !this.isPickerOpen() && GuiUtility.isHovered((double)cardX, (double)cardY, cardWidth, cardHeight, (double)context.getMouseX(), (double)context.getMouseY());
            preset.getHoverAnim().update(isHovered);
            if (isHovered) {
                CursorUtility.set(CursorType.HAND);
            }

            boolean isSelected = (this.selectedPreset == preset);
            preset.getSelectAnim().update(isSelected);

            // Card background (matching module grid style without outline)
            float glass = Interface.glass();
            ColorRGBA base = (dark ? Colors.getBackgroundColor() : Colors.getAdditionalColor())
                    .withAlpha(255.0f * (dark ? 0.16f + 0.14f * Interface.minimalizm() : 0.12f + 0.12f * Interface.minimalizm()));
            ColorRGBA cardBg = base
                    .mix(accent.withAlpha(255.0f * (0.25f + 0.15f * (1.0f - glass))), preset.getSelectAnim().getValue() * 0.9f)
                    .mix(Colors.getTextColor().withAlpha(20.0f), preset.getHoverAnim().getValue() * 0.35f);

            BorderRadius cardRadius = BorderRadius.all(4.0f);
            context.drawSquircle(cardX, cardY, cardWidth, cardHeight, 3.0f, cardRadius, cardBg);

            // Theme Name
            context.drawText(Fonts.MEDIUM.getFont(6.5f), preset.getName(), cardX + 7.0f, cardY + 5.0f, textColor);

            // Color 1 Dot
            float dot1X = cardX + 7.0f;
            float dot1Y = cardY + 16.0f;
            float dotSize = 8.0f;
            boolean dot1Hov = !this.isPickerOpen() && GuiUtility.isHovered((double)dot1X, (double)dot1Y, dotSize, dotSize, (double)context.getMouseX(), (double)context.getMouseY());
            context.drawRoundedRect(dot1X, dot1Y, dotSize, dotSize, BorderRadius.all(2.5f), preset.getColor1());
            if (dot1Hov) {
                context.drawRoundedBorder(dot1X - 0.5f, dot1Y - 0.5f, dotSize + 1.0f, dotSize + 1.0f, 0.8f, BorderRadius.all(3.0f), Colors.WHITE);
            }

            // Color 2 Dot
            float dot2X = dot1X + dotSize + 4.0f;
            float dot2Y = dot1Y;
            boolean dot2Hov = !this.isPickerOpen() && GuiUtility.isHovered((double)dot2X, (double)dot2Y, dotSize, dotSize, (double)context.getMouseX(), (double)context.getMouseY());
            context.drawRoundedRect(dot2X, dot2Y, dotSize, dotSize, BorderRadius.all(2.5f), preset.getColor2());
            if (dot2Hov) {
                context.drawRoundedBorder(dot2X - 0.5f, dot2Y - 0.5f, dotSize + 1.0f, dotSize + 1.0f, 0.8f, BorderRadius.all(3.0f), Colors.WHITE);
            }
        }

        int totalRows = (this.presets.size() + 1) / 2;
        float totalListHeight = totalRows * (cardHeight + rowGap);
        float maxScroll = -Math.max(0.0f, totalListHeight - listHeight);
        this.scrollHandler.setMax(maxScroll);

        ScissorUtility.pop();

        // 4. Render ColorPicker if open
        if (this.colorPicker != null) {
            this.colorPicker.render(context);
            if (this.editingPreset != null) {
                ColorRGBA newColor = this.colorPicker.built();
                if (this.editingColorIndex == 1) {
                    this.editingPreset.setColor1(newColor);
                } else {
                    this.editingPreset.setColor2(newColor);
                }

                if (this.selectedPreset == this.editingPreset) {
                    Theme curTheme = DefineVisuals.getInstance().getThemeManager().getCurrentTheme();
                    if (this.editingColorIndex == 1) {
                        curTheme.setAccentColor(newColor);
                    } else {
                        curTheme.setAdditionalColor(newColor);
                    }
                }
            }

            if (!this.colorPicker.isShowing() && this.colorPicker.getAnimation().getValue() == 0.0f) {
                this.colorPicker = null;
                this.editingPreset = null;
                DefineVisuals.getInstance().getThemeManager().flushSave();
            }
        }
    }

    public void onMouseClicked(double mouseX, double mouseY, MouseButton button, float x, float y, float width, float height) {
        if (this.colorPicker != null) {
            boolean pickerHovered = this.colorPicker.isHovered(mouseX, mouseY);
            boolean pickerWasPick = this.colorPicker.isPick();
            this.colorPicker.onMouseClicked(mouseX, mouseY, button);
            if (pickerHovered || pickerWasPick) {
                return;
            }
            this.colorPicker.setShowing(false);
            return;
        }

        float headerY = y + 14.0f;
        float headerX = x + 12.0f;

        // Mode switch button
        float modeBtnW = 54.0f;
        float modeBtnH = 14.0f;
        float modeBtnX = x + width - 14.0f - modeBtnW;
        float modeBtnY = headerY - 1.0f;
        if (GuiUtility.isHovered((double)modeBtnX, (double)modeBtnY, modeBtnW, modeBtnH, mouseX, mouseY)) {
            DefineVisuals.getInstance().getThemeManager().switchTheme();
            this.playClickSound();
            return;
        }

        // Cards click
        float subHeaderY = headerY + 20.0f;
        float listY = subHeaderY + 11.0f;
        float totalWidth = width - 24.0f;
        float colGap = 6.0f;
        float cardWidth = (totalWidth - colGap) / 2.0f;
        float cardHeight = 28.0f;
        float rowGap = 5.0f;
        float currentScroll = (float)this.scrollHandler.getValue();

        for (int i = 0; i < this.presets.size(); i++) {
            ThemePreset preset = this.presets.get(i);
            int col = i % 2;
            int row = i / 2;

            float cardX = headerX + col * (cardWidth + colGap);
            float cardY = listY - currentScroll + row * (cardHeight + rowGap);

            if (GuiUtility.isHovered((double)cardX, (double)cardY, cardWidth, cardHeight, mouseX, mouseY)) {
                float dot1X = cardX + 7.0f;
                float dot1Y = cardY + 16.0f;
                float dotSize = 8.0f;
                float dot2X = dot1X + dotSize + 4.0f;
                float dot2Y = dot1Y;

                // Right click on Color 1
                if (button == MouseButton.RIGHT && GuiUtility.isHovered((double)dot1X, (double)dot1Y, dotSize, dotSize, mouseX, mouseY)) {
                    this.openColorPicker(mouseX, mouseY, preset, 1);
                    this.playClickSound();
                    return;
                }

                // Right click on Color 2
                if (button == MouseButton.RIGHT && GuiUtility.isHovered((double)dot2X, (double)dot2Y, dotSize, dotSize, mouseX, mouseY)) {
                    this.openColorPicker(mouseX, mouseY, preset, 2);
                    this.playClickSound();
                    return;
                }

                if (button == MouseButton.RIGHT) {
                    // Right click anywhere on the card -> open Color 1 picker
                    this.openColorPicker(mouseX, mouseY, preset, 1);
                    this.playClickSound();
                    return;
                }

                // Left click -> apply preset
                this.selectThemePreset(preset);
                this.playClickSound();
                return;
            }
        }
    }

    private void selectThemePreset(ThemePreset preset) {
        this.selectedPreset = preset;
        Theme currentTheme = DefineVisuals.getInstance().getThemeManager().getCurrentTheme();
        currentTheme.setAccentColor(preset.getColor1());
        currentTheme.setAdditionalColor(preset.getColor2());
        DefineVisuals.getInstance().getThemeManager().flushSave();
        DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.SUCCESS, "Выбрана тема: " + preset.getName());
    }

    private void openColorPicker(double mouseX, double mouseY, ThemePreset preset, int colorIndex) {
        this.editingPreset = preset;
        this.editingColorIndex = colorIndex;
        ColorRGBA color = (colorIndex == 1) ? preset.getColor1() : preset.getColor2();
        String title = preset.getName() + " #" + colorIndex;
        this.colorPicker = new ColorPicker((float)mouseX, (float)mouseY, 0.0f, false, color, title);
    }

    private void playClickSound() {
        Sounds sounds = DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class);
        if (sounds != null && sounds.isEnabled()) {
            ClientSounds.MODULE.play(sounds.getVolume().getCurrentValue(), 1.0f);
        }
    }

    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        if (this.colorPicker != null) {
            this.colorPicker.onMouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.colorPicker != null && this.colorPicker.isShowing()) {
            return false;
        }
        this.scrollHandler.scroll(verticalAmount);
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.colorPicker != null && this.colorPicker.isShowing()) {
            if (keyCode == 256) { // Escape
                this.colorPicker.setShowing(false);
                return true;
            }
            this.colorPicker.onKeyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return false;
    }
}
