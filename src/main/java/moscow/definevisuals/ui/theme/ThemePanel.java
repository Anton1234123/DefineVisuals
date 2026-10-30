package moscow.definevisuals.ui.theme;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomComponent;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.framework.style.InterfaceStyleRenderer;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.ui.components.ColorPicker;
import moscow.definevisuals.ui.components.textfield.TextField;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.interfaces.IScaledResolution;
import moscow.definevisuals.utility.sounds.UiSound;

public class ThemePanel extends CustomComponent implements IScaledResolution {

    private static final float COLLAPSED_WIDTH  = 42.0F;
    private static final float EXPANDED_WIDTH   = 158.0F;
    private static final float COLLAPSED_HEIGHT = 34.0F;


    private static final float EXPANDED_HEIGHT  = 206.0F;

    private static final String TXT_THEMES       = "Темы";
    private static final String TXT_PLACEHOLDER  = "Введите название";
    private static final String TXT_COLORS       = "Цвета";
    private static final String TXT_ACCENT       = "Акцент";
    private static final String TXT_BACKGROUND   = "Фон";
    private static final String TXT_ADDITIONAL   = "Дополнительный";
    private static final String TXT_TEXT         = "Текст";
    private static final String TXT_OUTLINE      = "Обводка";
    private static final String TXT_OTHER        = "Другое";

    private final Animation expandAnimation = new Animation(250L, Easing.FIGMA_EASE_IN_OUT);
    private boolean expanded;
    private float menuAlpha = 1.0F;

    private ColorPicker colorPicker;
    private EditedColor editedColor;

    private final TextField nameField = new TextField(Fonts.REGULAR.getFont(6.5F));

    private final Animation addButtonHover = new Animation(200L, 0.0F, Easing.FIGMA_EASE_IN_OUT);

    public void setMenuAlpha(float menuAlpha) {
        this.menuAlpha = Math.max(0.0F, Math.min(1.0F, menuAlpha));
    }

    @Override
    public void onInit() {
        this.width  = COLLAPSED_WIDTH;
        this.height = COLLAPSED_HEIGHT;
        this.nameField.setPreview(TXT_PLACEHOLDER);
    }

    @Override
    protected void renderComponent(UIContext context) {
        this.expandAnimation.update(this.expanded);
        float expand = this.expandAnimation.getValue();

        this.width  = COLLAPSED_WIDTH  + (EXPANDED_WIDTH  - COLLAPSED_WIDTH)  * expand;
        this.height = COLLAPSED_HEIGHT + (EXPANDED_HEIGHT - COLLAPSED_HEIGHT) * expand;
        this.x = sr.getScaledWidth()  - this.width  - 18.0F;
        this.y = sr.getScaledHeight() - this.height - 26.0F;

        float alpha  = this.menuAlpha;
        float radius = 10.0F;

        InterfaceStyleRenderer.drawSurface(
            context,
            this.x,
            this.y,
            this.width,
            this.height,
            radius,
            BorderRadius.all(radius),
            alpha,
            0.0f
        );

        context.drawRoundedRect(
            this.x + 10.0F, this.y + 11.5F,
            11.0F, 11.0F, BorderRadius.all(5.5F),
            Colors.getAccent().withAlpha(255.0F * alpha)
        );

        if (expand <= 0.05F) {
            if (this.isHovered(context.getMouseX(), context.getMouseY())) {
                CursorUtility.set(CursorType.HAND);
            }
            this.renderColorPicker(context);
            return;
        }

        float ca = alpha * expand; // content alpha

        float headerY = this.y + 11.0F;

        context.drawText(
            Fonts.MEDIUM.getFont(8.5F), TXT_THEMES,
            this.x + 24.0F, headerY + 1.0F,
            Colors.getTextColor().withAlpha(240.0F * ca)
        );


        context.drawRect(
            this.x + 10.0F, headerY + 15.0F,
            this.width - 20.0F, 0.5F,
            Colors.getAdditionalColor().withAlpha(150.0F * ca)
        );

        float dividerY = headerY + 15.0F;
        float inputY    = dividerY + 6.0F;
        float inputH    = 16.0F;
        float addBtnW   = 16.0F;
        float inputW    = this.width - 16.0F - addBtnW - 4.0F;

        context.drawRoundedRect(
            this.x + 8.0F, inputY,
            inputW, inputH,
            BorderRadius.all(5.0F),
            Colors.getAdditionalColor().withAlpha(55.0F * ca)
        );

        this.nameField.set(
            this.x + 10.0F, inputY + 1.0F,
            inputW - 4.0F, inputH - 2.0F
        );
        this.nameField.setAlpha(ca);
        this.nameField.setTextColor(Colors.getTextColor());
        this.nameField.render(context);
        float addBtnX = this.x + 8.0F + inputW + 4.0F;
        boolean addHovered = GuiUtility.isHovered(addBtnX, inputY, addBtnW, inputH,
            context.getMouseX(), context.getMouseY());
        this.addButtonHover.update(addHovered);
        ColorRGBA addBtnColor = new ColorRGBA(65.0F, 155.0F, 245.0F)
            .mix(new ColorRGBA(90.0F, 175.0F, 255.0F), this.addButtonHover.getValue());
        context.drawRoundedRect(
            addBtnX, inputY, addBtnW, inputH,
            BorderRadius.all(5.0F),
            addBtnColor.withAlpha(230.0F * ca)
        );
        context.drawCenteredText(
            Fonts.MEDIUM.getFont(8.0F), "+",
            addBtnX + addBtnW / 2.0F, inputY + 4.0F,
            ColorRGBA.WHITE.withAlpha(240.0F * ca)
        );
        if (addHovered) CursorUtility.set(CursorType.HAND);


        float dotsRowY = inputY + inputH + 6.0F;
        float dotSize  = 7.0F;
        float dotX     = this.x + 11.0F;

        for (Theme theme : Theme.values()) {
            boolean isCurrent = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == theme;
            ColorRGBA dotColor = theme.getAccentColor();
            context.drawRoundedRect(
                dotX, dotsRowY,
                dotSize, dotSize,
                BorderRadius.all(dotSize / 2.0F),
                dotColor.withAlpha(255.0F * ca)
            );
            if (isCurrent) {
                context.drawRoundedRect(
                    dotX - 1.5F, dotsRowY - 1.5F,
                    dotSize + 3.0F, dotSize + 3.0F,
                    BorderRadius.all((dotSize + 3.0F) / 2.0F),
                    dotColor.withAlpha(80.0F * ca)
                );
            }
            dotX += dotSize + 5.0F;
        }


        float colorsLabelY = dotsRowY + dotSize + 7.0F;
        context.drawText(
            Fonts.MEDIUM.getFont(7.5F), TXT_COLORS,
            this.x + 10.0F, colorsLabelY,
            Colors.getTextColor().withAlpha(230.0F * ca)
        );

        float rowStartY = colorsLabelY + 13.0F;
        float rowStep   = 14.0F;
        this.drawColorRow(context, TXT_ACCENT,     Colors.getAccent(),           rowStartY,               ca, EditedColor.ACCENT);
        this.drawColorRow(context, TXT_BACKGROUND, Colors.getBackgroundColor(),  rowStartY + rowStep,     ca, EditedColor.BACKGROUND);
        this.drawColorRow(context, TXT_ADDITIONAL, Colors.getAdditionalColor(),  rowStartY + rowStep * 2, ca, EditedColor.ADDITIONAL);
        this.drawColorRow(context, TXT_TEXT,       Colors.getTextColor(),         rowStartY + rowStep * 3, ca, EditedColor.TEXT);
        this.drawColorRow(context, TXT_OUTLINE,    Colors.getOutlineColor(),      rowStartY + rowStep * 4, ca, EditedColor.OUTLINE);

        float otherLabelY = rowStartY + rowStep * 5 + 4.0F;
        context.drawText(
            Fonts.MEDIUM.getFont(7.5F), TXT_OTHER,
            this.x + 10.0F, otherLabelY,
            Colors.getTextColor().withAlpha(230.0F * ca)
        );

        if (this.isHovered(context.getMouseX(), context.getMouseY())) {
            CursorUtility.set(CursorType.HAND);
        }

        this.renderColorPicker(context);
    }

    private void drawColorRow(UIContext context, String label, ColorRGBA color,
                              float y, float alpha, EditedColor target) {
        boolean hovered = this.isColorRowHovered(y, context.getMouseX(), context.getMouseY());

        context.drawRoundedRect(
            this.x + 7.0F, y - 3.5F,
            this.width - 14.0F, 12.5F,
            BorderRadius.all(4.0F),
            Colors.getAdditionalColor().withAlpha((hovered ? 70.0F : 0.0F) * alpha)
        );

        context.drawText(
            Fonts.REGULAR.getFont(6.5F), label,
            this.x + 11.0F, y,
            Colors.getTextColor().withAlpha(190.0F * alpha)
        );


        float dotRightX = this.x + this.width - 18.0F;
        context.drawRoundedRect(
            dotRightX - 0.8F, y - 1.8F,
            9.6F, 9.6F, BorderRadius.all(4.8F),
            Colors.getOutlineColor().withAlpha(180.0F * alpha)
        );

        context.drawRoundedRect(
            dotRightX, y,
            8.0F, 8.0F, BorderRadius.all(4.0F),
            color.withAlpha(255.0F * alpha)
        );

        if (hovered) CursorUtility.set(CursorType.HAND);
    }

    private boolean isColorRowHovered(float rowY, double mouseX, double mouseY) {
        return GuiUtility.isHovered(
            this.x + 7.0F, rowY - 3.5F,
            this.width - 14.0F, 12.5F,
            mouseX, mouseY
        );
    }

    private void renderColorPicker(UIContext context) {
        if (this.colorPicker == null) return;
        this.colorPicker.render(context);
        if (this.editedColor != null) {
            this.applyEditedColor(this.colorPicker.built());
        }
        if (!this.colorPicker.isShowing() && this.colorPicker.getAnimation().getValue() == 0.0F) {
            this.colorPicker = null;
            this.editedColor = null;
            DefineVisuals.getInstance().getThemeManager().flushSave();
        }
    }

    private void openColorPicker(double mouseX, double mouseY, EditedColor target, ColorRGBA color, String title) {
        this.editedColor = target;
        this.colorPicker = new ColorPicker((float) mouseX, (float) mouseY, 6.0F, false, color, title);
        UiSound.WINDOW_OPEN.play();
    }

    private void applyEditedColor(ColorRGBA color) {
        Theme theme = DefineVisuals.getInstance().getThemeManager().getCurrentTheme();
        switch (this.editedColor) {
            case ACCENT     -> theme.setAccentColor(color);
            case BACKGROUND -> theme.setBackgroundColor(color);
            case ADDITIONAL -> theme.setAdditionalColor(color);
            case TEXT       -> theme.setTextColor(color);
            case OUTLINE    -> theme.setOutlineColor(color);
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {

        this.nameField.onMouseClicked(mouseX, mouseY, button);

        if (this.colorPicker != null) {
            boolean pickerHovered   = this.colorPicker.isHovered(mouseX, mouseY);
            boolean pickerWasPick   = this.colorPicker.isPick();
            this.colorPicker.onMouseClicked(mouseX, mouseY, button);
            if (pickerHovered || pickerWasPick) return;
            if (!super.isHovered(mouseX, mouseY)) {
                this.colorPicker.setShowing(false);
                UiSound.WINDOW_CLOSE.play();
            }
        }

        if (button != MouseButton.LEFT || !this.isHovered(mouseX, mouseY)) return;


        if (!this.expanded) {
            this.expanded = true;
            UiSound.WINDOW_OPEN.play();
            return;
        }

        float expand = this.expandAnimation.getValue();
        float ca     = expand;

        float dividerY   = this.y + COLLAPSED_HEIGHT - 1.0F;
        float inputY     = dividerY + 6.0F;
        float inputH     = 16.0F;
        float addBtnW    = 16.0F;
        float inputW     = this.width - 16.0F - addBtnW - 4.0F;
        float addBtnX    = this.x + 8.0F + inputW + 4.0F;


        if (GuiUtility.isHovered(addBtnX, inputY, addBtnW, inputH, mouseX, mouseY)) {
            String name = this.nameField.getBuiltText().trim();
            if (name.isBlank()) {
                UiSound.ERROR.play();
                return;
            }
            this.nameField.clear();
            DefineVisuals.getInstance().getThemeManager().flushSave();
            UiSound.SUCCESS.play();
            return;
        }

        float dotsRowY   = inputY + inputH + 6.0F;
        float dotSize    = 7.0F;
        float dotX       = this.x + 11.0F;
        for (Theme theme : Theme.values()) {
            if (GuiUtility.isHovered(dotX, dotsRowY, dotSize, dotSize, mouseX, mouseY)) {
                if (DefineVisuals.getInstance().getThemeManager().getCurrentTheme() != theme) {
                    DefineVisuals.getInstance().getThemeManager().setCurrentTheme(theme);
                    DefineVisuals.getInstance().getThemeManager().flushSave();
                    UiSound.NAVIGATION.play();
                }
                return;
            }
            dotX += dotSize + 5.0F;
        }

        float colorsLabelY = dotsRowY + dotSize + 7.0F;
        float rowStartY    = colorsLabelY + 13.0F;
        float rowStep      = 14.0F;

        if (this.isColorRowHovered(rowStartY,               mouseX, mouseY)) {
            this.openColorPicker(mouseX, mouseY, EditedColor.ACCENT,     Colors.getAccent(),          TXT_ACCENT);
        } else if (this.isColorRowHovered(rowStartY + rowStep,     mouseX, mouseY)) {
            this.openColorPicker(mouseX, mouseY, EditedColor.BACKGROUND, Colors.getBackgroundColor(), TXT_BACKGROUND);
        } else if (this.isColorRowHovered(rowStartY + rowStep * 2, mouseX, mouseY)) {
            this.openColorPicker(mouseX, mouseY, EditedColor.ADDITIONAL, Colors.getAdditionalColor(), TXT_ADDITIONAL);
        } else if (this.isColorRowHovered(rowStartY + rowStep * 3, mouseX, mouseY)) {
            this.openColorPicker(mouseX, mouseY, EditedColor.TEXT,       Colors.getTextColor(),        TXT_TEXT);
        } else if (this.isColorRowHovered(rowStartY + rowStep * 4, mouseX, mouseY)) {
            this.openColorPicker(mouseX, mouseY, EditedColor.OUTLINE,    Colors.getOutlineColor(),     TXT_OUTLINE);
        } else if (GuiUtility.isHovered(this.x, this.y, this.width, COLLAPSED_HEIGHT, mouseX, mouseY)) {
            this.expanded = false;
            UiSound.WINDOW_CLOSE.play();
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        this.nameField.onMouseReleased(mouseX, mouseY, button);
        if (this.colorPicker != null) {
            this.colorPicker.onMouseReleased(mouseX, mouseY, button);
            DefineVisuals.getInstance().getThemeManager().flushSave();
        }
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        this.nameField.onKeyPressed(keyCode, scanCode, modifiers);
        if (this.colorPicker != null) {
            if (keyCode == 256 && this.colorPicker.isShowing()) {
                this.colorPicker.setShowing(false);
                UiSound.WINDOW_CLOSE.play();
                return;
            }
            this.colorPicker.onKeyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        return super.isHovered(mouseX, mouseY)
            || (this.colorPicker != null && (this.colorPicker.isHovered(mouseX, mouseY) || this.colorPicker.isPick()));
    }

    @Override
    public boolean isHovered(float mouseX, float mouseY) {
        return this.isHovered((double) mouseX, (double) mouseY);
    }

    public boolean isExpanded() {
        return this.expanded || this.expandAnimation.getValue() > 0.05F || this.colorPicker != null;
    }

    private enum EditedColor {
        ACCENT, BACKGROUND, ADDITIONAL, TEXT, OUTLINE
    }
}
