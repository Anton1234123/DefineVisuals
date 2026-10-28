package moscow.definevisuals.ui.menu.modern;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.modules.Module;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.modules.modules.visuals.MenuModule;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.ui.components.ColorPicker;
import moscow.definevisuals.ui.components.textfield.FieldAction;
import moscow.definevisuals.ui.components.textfield.TextField;
import moscow.definevisuals.ui.menu.MenuScreen;
import moscow.definevisuals.ui.menu.api.MenuCategory;
import moscow.definevisuals.ui.menu.dropdown.components.MenuPanel;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.BezierSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.BindSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.BooleanSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.ButtonSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.ColorSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.ModeSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.RangeSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.SliderSettingComponent;
import moscow.definevisuals.ui.menu.dropdown.components.settings.impl.StringSettingComponent;
import moscow.definevisuals.ui.menu.modern.components.ModernConfigPanel;
import moscow.definevisuals.ui.menu.modern.components.ModernEventsPanel;
import moscow.definevisuals.ui.menu.modern.components.ModernThemePanel;
import moscow.definevisuals.ui.menu.modern.components.ModernModule;
import moscow.definevisuals.ui.menu.modern.components.ModernSettings;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.gui.ScrollHandler;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.interfaces.IScaledResolution;
import moscow.definevisuals.utility.render.DrawUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.ScissorUtility;
import moscow.definevisuals.utility.render.batching.impl.FadeOutBatching;
import moscow.definevisuals.utility.render.batching.impl.FontBatching;
import moscow.definevisuals.utility.render.batching.impl.IconBatching;
import moscow.definevisuals.utility.render.batching.impl.RoundedRectBatching;
import moscow.definevisuals.utility.render.batching.impl.SquircleBatching;
import moscow.definevisuals.utility.render.obj.Rect;
import moscow.definevisuals.utility.render.penis.PenisPlayer;
import moscow.definevisuals.utility.sounds.ClientSounds;
import moscow.definevisuals.utility.time.Timer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;

public class ModernScreen
extends MenuScreen
implements IMinecraft,
IScaledResolution {
    private final Rect menuWindow;
    private float dragX;
    private float dragY;
    private boolean drag;
    private final ScrollHandler scrollHandler = new ScrollHandler();
    private MenuCategory current = MenuCategory.PLAYER;
    private MenuCategory parentView = null;
    private final List<ColorPicker> colorPickers = new LinkedList<ColorPicker>();
    private final List<ModernCategory> categories = new ArrayList<ModernCategory>();
    private final List<ModernCategory> topLevelCategories = new ArrayList<ModernCategory>();
    private final List<ModernSettings> windows = new LinkedList<ModernSettings>();
    private final Animation currentCategory = new Animation(300L, Easing.BAKEK_SMALLER);
    private final ModernConfigPanel configPanel = new ModernConfigPanel();
    private final ModernEventsPanel eventsPanel = new ModernEventsPanel();
    private final ModernThemePanel themePanel = new ModernThemePanel();
    private final TextField searchField;
    private final PenisPlayer searchPenis;
    private boolean prevFocused;
    Timer timer = new Timer();

    public ModernScreen() {
        float width = 400.0f;
        float height = 280.0f;
        this.menuWindow = new Rect((float)mc.getWindow().getScaledWidth() / 2.0f - width / 2.0f, (float)mc.getWindow().getScaledHeight() / 2.0f - height / 2.0f, width, height);
        this.categories.clear();
        this.topLevelCategories.clear();
        for (MenuCategory category : MenuCategory.values()) {
            if (category == MenuCategory.OTHER || category == MenuCategory.INTERFACE) continue;
            LinkedList<ModernModule> filteredModules = new LinkedList<ModernModule>();
            ModernCategory modern = new ModernCategory(category, filteredModules);
            try {
                modern.setPenis(new PenisPlayer(DefineVisuals.id("penises/" + category.getName().toLowerCase() + ".penis")));
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            this.categories.add(modern);
            if (!category.isSubCategory()) {
                this.topLevelCategories.add(modern);
            }
            filteredModules.addAll(DefineVisuals.getInstance().getModuleManager().getModules().stream().sorted(Comparator.comparing(Module::getName)).filter(module -> module.getCategory().equals((Object)category.getCategory()) || (category.getCategory() == ModuleCategory.UTILS && module.getCategory() == ModuleCategory.OTHER)).map(module -> new ModernModule((Module)module, modern)).toList());
        }
        this.searchField = new TextField(Fonts.MEDIUM.getFont(6.0f));
        HashMap<String, FieldAction> append = new HashMap<String, FieldAction>();
        for (Module module2 : DefineVisuals.getInstance().getModuleManager().getModules()) {
            FieldAction action = new FieldAction(module2::toggle, () -> this.categories.forEach(panel -> panel.getModules().stream().filter(component -> component.getModule() == module2).forEach(modernModule -> System.out.println("poka pichego"))));
            append.put(module2.getName().replace(" ", ""), action);
            append.put(module2.getName(), action);
        }
        this.searchField.setAppend(append);
        this.searchField.setPreview("\u041f\u043e\u0438\u0441\u043a");
        this.searchPenis = new PenisPlayer(DefineVisuals.id("penises/search.penis"));
        this.searchPenis.stop();
    }
    protected void init() {
        this.closing = false;
        for (ModernCategory category : this.categories) {
            if (category.getPenis() == null) continue;
            category.getPenis().stop();
        }
        super.init();
    }

    public void tick() {
        this.handleMovementKeys();
        super.tick();
    }

    @Override
    public void render(UIContext context) {
        this.menuAnimation.update(this.closing ? 0.0f : 1.0f);
        this.menuAnimation.setEasing(!this.closing ? Easing.BAKEK : Easing.BAKEK_BACK);
        this.menuAnimation.setDuration(400L);
        this.scrollHandler.update();
        if (this.searchField.isFocused() && !this.prevFocused) {
            this.searchPenis.playOnce();
        }
        this.prevFocused = this.searchField.isFocused();
        float scroll = (float)(-this.scrollHandler.getValue());
        float alpha = Math.min(1.0f, this.menuAnimation.getValue());
        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        ColorRGBA accent = Colors.getAccent();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        RenderUtility.scale(context.getMatrices(), this.menuWindow.getX() + this.menuWindow.getWidth() / 2.0f, this.menuWindow.getY() + this.menuWindow.getHeight() / 2.0f, 0.5f + 0.5f * this.menuAnimation.getValue());
        context.drawShadow(this.menuWindow.getX(), this.menuWindow.getY(), this.menuWindow.getWidth(), this.menuWindow.getHeight(), 25.0f, BorderRadius.all(12.0f), ColorRGBA.BLACK.withAlpha(51.0f));
        this.drawGlassPanel(context, this.menuWindow.getX(), this.menuWindow.getY(), this.menuWindow.getWidth(), this.menuWindow.getHeight(), 10.0f, 12.0f, alpha);
        if (Interface.showMinimalizm()) {
            context.drawBlurredRect(this.menuWindow.getX() + 5.0f, this.menuWindow.getY() + 5.0f, 109.0f, 270.0f, 45.0f, BorderRadius.all(9.0f), ColorRGBA.WHITE.withAlpha(255.0f * alpha * Interface.minimalizm()));
        }
        context.drawSquircle(this.menuWindow.getX() + 5.0f, this.menuWindow.getY() + 5.0f, 109.0f, 270.0f, 8.0f, BorderRadius.all(9.0f), Colors.getBackgroundColor().withAlpha(255.0f * (dark ? 0.28f : 0.2f) * (0.45f + 0.55f * Interface.minimalizm())));
        float x = this.menuWindow.getX();
        float y = this.menuWindow.getY();
        float yOff = 0.0f;
        float xOff = 0.0f;
        float moduleWidth = 120.0f;
        float searchX = x + 13.0f;
        float searchY = y + 13.0f;
        if (Interface.showMinimalizm()) {
            context.drawBlurredRect(searchX, searchY, 93.0f, 14.0f, 45.0f, BorderRadius.all(3.0f), ColorRGBA.WHITE.withAlpha(255.0f * alpha * Interface.minimalizm()));
        }
        if (Interface.showGlass()) {
            context.drawLiquidGlass(searchX, searchY, 93.0f, 14.0f, 2.0f, 0.08f, BorderRadius.all(3.0f), Colors.getAccent().withAlpha(255.0f * alpha * Interface.glass()));
        } else {
            context.drawRoundedRect(searchX, searchY, 93.0f, 14.0f, BorderRadius.all(3.0f), dark ? Colors.getAdditionalColor().mulAlpha(0.6f) : Colors.getBackgroundColor().mulAlpha(0.6f));
        }
        context.drawCenteredText(Fonts.MEDIUM.getFont(7.0f), "parasha", searchX + 93.0f / 2.0f, searchY + 3.0f, Colors.getTextColor().mulAlpha(0.5f));
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        FontBatching regularBatching = new FontBatching(VertexFormats.POSITION_TEXTURE_COLOR, Fonts.REGULAR);
        context.drawText(Fonts.REGULAR.getFont(6.0f), "\u0424\u0443\u043d\u043a\u0446\u0438\u0438", x + 14.0f, y + 35.0f, Colors.getTextColor().mulAlpha(0.3f));
        regularBatching.draw();
        for (ModernCategory modernCategory : this.categories) {
            this.currentCategory.setDuration(150L);
            this.currentCategory.setEasing(Easing.QUAD_OUT);
            if (modernCategory.getCategory() == this.current) {
                this.currentCategory.update(yOff);
            }
            if (modernCategory.getCategory().isSubCategory() && GuiUtility.isHovered((double)(x + 12.0f), (double)(y + 43.0f + yOff), 95.0, 16.0, (double)context.getMouseX(), (double)context.getMouseY())) {
                CursorUtility.set(CursorType.HAND);
            }
            yOff += 18.0f;
        }
        context.drawShadow(x + 12.0f, y + 43.0f + this.currentCategory.getValue(), 95.0f, 16.0f, 16.0f, BorderRadius.all(5.0f), accent.mulAlpha(0.45f));
        context.drawSquircle(x + 12.0f, y + 43.0f + this.currentCategory.getValue(), 95.0f, 16.0f, 10.0f, BorderRadius.all(4.0f), accent);
        context.drawRoundedRect(x + 14.0f, y + 46.5f + this.currentCategory.getValue(), 2.0f, 9.0f, BorderRadius.all(1.0f), Colors.WHITE.mulAlpha(0.85f));
        yOff = 0.0f;
        IconBatching iconBatchingCat = new IconBatching(VertexFormats.POSITION_TEXTURE_COLOR, context.getMatrices());
        for (ModernCategory modernCategory : this.categories) {
            boolean isSub = modernCategory.getCategory().isSubCategory();
            modernCategory.getSelected().update(isSub && modernCategory.getCategory() == this.current);
            float xOffset = isSub ? 10.0f : 0.0f;
            ColorRGBA iconColor = isSub ? Colors.getTextColor().mix(Colors.WHITE, modernCategory.getSelected().getValue()) : Colors.getTextColor().mulAlpha(0.65f);
            if (modernCategory.getPenis() == null) {
                context.drawSprite(modernCategory.getCategory().getMenuSprite(), x + 18.0f + xOffset, y + 47.0f + yOff, 8.0f, 8.0f, iconColor);
            }
            yOff += 18.0f;
        }
        iconBatchingCat.draw();
        yOff = 0.0f;
        IconBatching iconBatching = new IconBatching(VertexFormats.POSITION_TEXTURE_COLOR, context.getMatrices());
        for (ModernCategory modernCategory : this.categories) {
            boolean isSub = modernCategory.getCategory().isSubCategory();
            modernCategory.getSelected().update(isSub && modernCategory.getCategory() == this.current);
            float xOffset = isSub ? 10.0f : 0.0f;
            ColorRGBA iconColor = isSub ? Colors.getTextColor().mix(Colors.WHITE, modernCategory.getSelected().getValue()) : Colors.getTextColor().mulAlpha(0.65f);
            if (modernCategory.getPenis() != null) {
                DrawUtility.drawAnimationSprite(context.getMatrices(), modernCategory.getPenis().getCurrentSprite(), x + 18.0f + xOffset, y + 47.0f + yOff, 8.0f, 8.0f, iconColor);
            }
            yOff += 18.0f;
        }
        iconBatching.draw();
        yOff = 0.0f;
        FontBatching fontBatching = new FontBatching(VertexFormats.POSITION_TEXTURE_COLOR, Fonts.MEDIUM);
        for (ModernCategory category : this.categories) {
            boolean isSub = category.getCategory().isSubCategory();
            float xOffset = isSub ? 10.0f : 0.0f;
            ColorRGBA textColor = isSub ? Colors.getTextColor().mix(Colors.WHITE, category.getSelected().getValue()) : Colors.getTextColor().mulAlpha(0.65f);
            context.drawText(Fonts.MEDIUM.getFont(7.0f), category.getCategory().getName(), x + 32.0f + xOffset, y + 48.5f + yOff, textColor);
            yOff += 18.0f;
        }
        fontBatching.draw();
        float f = yOff = scroll;
        ScissorUtility.push(context.getMatrices(), this.menuWindow.getX(), this.menuWindow.getY() + 1.0f, this.menuWindow.getWidth(), this.menuWindow.getHeight() - 2.0f);
        if (this.current == MenuCategory.CONFIGS) {
            this.configPanel.render(context, x + 115.0f, y, this.menuWindow.getWidth() - 115.0f, this.menuWindow.getHeight(), alpha);
        } else if (this.current == MenuCategory.THEMES) {
            this.themePanel.render(context, x + 115.0f, y, this.menuWindow.getWidth() - 115.0f, this.menuWindow.getHeight(), alpha);
        } else if (this.current == MenuCategory.EVENTS) {
            this.eventsPanel.render(context, x + 115.0f, y, this.menuWindow.getWidth() - 115.0f, this.menuWindow.getHeight(), alpha);
        } else {
            // --- Search bar at top of right content area ---
            float contentX = x + 115.0f;
            float contentWidth = this.menuWindow.getWidth() - 115.0f;
            float searchBarX = contentX + 8.0f;
            float searchBarY = y + 8.0f;
            float searchBarW = contentWidth - 16.0f;
            float searchBarH = 16.0f;
            if (Interface.showMinimalizm()) {
                context.drawBlurredRect(searchBarX, searchBarY, searchBarW, searchBarH, 45.0f, BorderRadius.all(3.0f), ColorRGBA.WHITE.withAlpha(255.0f * alpha * Interface.minimalizm()));
            }
            if (Interface.showGlass()) {
                context.drawLiquidGlass(searchBarX, searchBarY, searchBarW, searchBarH, 2.0f, 0.08f, BorderRadius.all(3.0f), Colors.getAccent().withAlpha(255.0f * alpha * Interface.glass()));
            } else {
                context.drawRoundedRect(searchBarX, searchBarY, searchBarW, searchBarH, BorderRadius.all(3.0f), dark ? Colors.getAdditionalColor().mulAlpha(0.4f) : Colors.getBackgroundColor().mulAlpha(0.4f));
            }
            DrawUtility.drawAnimationSprite(context.getMatrices(), this.searchPenis.getCurrentSprite(), searchBarX + 4.0f, searchBarY + 4.0f, 8.0f, 8.0f, Colors.getTextColor().mulAlpha(0.5f));
            this.searchField.set(searchBarX + 14.0f, searchBarY, searchBarW - 18.0f, searchBarH);
            this.searchField.setTextColor(Colors.getTextColor().mulAlpha(0.5f));
            this.searchField.render(context);

            // --- 2-column masonry module grid ---
            float gridStartY = searchBarY + searchBarH + 8.0f;
            float colGap = 6.0f;
            float sidePad = 8.0f;
            float totalGridW = contentWidth - sidePad * 2.0f;
            float cardWidth = (totalGridW - colGap) / 2.0f;
            float[] colYOff = new float[]{0.0f, 0.0f}; // track Y per column
            float gridVisibleHeight = this.menuWindow.getHeight() - (gridStartY - y) - 5.0f;

            ScissorUtility.push(context.getMatrices(), contentX, gridStartY, contentWidth, gridVisibleHeight);
            SquircleBatching squircleBatching = new SquircleBatching(5.0f);
            List<ModernCategory> contentCategories = this.searchField.getBuiltText().isBlank() 
                ? this.categories.stream().filter(cat -> cat.getCategory() == this.current).toList()
                : this.categories;
            boolean hasModules = false;
            for (ModernCategory modernCategory : contentCategories) {
                modernCategory.setY(0.0f);
                for (ModernModule modernModule : modernCategory.getModules()) {
                    boolean cond = !this.opened(modernModule);
                    modernModule.getVisible().update(cond);
                    modernModule.getOffset().update(cond);
                    if (this.visibleCheck(modernModule)) continue;
                    hasModules = true;
                    float cardH = modernModule.getCardHeight(cardWidth);
                    // Pick the shorter column
                    int col = colYOff[0] <= colYOff[1] ? 0 : 1;
                    float cardX = contentX + sidePad + col * (cardWidth + colGap);
                    float cardY = gridStartY + scroll + colYOff[col];
                    modernModule.set(cardX, cardY, cardWidth, cardH);
                    if (GuiUtility.isHovered((double)x, (double)(y - cardH), (double)this.menuWindow.getWidth(), (double)(this.menuWindow.getHeight() + cardH), modernModule.getX(), modernModule.getY())) {
                        modernModule.render(context);
                        if (GuiUtility.isHovered(modernModule.getX(), modernModule.getY(), modernModule.getWidth(), modernModule.getHeight(), context)) {
                            CursorUtility.set(CursorType.HAND);
                        }
                    }
                    colYOff[col] += (cardH + colGap) * modernModule.getOffset().getValue();
                }
            }
            if (!hasModules) {
                context.drawCenteredText(Fonts.MEDIUM.getFont(8.0f), "\u0442\u0443\u0442 \u043f\u043e\u043a\u0430 \u0447\u0442\u043e \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435\u0442\u0443(", x + 115.0f + (this.menuWindow.getWidth() - 115.0f) / 2.0f, y + this.menuWindow.getHeight() / 2.0f, Colors.getTextColor().mulAlpha(0.5f));
            }
            squircleBatching.draw();
            // Rounds & Into batches (empty now but keep batching structure)
            RoundedRectBatching roundBatching = new RoundedRectBatching();
            roundBatching.draw();
            RoundedRectBatching roundedRectBatching = new RoundedRectBatching();
            roundedRectBatching.draw();
            // Module name text
            FontBatching mediumBatching = new FontBatching(VertexFormats.POSITION_TEXTURE_COLOR, Fonts.MEDIUM);
            for (ModernCategory modernCategory : contentCategories) {
                for (ModernModule modernModule : modernCategory.getModules()) {
                    if (this.visibleCheck(modernModule) || !GuiUtility.isHovered((double)x, (double)(y - modernModule.getHeight()), (double)this.menuWindow.getWidth(), (double)(this.menuWindow.getHeight() + modernModule.getHeight()), modernModule.getX(), modernModule.getY())) continue;
                    modernModule.renderMedium(context);
                }
            }
            mediumBatching.draw();
            // Description text (wrapped, rendered per-line)
            FontBatching descBatching = new FontBatching(VertexFormats.POSITION_TEXTURE_COLOR, Fonts.REGULAR);
            for (ModernCategory category : contentCategories) {
                for (ModernModule modernModule : category.getModules()) {
                    if (this.visibleCheck(modernModule) || !GuiUtility.isHovered((double)x, (double)(y - modernModule.getHeight()), (double)this.menuWindow.getWidth(), (double)(this.menuWindow.getHeight() + modernModule.getHeight()), modernModule.getX(), modernModule.getY())) continue;
                    modernModule.renderRegular(context);
                }
            }
            descBatching.draw();
            ScissorUtility.pop();
            float totalContentHeight = Math.max(colYOff[0], colYOff[1]);
            float maxScroll = -Math.max(0.0f, totalContentHeight - gridVisibleHeight);
            this.scrollHandler.setMax(maxScroll - 10.0f);
        }
        ScissorUtility.pop();
        this.renderWatermark(context, x, y);
        RenderUtility.end(context.getMatrices());
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        for (ModernSettings window2 : this.windows) {
            window2.render(context);
        }
        for (ColorPicker colorPicker2 : this.colorPickers) {
            colorPicker2.render(context);
        }
        this.windows.removeIf(window -> window.getAnimation().getValue() == 0.0f && !window.isShowing());
        this.colorPickers.removeIf(colorPicker -> colorPicker.getAnimation().getValue() == 0.0f && !colorPicker.isShowing());
    }
    private void handleMovementKeys() {
        KeyBinding[] movementKeys;
        if (ModernScreen.mc.player == null || this.isTyping()) {
            return;
        }
        long windowHandle = mc.getWindow().getHandle();
        for (KeyBinding key : movementKeys = new KeyBinding[]{ModernScreen.mc.options.forwardKey, ModernScreen.mc.options.backKey, ModernScreen.mc.options.leftKey, ModernScreen.mc.options.rightKey, ModernScreen.mc.options.jumpKey}) {
            int keyCode = InputUtil.fromTranslationKey((String)key.getBoundKeyTranslationKey()).getCode();
            key.setPressed(InputUtil.isKeyPressed((long)windowHandle, (int)keyCode));
        }
        if (ModernScreen.mc.player.getAbilities().flying) {
            int keyCode = InputUtil.fromTranslationKey((String)ModernScreen.mc.options.sneakKey.getBoundKeyTranslationKey()).getCode();
            ModernScreen.mc.options.sneakKey.setPressed(InputUtil.isKeyPressed((long)windowHandle, (int)keyCode));
        }
    }

    private boolean isTyping() {
        return ModernScreen.mc.currentScreen != null && TextField.LAST_FIELD != null && TextField.LAST_FIELD.isFocused();
    }

    private void drawGlassPanel(UIContext context, float x, float y, float width, float height, float squirt, float radius, float alpha) {
        BorderRadius borderRadius = BorderRadius.all(radius);
        if (Interface.showMinimalizm()) {
            context.drawBlurredRect(x, y, width, height, 45.0f, squirt, borderRadius, ColorRGBA.WHITE.withAlpha(255.0f * alpha * Interface.minimalizm()));
        }
        if (Interface.showGlass()) {
            context.drawLiquidGlass(x, y, width, height, squirt, 0.08f, borderRadius, Colors.getAccent().withAlpha(255.0f * alpha * Interface.glass()));
        }
        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        context.drawSquircle(x, y, width, height, squirt, borderRadius, Colors.getBackgroundColor().withAlpha(255.0f * (dark ? 0.9f - 0.7f * Interface.glass() : 0.7f)));
    }

    private void renderWatermark(UIContext context, float menuX, float menuY) {
        Font titleFont = Fonts.SEMIBOLD.getFont(7.0f);
        Font infoFont = Fonts.REGULAR.getFont(5.5f);
        String title = DefineVisuals.NAME;
        String user = mc.getSession() != null ? mc.getSession().getUsername() : "Player";
        String info = "v" + DefineVisuals.VERSION + "  ·  " + DefineVisuals.BUILD_TYPE + "  ·  " + user;
        float logo = 18.0f;
        float height = 32.0f;
        float x = menuX + 13.0f;
        float width = 93.0f;
        float y = menuY + 5.0f + 270.0f - 10.0f - height;
        context.drawRoundedRect(x, y - 6.0f, width, 0.8f, BorderRadius.all(1.0f), Colors.getTextColor().mulAlpha(0.1f));
        context.drawTexture(DefineVisuals.id("icon.png"), x, y + (height - logo) / 2.0f, logo, logo, Colors.WHITE);
        float textX = x + logo + 6.0f;
        context.drawText(titleFont, title, textX, y + 6.0f, Colors.getTextColor());
        context.drawFadeoutText(infoFont, info, textX, y + 17.5f, Colors.getTextColor().mulAlpha(0.45f), 0.7f, 1.0f, width - logo - 8.0f);
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
        for (ModernSettings window : this.windows) {
            window.onMouseClicked(mouseX, mouseY, button);
            if (window.isHovered(mouseX, mouseY)) {
                return;
            }
            if (GuiUtility.isHovered(this.menuWindow, mouseX, mouseY)) continue;
            boolean can = true;
            for (ModernSettings window1 : this.windows) {
                if (!GuiUtility.isHovered(window1, mouseX, mouseY)) continue;
                can = false;
            }
            if (!can) continue;
            window.setShowing(false);
        }
        float x = this.menuWindow.getX();
        float y = this.menuWindow.getY();
        float yOff = 0.0f;
        float xOff = 0.0f;
        for (ModernCategory category : this.categories) {
            if (category.getCategory().isSubCategory() && GuiUtility.isHovered((double)(x + 12.0f), (double)(y + 43.0f + yOff), 95.0, 16.0, mouseX, mouseY)) {
                if (category.getCategory() != this.current) {
                    this.current = category.getCategory();
                    this.scrollHandler.setValue(0.0f);
                    this.scrollHandler.setTargetValue(0.0f);
                    if (category.getPenis() != null) {
                        category.getPenis().playOnce();
                    }
                }
                return;
            }
            yOff += 18.0f;
        }
        if (this.current == MenuCategory.CONFIGS) {
            this.configPanel.onMouseClicked(mouseX, mouseY, button, x + 115.0f, y, this.menuWindow.getWidth() - 115.0f, this.menuWindow.getHeight());
            if (this.configPanel.isModalOpen()) return;
        } else if (this.current == MenuCategory.THEMES) {
            this.themePanel.onMouseClicked(mouseX, mouseY, button, x + 115.0f, y, this.menuWindow.getWidth() - 115.0f, this.menuWindow.getHeight());
            if (this.themePanel.isPickerOpen()) return;
        } else if (this.current == MenuCategory.EVENTS) {
            this.eventsPanel.onMouseClicked(mouseX, mouseY, button, x + 115.0f, y, this.menuWindow.getWidth() - 115.0f, this.menuWindow.getHeight());
            return;
        } else {
            List<ModernCategory> contentCategories = this.searchField.getBuiltText().isBlank() 
                ? this.categories.stream().filter(cat -> cat.getCategory() == this.current).toList()
                : this.categories;
            for (ModernCategory category : contentCategories) {
                for (ModernModule module : category.getModules()) {
                    if (this.visibleCheck(module) || !this.isInModuleGrid(mouseX, mouseY) || !GuiUtility.isHovered(this.menuWindow, mouseX, mouseY) && (button == MouseButton.LEFT || button == MouseButton.RIGHT) || !GuiUtility.isHovered((double)module.getX(), (double)module.getY(), (double)module.getWidth(), (double)module.getHeight(), mouseX, mouseY)) continue;
                    module.onMouseClicked(mouseX, mouseY, button);
                    return;
                }
            }
        }
        if (button != MouseButton.MIDDLE) {
            this.searchField.onMouseClicked(mouseX, mouseY, button);
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        if (this.current == MenuCategory.CONFIGS) {
            this.configPanel.onMouseReleased(mouseX, mouseY, button);
        } else if (this.current == MenuCategory.THEMES) {
            this.themePanel.onMouseReleased(mouseX, mouseY, button);
        } else if (this.current == MenuCategory.EVENTS) {
            this.eventsPanel.onMouseReleased(mouseX, mouseY, button);
        }
        for (ModernSettings window : this.windows) {
            window.onMouseReleased(mouseX, mouseY, button);
        }
        for (ColorPicker colorPicker : this.colorPickers) {
            colorPicker.onMouseReleased(mouseX, mouseY, button);
        }
        if (this.searchField.isFocused()) {
            this.searchField.onMouseReleased(mouseX, mouseY, button);
        }
        super.onMouseReleased(mouseX, mouseY, button);
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (ModernSettings window : this.windows) {
            window.onScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        if (this.current == MenuCategory.CONFIGS) {
            if (GuiUtility.isHovered(this.menuWindow, mouseX, mouseY)) {
                this.configPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        } else if (this.current == MenuCategory.THEMES) {
            if (GuiUtility.isHovered(this.menuWindow, mouseX, mouseY)) {
                this.themePanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        } else if (this.current == MenuCategory.EVENTS) {
            if (GuiUtility.isHovered(this.menuWindow, mouseX, mouseY)) {
                this.eventsPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        } else if (GuiUtility.isHovered(this.menuWindow, mouseX, mouseY)) {
            this.scrollHandler.scroll(verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.current == MenuCategory.CONFIGS && this.configPanel.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.current == MenuCategory.THEMES && this.themePanel.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.current == MenuCategory.EVENTS && this.eventsPanel.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (!this.searchField.isFocused() && Screen.hasControlDown() && keyCode == 70) {
            this.searchField.setFocused(true);
        }
        this.scrollHandler.onKeyPressed(keyCode);
        for (ModernSettings window : this.windows) {
            window.onKeyPressed(keyCode, scanCode, modifiers);
        }
        for (ColorPicker colorPicker : this.colorPickers) {
            colorPicker.onKeyPressed(keyCode, scanCode, modifiers);
        }
        if (this.searchField.isFocused() && !this.isBindingModule()) {
            this.searchField.onKeyPressed(keyCode, scanCode, modifiers);
        }
        for (ModernCategory category : this.categories) {
            for (ModernModule module : category.getModules()) {
                if (this.visibleCheck(module)) continue;
                module.onKeyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    public boolean charTyped(char chr, int modifiers) {
        if (this.current == MenuCategory.CONFIGS && this.configPanel.charTyped(chr, modifiers)) {
            return true;
        }
        if (this.searchField.isFocused() && !this.isBindingModule()) {
            this.searchField.charTyped(chr, modifiers);
        }
        for (ModernSettings window : this.windows) {
            window.charTyped(chr, modifiers);
        }
        for (ModernCategory category : this.categories) {
            for (ModernModule module : category.getModules()) {
                if (this.visibleCheck(module)) continue;
                module.charTyped(chr, modifiers);
            }
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

    private boolean searchCheck(ModernModule component) {
        TextField search = this.searchField;
        return search != null && !search.getBuiltText().isBlank() && !component.getModule().getName().toLowerCase().contains(search.getBuiltText().toLowerCase()) && !component.getModule().getName().replace(" ", "").toLowerCase().contains(search.getBuiltText().toLowerCase());
    }

    private boolean visibleCheck(ModernModule component) {
        return component.getOffset().getValue() == 0.0f || this.searchCheck(component);
    }

    private boolean opened(ModernModule component) {
        return this.windows.stream().anyMatch(window -> window.getModule() == component);
    }

    private boolean isInModuleGrid(double mouseX, double mouseY) {
        float x = this.menuWindow.getX() + 115.0f;
        float y = this.menuWindow.getY() + 32.0f;
        return GuiUtility.isHovered(x, y, this.menuWindow.getWidth() - 115.0f, this.menuWindow.getHeight() - 37.0f, mouseX, mouseY);
    }

    public boolean isBindingModule() {
        return this.categories.stream().flatMap(panel -> panel.getModules().stream()).anyMatch(ModernModule::isBinding);
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
    public Rect getMenuWindow() {
        return this.menuWindow;
    }

    @Generated
    public float getDragX() {
        return this.dragX;
    }

    @Generated
    public float getDragY() {
        return this.dragY;
    }

    @Generated
    public boolean isDrag() {
        return this.drag;
    }

    @Generated
    public ScrollHandler getScrollHandler() {
        return this.scrollHandler;
    }

    @Generated
    public MenuCategory getCurrent() {
        return this.current;
    }

    @Generated
    public List<ColorPicker> getColorPickers() {
        return this.colorPickers;
    }

    @Generated
    public List<ModernCategory> getCategories() {
        return this.categories;
    }

    @Generated
    public List<ModernCategory> getTopLevelCategories() {
        return this.topLevelCategories;
    }

    @Generated
    public MenuCategory getParentView() {
        return this.parentView;
    }

    @Generated
    public List<ModernSettings> getWindows() {
        return this.windows;
    }

    @Generated
    public Animation getCurrentCategory() {
        return this.currentCategory;
    }

    @Generated
    public TextField getSearchField() {
        return this.searchField;
    }

    @Generated
    public PenisPlayer getSearchPenis() {
        return this.searchPenis;
    }

    @Generated
    public boolean isPrevFocused() {
        return this.prevFocused;
    }

    @Generated
    public Timer getTimer() {
        return this.timer;
    }


}
