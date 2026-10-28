/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.InputUtil
 */
package moscow.definevisuals.ui.menu.dropdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.Module;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.modules.modules.visuals.MenuModule;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.ui.components.ColorPicker;
import moscow.definevisuals.ui.components.animated.AnimatedText;
import moscow.definevisuals.ui.components.textfield.FieldAction;
import moscow.definevisuals.ui.components.textfield.TextField;
import moscow.definevisuals.ui.menu.MenuScreen;
import moscow.definevisuals.ui.menu.api.MenuCategory;
import moscow.definevisuals.ui.menu.dropdown.components.MenuPanel;
import moscow.definevisuals.ui.menu.dropdown.components.module.ModuleComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.BezierSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.BindSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.BooleanSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.ButtonSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.ColorSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.ModeSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.RangeSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.SliderSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.StringSettingComponent;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.math.MathUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.ScissorUtility;
import moscow.definevisuals.utility.render.batching.Batching;
import moscow.definevisuals.utility.render.batching.impl.FontBatching;
import moscow.definevisuals.utility.render.batching.impl.IconBatching;
import moscow.definevisuals.utility.render.batching.impl.RectBatching;
import moscow.definevisuals.utility.sounds.ClientSounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;

public class DropDownScreen
extends MenuScreen
implements IMinecraft {
    private final Animation searchAnimation = new Animation(300L, Easing.BAKEK);
    private final Animation appendingAnim = new Animation(300L, Easing.BAKEK);
    private boolean closing;
    private List<MenuPanel> panels = new ArrayList<MenuPanel>();
    private float panelWidth;
    private float panelHeight;
    private String desc = "";
    private AnimatedText descText;
    private final List<ColorPicker> colorPickers = new ArrayList<ColorPicker>();
    private TextField searchField;
    protected void init() {
        this.descText = new AnimatedText(Fonts.REGULAR.getFont(10.0f), 10.0f, 300L, Easing.BAKEK).centered();
        this.closing = false;
        this.panelWidth = 115.0f;
        this.panelHeight = 240.0f;
        this.panels = Arrays.stream(MenuCategory.values())
            .filter(cat -> cat == MenuCategory.PLAYER || cat == MenuCategory.VISUALS || cat == MenuCategory.UTILS)
            .map(MenuPanel::new)
            .toList();
        for (MenuPanel panel : this.panels) {
            panel.setWidth(this.panelWidth);
            panel.setHeight(this.panelHeight);
            panel.onInit();
        }
        this.searchField = new TextField(Fonts.REGULAR.getFont(12.0f));
        HashMap<String, FieldAction> append = new HashMap<String, FieldAction>();
        for (Module module : DefineVisuals.getInstance().getModuleManager().getModules()) {
            FieldAction action = new FieldAction(module::toggle, () -> this.panels.forEach(panel -> panel.getModuleComponents().stream().filter(component -> component.getModule() == module).forEach(ModuleComponent::open)));
            append.put(module.getName().replace(" ", ""), action);
            append.put(module.getName(), action);
        }
        this.searchField.setAppend(append);
        super.init();
    }

    public void tick() {
        this.handleMovementKeys();
        super.tick();
    }

    @Override
    public void render(UIContext context) {
        this.menuAnimation.setEasing(Easing.LINEAR);
        this.menuAnimation.update(this.isClosing() ? 0.0f : 1.0f);
        this.menuAnimation.setDuration(this.isClosing() ? 300L : 500L);
        this.desc = "";
        float spacing = 10.0f;
        float x = ((float)this.width - (this.panelWidth + spacing) * (float)this.panels.size() + spacing) / 2.0f;
        float y = ((float)this.height - this.panelHeight) / 2.0f;
        context.pushMatrix();
        float offset = 0.0f;
        for (MenuPanel menuPanel : this.panels) {
            menuPanel.setX(MathUtility.interpolate(x + offset, (float)this.width / 2.0f - this.panelWidth / 2.0f, this.closing ? (double)(1.0f - this.menuAnimation.getValue()) : 0.0));
            menuPanel.setY(y);
            menuPanel.setWidth(this.panelWidth);
            menuPanel.setHeight(this.panelHeight);
            offset += this.panelWidth + spacing;
        }
        for (MenuPanel menuPanel : this.panels) {
            menuPanel.renderBlur(context);
            offset += this.panelWidth + spacing;
        }
        for (MenuPanel menuPanel : this.panels) {
            menuPanel.render(context);
        }
        IconBatching icon = new IconBatching(VertexFormats.POSITION_TEXTURE_COLOR, context.getMatrices());
        for (MenuPanel panel : this.panels) {
            panel.drawType(context);
        }
        ((Batching)icon).draw();
        for (MenuPanel panel : this.panels) {
            this.scissor(context, panel, () -> {
                FontBatching font = new FontBatching(VertexFormats.POSITION_TEXTURE_COLOR, Fonts.REGULAR);
                panel.drawRegular8(context);
                ((Batching)font).draw();
                IconBatching icon1 = new IconBatching(VertexFormats.POSITION_TEXTURE_COLOR, context.getMatrices());
                panel.drawIcons(context);
                ((Batching)icon1).draw();
                RectBatching split = new RectBatching(VertexFormats.POSITION_COLOR, context.getMatrices());
                panel.drawSplit(context);
                ((Batching)split).draw();
            });
        }
        context.popMatrix();
        if (this.menuAnimation.getValue() < 0.5f) {
            this.desc = "";
        }
        this.searchAnimation.update(this.searchField.isFocused());
        float f = this.menuAnimation.getValue() * this.searchAnimation.getValue();
        if (f > 0.0f) {
            if (Interface.showMinimalizm()) {
                context.drawBlurredRect(this.searchField.getX(), this.searchField.getY(), this.searchField.getWidth(), this.searchField.getHeight(), 45.0f, BorderRadius.all(6.0f), ColorRGBA.WHITE.withAlpha(255.0f * f));
            }
            if (Interface.showGlass()) {
                context.drawLiquidGlass(this.searchField.getX(), this.searchField.getY(), this.searchField.getWidth(), this.searchField.getHeight(), 2.0f, 0.08f, BorderRadius.all(6.0f), ColorRGBA.WHITE.withAlpha(255.0f * f));
            }
            boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
            context.drawRoundedRect(this.searchField.getX(), this.searchField.getY(), this.searchField.getWidth(), this.searchField.getHeight(), BorderRadius.all(6.0f), Colors.getBackgroundColor().withAlpha(255.0f * (dark ? 0.9f - 0.7f * Interface.glass() : 0.7f) * f));
            this.searchField.set((float)this.width / 2.0f - this.searchField.getWidth() / 2.0f, (float)(this.height - 20) - 20.0f * f, 100.0f, 20.0f);
            this.searchField.setAlpha(f);
            this.searchField.setTextColor(Colors.getTextColor());
            this.searchField.render(context);
            this.appendingAnim.update(!this.searchField.getAppending().isBlank());
            context.drawCenteredText(Fonts.MEDIUM.getFont(11.0f), Localizator.translate("search.tooltip.tab"), (float)this.width / 2.0f, (float)(this.height - 65) - 10.0f * f * this.appendingAnim.getValue(), ColorRGBA.WHITE.withAlpha(150.0f * f * this.appendingAnim.getValue()));
            context.drawCenteredText(Fonts.MEDIUM.getFont(11.0f), Localizator.translate("search.tooltip.enter"), (float)this.width / 2.0f, (float)(this.height - 50) - 10.0f * f * this.appendingAnim.getValue(), ColorRGBA.WHITE.withAlpha(150.0f * f * this.appendingAnim.getValue()));
        } else {
            this.searchField.clear();
        }
        context.drawCenteredText(Fonts.MEDIUM.getFont(11.0f), Localizator.translate("search.tooltip"), (float)this.width / 2.0f, (float)(this.height - 20) - 10.0f * this.menuAnimation.getValue() * (1.0f - this.searchAnimation.getValue()), ColorRGBA.WHITE.withAlpha(150.0f * this.menuAnimation.getValue() * (1.0f - this.searchAnimation.getValue())));
        this.descText.pos((float)this.width / 2.0f, (float)this.height / 2.0f - 150.0f);
        if (!this.desc.contains(".description")) {
            this.descText.update(this.desc);
            this.descText.render(context);
        }
        for (ColorPicker colorPicker : this.colorPickers) {
            colorPicker.render(context);
            if (DropDownScreen.mc.currentScreen instanceof DropDownScreen) continue;
            colorPicker.setShowing(false);
        }
        this.colorPickers.removeIf(popup -> popup.getAnimation().getValue() == 0.0f && !popup.isShowing());
        // RussianRoulette russianRoulette = DefineVisuals.getInstance().getModuleManager().getModule(RussianRoulette.class);
        // if (russianRoulette.isEnabled()) {
        //    if (russianRoulette.getQrTexture() == null) {
        //       return;
        //    }
        //    if (russianRoulette.getQrAnimation().getValue() == 0.0f && russianRoulette.isQrRemoving()) {
        //       return;
        //    }
        //    float scale = 180.0f;
        //    float xQR = ((float)mc.getWindow().getScaledWidth() - scale) / 2.0f;
        //    float yQR = ((float)mc.getWindow().getScaledHeight() - scale) / 2.0f;
        //    context.drawTexture(russianRoulette.getQrTexture(), xQR, yQR, scale, scale, Colors.WHITE.withAlpha((int)(255.0f * russianRoulette.getQrAnimation().getValue())));
        // }
    }
    private void handleMovementKeys() {
        KeyBinding[] movementKeys;
        if (DropDownScreen.mc.player == null || this.isTyping()) {
            return;
        }
        long windowHandle = mc.getWindow().getHandle();
        for (KeyBinding key : movementKeys = new KeyBinding[]{DropDownScreen.mc.options.forwardKey, DropDownScreen.mc.options.backKey, DropDownScreen.mc.options.leftKey, DropDownScreen.mc.options.rightKey, DropDownScreen.mc.options.jumpKey}) {
            int keyCode = InputUtil.fromTranslationKey((String)key.getBoundKeyTranslationKey()).getCode();
            key.setPressed(InputUtil.isKeyPressed((long)windowHandle, (int)keyCode));
        }
        if (DropDownScreen.mc.player.getAbilities().flying) {
            int keyCode = InputUtil.fromTranslationKey((String)DropDownScreen.mc.options.sneakKey.getBoundKeyTranslationKey()).getCode();
            DropDownScreen.mc.options.sneakKey.setPressed(InputUtil.isKeyPressed((long)windowHandle, (int)keyCode));
        }
    }

    private boolean isTyping() {
        return DropDownScreen.mc.currentScreen != null && TextField.LAST_FIELD != null && TextField.LAST_FIELD.isFocused();
    }

    public boolean isBindingModule() {
        return this.panels.stream().flatMap(panel -> panel.getModuleComponents().stream()).anyMatch(ModuleComponent::isBindingMode);
    }

    private void scissor(UIContext context, MenuPanel panel, Runnable runnable) {
        panel.scale(context);
        panel.push(context);
        runnable.run();
        ScissorUtility.pop();
        RenderUtility.end(context.getMatrices());
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (DefineVisuals.getInstance().getHud().getIsland().handleClick((float)mouseX, (float)mouseY, button.getButtonIndex())) {
            return;
        }
        for (ColorPicker colorPicker : this.colorPickers) {
            boolean isPick = colorPicker.isPick();
            colorPicker.onMouseClicked(mouseX, mouseY, button);
            if (colorPicker.isHovered(mouseX, mouseY) || isPick) {
                return;
            }
            colorPicker.setShowing(false);
        }
        for (MenuPanel panel : this.panels) {
            if (!panel.isHovered(mouseX, mouseY)) continue;
            panel.onMouseClicked(mouseX, mouseY, button);
        }
        if (this.searchField.isFocused() && button != MouseButton.MIDDLE) {
            this.searchField.onMouseClicked(mouseX, mouseY, button);
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        for (ColorPicker colorPicker : this.colorPickers) {
            colorPicker.onMouseReleased(mouseX, mouseY, button);
        }
        for (MenuPanel panel : this.panels) {
            panel.onMouseReleased(mouseX, mouseY, button);
        }
        if (this.searchField.isFocused()) {
            this.searchField.onMouseReleased(mouseX, mouseY, button);
        }
        super.onMouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (MenuPanel panel : this.panels) {
            panel.onScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ColorPicker colorPicker : this.colorPickers) {
            colorPicker.onKeyPressed(keyCode, scanCode, modifiers);
        }
        if (this.searchField != null && !this.searchField.isFocused() && Screen.hasControlDown() && keyCode == 70) {
            this.searchField.setFocused(true);
        }
        for (MenuPanel panel : this.panels) {
            panel.onKeyPressed(keyCode, scanCode, modifiers);
        }
        if (this.searchField.isFocused() && !this.isBindingModule()) {
            this.searchField.onKeyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    public boolean charTyped(char chr, int modifiers) {
        if (this.searchField.isFocused() && !this.isBindingModule()) {
            this.searchField.charTyped(chr, modifiers);
        }
        for (MenuPanel panel : this.panels) {
            panel.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }
    public void close() {
        this.closing = true;
        DefineVisuals.getInstance().getModuleManager().getModule(MenuModule.class).disable();
        Sounds soundsModule = DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class);
        if (soundsModule.isEnabled()) {
            ClientSounds.CLICKGUI_OPEN.play(soundsModule.getVolume().getCurrentValue(), 1.0f);
        }
        // Async file save to prevent freeze
        CompletableFuture.runAsync(() -> {
            DefineVisuals.getInstance().getFileManager().writeFile("client");
            if (DefineVisuals.getInstance().getConfigManager().getCurrent() != null) {
                DefineVisuals.getInstance().getConfigManager().getCurrent().save();
            }
        });
        if (TextField.LAST_FIELD != null) {
            TextField.LAST_FIELD.setFocused(false);
        }
        super.close();
    }

    public boolean shouldPause() {
        return false;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Empty to allow world rendering behind GUI (HUD-like behavior)
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Generated
    public Animation getSearchAnimation() {
        return this.searchAnimation;
    }

    @Generated
    public Animation getAppendingAnim() {
        return this.appendingAnim;
    }

    @Override
    @Generated
    public boolean isClosing() {
        return this.closing;
    }

    @Generated
    public List<MenuPanel> getPanels() {
        return this.panels;
    }

    @Generated
    public float getPanelWidth() {
        return this.panelWidth;
    }

    @Generated
    public float getPanelHeight() {
        return this.panelHeight;
    }

    @Generated
    public String getDesc() {
        return this.desc;
    }

    @Generated
    public AnimatedText getDescText() {
        return this.descText;
    }

    @Generated
    public List<ColorPicker> getColorPickers() {
        return this.colorPickers;
    }

    @Override
    @Generated
    public void setClosing(boolean closing) {
        this.closing = closing;
    }

    @Generated
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Generated
    public TextField getSearchField() {
        return this.searchField;
    }

    static {
        new MenuPanel(null);
        new BezierSettingComponent(null, null);
        new BindSettingComponent(null, null);
        new BooleanSettingComponent(null, null);
        new ModeSettingComponent(null, null);
        new ButtonSettingComponent(null, null);
        new ColorSettingComponent(null, null);
        new StringSettingComponent(null, null);
        new RangeSettingComponent(null, null);
        new SliderSettingComponent(null, null);
    }
}

