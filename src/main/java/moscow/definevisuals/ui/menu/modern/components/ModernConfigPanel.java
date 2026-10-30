package moscow.definevisuals.ui.menu.modern.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.cosmetics.LocalCosmetics;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.config.ConfigFile;
import moscow.definevisuals.systems.config.ConfigManager;
import moscow.definevisuals.systems.file.FileManager;
import moscow.definevisuals.systems.modules.Module;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.modules.modules.visuals.MenuModule;
import moscow.definevisuals.systems.notifications.NotificationType;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.ui.components.textfield.TextField;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.gui.ScrollHandler;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.render.DrawUtility;
import moscow.definevisuals.utility.render.ScissorUtility;
import moscow.definevisuals.utility.render.obj.CustomSprite;
import moscow.definevisuals.utility.sounds.ClientSounds;
import net.minecraft.util.Identifier;

public class ModernConfigPanel implements IMinecraft {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final Identifier TRASH_ICON = DefineVisuals.id("icons/hud/trash.png");
    private static final float TEXT_BTN_W = 38.0f;

    public enum ModalType {
        NONE,
        CREATE,
        RESET
    }

    private final ScrollHandler scrollHandler = new ScrollHandler();
    private ModalType activeModal = ModalType.NONE;
    private final Animation modalAnim = new Animation(250L, Easing.BAKEK);

    private final TextField createField;

    // Cache of animation states for config cards
    private final List<ConfigCardState> cardStates = new ArrayList<>();

    private static class ConfigCardState {
        final ConfigFile configFile;
        final Animation hoverAnim = new Animation(200L, Easing.QUAD_OUT);
        final Animation selectAnim = new Animation(250L, Easing.FIGMA_EASE_IN_OUT);
        final Animation loadHover = new Animation(150L, Easing.QUAD_OUT);
        final Animation saveHover = new Animation(150L, Easing.QUAD_OUT);
        final Animation deleteHover = new Animation(150L, Easing.QUAD_OUT);

        ConfigCardState(ConfigFile configFile) {
            this.configFile = configFile;
        }
    }

    public ModernConfigPanel() {
        this.createField = new TextField(Fonts.MEDIUM.getFont(6.5f));
        this.createField.setPreview("Название конфига");

    }

    public boolean isModalOpen() {
        return this.activeModal != ModalType.NONE;
    }

    public void refresh() {
        DefineVisuals.getInstance().getConfigManager().refresh();
    }

    public void render(UIContext context, float x, float y, float width, float height, float menuAlpha) {
        this.scrollHandler.update();
        this.modalAnim.update(this.activeModal != ModalType.NONE ? 1.0f : 0.0f);

        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        ColorRGBA accent = Colors.getAccent();
        ColorRGBA textColor = Colors.getTextColor();

        // 1. Header Bar
        float headerY = y + 14.0f;
        float headerX = x + 12.0f;

        // Header Title: [Folder Icon] Конфигурации
        context.drawSprite(CustomSprite.CONFIGS, headerX, headerY + 1.0f, 9.0f, 9.0f, textColor);
        context.drawText(Fonts.SEMIBOLD.getFont(9.0f), "Конфигурации", headerX + 13.0f, headerY + 1.0f, textColor);

        // Header Action Buttons on Right
        float rightBtnY = headerY - 1.0f;
        float btnHeight = 14.0f;

        // Button 4: [Сбросить]
        float resetW = 44.0f;
        float resetX = x + width - 14.0f - resetW;
        boolean resetHov = GuiUtility.isHovered((double)resetX, (double)rightBtnY, resetW, btnHeight, (double)context.getMouseX(), (double)context.getMouseY());
        if (resetHov) CursorUtility.set(CursorType.HAND);
        this.drawButton(context, resetX, rightBtnY, resetW, btnHeight, "Сбросить", resetHov, dark, accent, textColor);

        // Button 2: [Создать]
        float createW = 38.0f;
        float createX = resetX - createW - 4.0f;
        boolean createHov = GuiUtility.isHovered((double)createX, (double)rightBtnY, createW, btnHeight, (double)context.getMouseX(), (double)context.getMouseY());
        if (createHov) CursorUtility.set(CursorType.HAND);
        this.drawButton(context, createX, rightBtnY, createW, btnHeight, "Создать", createHov, dark, accent, textColor);

        // Button 1: [Папка] - открыть папку с конфигами
        float folderW = 32.0f;
        float folderX = createX - folderW - 4.0f;
        boolean folderHov = GuiUtility.isHovered((double)folderX, (double)rightBtnY, folderW, btnHeight, (double)context.getMouseX(), (double)context.getMouseY());
        if (folderHov) CursorUtility.set(CursorType.HAND);
        this.drawButton(context, folderX, rightBtnY, folderW, btnHeight, "Папка", folderHov, dark, accent, textColor);

        // 2. Subheader: "Мои конфиги"
        float subHeaderY = headerY + 20.0f;
        context.drawText(Fonts.MEDIUM.getFont(6.0f), "Мои конфиги", headerX, subHeaderY, textColor.mulAlpha(0.35f));

        // 3. Config Cards List
        float listY = subHeaderY + 11.0f;
        float listHeight = height - (listY - y) - 10.0f;
        float cardWidth = width - 24.0f;
        float cardHeight = 28.0f;

        ConfigManager configManager = DefineVisuals.getInstance().getConfigManager();
        List<ConfigFile> configs = configManager.getConfigFiles();

        // Sync card states
        this.syncCardStates(configs);

        ScissorUtility.push(context.getMatrices(), headerX - 2.0f, listY, cardWidth + 4.0f, listHeight);

        float currentY = listY - (float)this.scrollHandler.getValue();
        ConfigFile currentActive = configManager.getCurrent();

        if (configs.isEmpty()) {
            context.drawCenteredText(Fonts.MEDIUM.getFont(7.5f), "Конфигов пока нет(", headerX + cardWidth / 2.0f, listY + listHeight / 2.0f - 4.0f, textColor.mulAlpha(0.4f));
        } else {
            for (ConfigCardState state : this.cardStates) {
                ConfigFile cfg = state.configFile;
                boolean isCardHovered = !this.isModalOpen() && GuiUtility.isHovered((double)headerX, (double)currentY, cardWidth, cardHeight, (double)context.getMouseX(), (double)context.getMouseY());
                state.hoverAnim.update(isCardHovered);
                if (isCardHovered) {
                    CursorUtility.set(CursorType.HAND);
                }

                boolean isActive = (currentActive != null && currentActive.getFileName().equalsIgnoreCase(cfg.getFileName())) || (currentActive == null && cfg.getFileName().equalsIgnoreCase("autosave"));
                state.selectAnim.update(isActive);

                // Card background (matching module grid style without outline)
                float glass = Interface.glass();
                ColorRGBA base = (dark ? Colors.getBackgroundColor() : Colors.getAdditionalColor())
                        .withAlpha(255.0f * (dark ? 0.16f + 0.14f * Interface.minimalizm() : 0.12f + 0.12f * Interface.minimalizm()));
                ColorRGBA cardBg = base
                        .mix(accent.withAlpha(255.0f * (0.25f + 0.15f * (1.0f - glass))), state.selectAnim.getValue() * 0.9f)
                        .mix(Colors.getTextColor().withAlpha(20.0f), state.hoverAnim.getValue() * 0.35f);

                BorderRadius cardRadius = BorderRadius.all(4.0f);
                context.drawSquircle(headerX, currentY, cardWidth, cardHeight, 3.0f, cardRadius, cardBg);

                // Left Avatar / Player Head
                float avatarSize = 18.0f;
                float avatarX = headerX + 5.0f;
                float avatarY = currentY + (cardHeight - avatarSize) / 2.0f;

                if (mc.player != null) {
                    DrawUtility.drawPlayerHeadWithHat(context.getMatrices(), mc.player, avatarX, avatarY, avatarSize, BorderRadius.all(avatarSize / 2.0f), Colors.WHITE);
                } else {
                    context.drawSquircle(avatarX, avatarY, avatarSize, avatarSize, 4.0f, BorderRadius.all(avatarSize / 2.0f), accent.mulAlpha(0.4f));
                    context.drawSprite(CustomSprite.CONFIGS, avatarX + 4.5f, avatarY + 4.5f, 9.0f, 9.0f, Colors.WHITE);
                }

                // Config Name & Subtitle
                boolean isAutosave = cfg.getFileName().equalsIgnoreCase("autosave");
                float textLeft = avatarX + avatarSize + 6.0f;
                float actionW = isAutosave ? 0.0f : TEXT_BTN_W * 2.0f + 13.0f + 8.0f;
                float maxTextW = Math.max(40.0f, headerX + cardWidth - 6.0f - actionW - textLeft);
                context.drawFadeoutText(Fonts.SEMIBOLD.getFont(7.0f), cfg.getFileName(), textLeft, currentY + 6.5f, textColor, 0.9f, 1.0f, maxTextW);

                File cfgDir = new File(FileManager.DIRECTORY, "configs");
                File cfgFile = new File(cfgDir, cfg.getFileName() + ".def");
                if (!cfgFile.exists()) {
                    File legacy = new File(cfgDir, cfg.getFileName() + ".rock");
                    if (legacy.exists()) {
                        cfgFile = legacy;
                    }
                }
                String dateStr = cfgFile.exists() ? DATE_FORMAT.format(new Date(cfgFile.lastModified())) : "—";
                String subInfo = dateStr + " · " + (isAutosave ? "Автосохранение" : isActive ? "Активен" : "Готов");
                context.drawText(Fonts.REGULAR.getFont(5.5f), subInfo, textLeft, currentY + 16.5f, isActive ? accent.mulAlpha(0.85f) : textColor.mulAlpha(0.45f));

                // Action Buttons on Card (Right Side)
                float btnSize = 13.0f;
                float actY = currentY + (cardHeight - btnSize) / 2.0f;

                if (isAutosave) {
                    state.deleteHover.update(false);
                    state.saveHover.update(false);
                    state.loadHover.update(false);
                } else {
                    // Delete Button
                    float delX = headerX + cardWidth - btnSize - 6.0f;
                    boolean isDelHov = !this.isModalOpen() && GuiUtility.isHovered((double)delX, (double)actY, btnSize, btnSize, (double)context.getMouseX(), (double)context.getMouseY());
                    state.deleteHover.update(isDelHov);
                    if (isDelHov) CursorUtility.set(CursorType.HAND);
                    context.drawSquircle(delX, actY, btnSize, btnSize, 2.5f, BorderRadius.all(2.5f), isDelHov ? ColorRGBA.RED.mulAlpha(0.35f) : Colors.getBackgroundColor().mulAlpha(0.2f));
                    context.drawTexture(TRASH_ICON, delX + 2.5f, actY + 2.5f, 8.0f, 8.0f, isDelHov ? ColorRGBA.RED : textColor.mulAlpha(0.6f));

                    // Save Button (Overwrite)
                    float saveX = delX - TEXT_BTN_W - 4.0f;
                    boolean isSaveHov = !this.isModalOpen() && GuiUtility.isHovered((double)saveX, (double)actY, TEXT_BTN_W, btnSize, (double)context.getMouseX(), (double)context.getMouseY());
                    state.saveHover.update(isSaveHov);
                    if (isSaveHov) CursorUtility.set(CursorType.HAND);
                    this.drawButton(context, saveX, actY, TEXT_BTN_W, btnSize, "Сохранить", isSaveHov, dark, accent, textColor);

                    // Load Button
                    float loadX = saveX - TEXT_BTN_W - 4.0f;
                    boolean isLoadHov = !this.isModalOpen() && GuiUtility.isHovered((double)loadX, (double)actY, TEXT_BTN_W, btnSize, (double)context.getMouseX(), (double)context.getMouseY());
                    state.loadHover.update(isLoadHov);
                    if (isLoadHov) CursorUtility.set(CursorType.HAND);
                    this.drawButton(context, loadX, actY, TEXT_BTN_W, btnSize, "Загрузить", isLoadHov || isActive, dark, accent, textColor);
                }

                currentY += cardHeight + 5.0f;
            }
        }

        float totalHeight = configs.size() * (cardHeight + 5.0f);
        float maxScroll = -Math.max(0.0f, totalHeight - listHeight);
        this.scrollHandler.setMax(maxScroll);

        ScissorUtility.pop();

        // 4. Modal Popups Overlay (if active)
        if (this.modalAnim.getValue() > 0.01f) {
            this.renderModal(context, x, y, width, height, this.modalAnim.getValue(), dark, accent, textColor);
        }
    }

    private void drawButton(UIContext context, float x, float y, float w, float h, String text, boolean hovered, boolean dark, ColorRGBA accent, ColorRGBA textColor) {
        ColorRGBA bg = hovered ? accent.mulAlpha(0.3f) : Colors.getBackgroundColor().mulAlpha(dark ? 0.4f : 0.25f);
        context.drawSquircle(x, y, w, h, 3.0f, BorderRadius.all(3.0f), bg);
        context.drawCenteredText(Fonts.MEDIUM.getFont(5.5f), text, x + w / 2.0f, y + 4.5f, hovered ? accent : textColor.mulAlpha(0.8f));
    }

    private void renderModal(UIContext context, float menuX, float menuY, float menuW, float menuH, float anim, boolean dark, ColorRGBA accent, ColorRGBA textColor) {
        // Dim overlay
        context.drawRoundedRect(menuX - 115.0f, menuY, menuW + 115.0f, menuH, BorderRadius.all(16.0f), ColorRGBA.BLACK.mulAlpha(0.45f * anim));

        // Center Modal Box
        float modalW = 160.0f;
        float modalH = this.activeModal == ModalType.RESET ? 75.0f : 85.0f;
        float modalX = menuX + (menuW - modalW) / 2.0f;
        float modalY = menuY + (menuH - modalH) / 2.0f;

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, anim);

        context.drawShadow(modalX, modalY, modalW, modalH, 20.0f, BorderRadius.all(8.0f), ColorRGBA.BLACK.withAlpha(60.0f * anim));
        if (Interface.showGlass()) {
            context.drawLiquidGlass(modalX, modalY, modalW, modalH, 6.0f, 0.08f, BorderRadius.all(8.0f), Colors.getAccent().withAlpha(255.0f * anim * Interface.glass()));
        }
        context.drawSquircle(modalX, modalY, modalW, modalH, 6.0f, BorderRadius.all(8.0f), Colors.getBackgroundColor().withAlpha(255.0f * (dark ? 0.95f : 0.85f) * anim));
        context.drawRoundedBorder(modalX, modalY, modalW, modalH, 0.8f, BorderRadius.all(8.0f), accent.mulAlpha(0.4f * anim));

        float btnW = 56.0f;
        float btnH = 14.0f;
        float btnY = modalY + modalH - btnH - 8.0f;

        if (this.activeModal == ModalType.RESET) {
            context.drawCenteredText(Fonts.SEMIBOLD.getFont(7.5f), "Сбросить настройки?", modalX + modalW / 2.0f, modalY + 12.0f, textColor);
            context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), "Все модули и бинды вернутся к заводским.", modalX + modalW / 2.0f, modalY + 25.0f, textColor.mulAlpha(0.6f));

            // Cancel Button
            float cancelX = modalX + 18.0f;
            boolean canHov = GuiUtility.isHovered((double)cancelX, (double)btnY, btnW, btnH, (double)context.getMouseX(), (double)context.getMouseY());
            if (canHov) CursorUtility.set(CursorType.HAND);
            this.drawButton(context, cancelX, btnY, btnW, btnH, "Отмена", canHov, dark, accent, textColor);

            // Confirm Reset Button
            float confirmX = modalX + modalW - btnW - 18.0f;
            boolean confHov = GuiUtility.isHovered((double)confirmX, (double)btnY, btnW, btnH, (double)context.getMouseX(), (double)context.getMouseY());
            if (confHov) CursorUtility.set(CursorType.HAND);
            ColorRGBA resetBtnBg = confHov ? ColorRGBA.RED.mulAlpha(0.6f) : ColorRGBA.RED.mulAlpha(0.35f);
            context.drawSquircle(confirmX, btnY, btnW, btnH, 3.0f, BorderRadius.all(3.0f), resetBtnBg);
            context.drawCenteredText(Fonts.MEDIUM.getFont(5.5f), "Сбросить", confirmX + btnW / 2.0f, btnY + 4.5f, Colors.WHITE);

        } else if (this.activeModal == ModalType.CREATE) {
            context.drawCenteredText(Fonts.SEMIBOLD.getFont(7.5f), "Создать конфигурацию", modalX + modalW / 2.0f, modalY + 10.0f, textColor);
            context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), "Введите название нового конфига:", modalX + modalW / 2.0f, modalY + 21.0f, textColor.mulAlpha(0.55f));

            float fieldW = modalW - 24.0f;
            float fieldX = modalX + 12.0f;
            float fieldY = modalY + 31.0f;
            context.drawRoundedRect(fieldX, fieldY, fieldW, 14.0f, BorderRadius.all(3.0f), Colors.getBackgroundColor().mulAlpha(dark ? 0.6f : 0.4f));
            this.createField.set(fieldX + 4.0f, fieldY, fieldW - 8.0f, 14.0f);
            this.createField.setTextColor(textColor);
            this.createField.render(context);

            // Cancel Button
            float cancelX = modalX + 18.0f;
            boolean canHov = GuiUtility.isHovered((double)cancelX, (double)btnY, btnW, btnH, (double)context.getMouseX(), (double)context.getMouseY());
            if (canHov) CursorUtility.set(CursorType.HAND);
            this.drawButton(context, cancelX, btnY, btnW, btnH, "Отмена", canHov, dark, accent, textColor);

            // Create Button
            float confirmX = modalX + modalW - btnW - 18.0f;
            boolean confHov = GuiUtility.isHovered((double)confirmX, (double)btnY, btnW, btnH, (double)context.getMouseX(), (double)context.getMouseY());
            if (confHov) CursorUtility.set(CursorType.HAND);
            ColorRGBA createBtnBg = confHov ? accent : accent.mulAlpha(0.7f);
            context.drawSquircle(confirmX, btnY, btnW, btnH, 3.0f, BorderRadius.all(3.0f), createBtnBg);
            context.drawCenteredText(Fonts.MEDIUM.getFont(5.5f), "Создать", confirmX + btnW / 2.0f, btnY + 4.5f, Colors.WHITE);

        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void onMouseClicked(double mouseX, double mouseY, MouseButton button, float x, float y, float width, float height) {
        if (this.activeModal != ModalType.NONE) {
            this.handleModalClick(mouseX, mouseY, button, x, y, width, height);
            return;
        }

        float headerY = y + 14.0f;
        float rightBtnY = headerY - 1.0f;
        float btnHeight = 14.0f;

        // Button 4: [Сбросить]
        float resetW = 44.0f;
        float resetX = x + width - 14.0f - resetW;
        if (GuiUtility.isHovered((double)resetX, (double)rightBtnY, resetW, btnHeight, mouseX, mouseY)) {
            this.activeModal = ModalType.RESET;
            this.playClickSound();
            return;
        }

        // Button 2: [Создать]
        float createW = 38.0f;
        float createX = resetX - createW - 4.0f;
        if (GuiUtility.isHovered((double)createX, (double)rightBtnY, createW, btnHeight, mouseX, mouseY)) {
            this.activeModal = ModalType.CREATE;
            this.createField.clear();
            this.createField.setFocused(true);
            this.playClickSound();
            return;
        }

        // Button 1: [Folder Button]
        float folderW = 32.0f;
        float folderX = createX - folderW - 4.0f;
        if (GuiUtility.isHovered((double)folderX, (double)rightBtnY, folderW, btnHeight, mouseX, mouseY)) {
            DefineVisuals.getInstance().getConfigManager().directionConfig();
            this.playClickSound();
            return;
        }

        // Config Cards Click
        float headerX = x + 12.0f;
        float subHeaderY = headerY + 20.0f;
        float listY = subHeaderY + 11.0f;
        float cardWidth = width - 24.0f;
        float cardHeight = 28.0f;
        float currentY = listY - (float)this.scrollHandler.getValue();

        for (ConfigCardState state : this.cardStates) {
            ConfigFile cfg = state.configFile;
            if (GuiUtility.isHovered((double)headerX, (double)currentY, cardWidth, cardHeight, mouseX, mouseY)) {
                boolean isAutosave = cfg.getFileName().equalsIgnoreCase("autosave");
                float btnSize = 13.0f;
                float actY = currentY + (cardHeight - btnSize) / 2.0f;

                if (!isAutosave) {
                    // Delete Button
                    float delX = headerX + cardWidth - btnSize - 6.0f;
                    if (GuiUtility.isHovered((double)delX, (double)actY, btnSize, btnSize, mouseX, mouseY)) {
                        cfg.delete();
                        this.playClickSound();
                        return;
                    }

                    // Save Button (Overwrite)
                    float saveX = delX - TEXT_BTN_W - 4.0f;
                    if (GuiUtility.isHovered((double)saveX, (double)actY, TEXT_BTN_W, btnSize, mouseX, mouseY)) {
                        cfg.save();
                        DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.SUCCESS, "Конфиг " + cfg.getFileName() + " сохранён!");
                        this.playClickSound();
                        return;
                    }

                    // Load Button
                    float loadX = saveX - TEXT_BTN_W - 4.0f;
                    if (GuiUtility.isHovered((double)loadX, (double)actY, TEXT_BTN_W, btnSize, mouseX, mouseY)) {
                        cfg.load();
                        this.playClickSound();
                        return;
                    }
                } else {
                    // Autosave card click -> load (no action buttons)
                    cfg.load();
                    this.playClickSound();
                    return;
                }
            }
            currentY += cardHeight + 5.0f;
        }
    }

    private void handleModalClick(double mouseX, double mouseY, MouseButton button, float menuX, float menuY, float menuW, float menuH) {
        float modalW = 160.0f;
        float modalH = this.activeModal == ModalType.RESET ? 75.0f : 85.0f;
        float modalX = menuX + (menuW - modalW) / 2.0f;
        float modalY = menuY + (menuH - modalH) / 2.0f;

        float btnW = 56.0f;
        float btnH = 14.0f;
        float btnY = modalY + modalH - btnH - 8.0f;

        // Cancel Button
        float cancelX = modalX + 18.0f;
        if (GuiUtility.isHovered((double)cancelX, (double)btnY, btnW, btnH, mouseX, mouseY)) {
            this.closeModal();
            this.playClickSound();
            return;
        }

        // Confirm Button
        float confirmX = modalX + modalW - btnW - 18.0f;
        if (GuiUtility.isHovered((double)confirmX, (double)btnY, btnW, btnH, mouseX, mouseY)) {
            if (this.activeModal == ModalType.RESET) {
                this.resetAllSettings();
            } else if (this.activeModal == ModalType.CREATE) {
                this.tryCreateConfig();
            }
            this.closeModal();
            this.playClickSound();
            return;
        }

        // TextFields click
        if (this.activeModal == ModalType.CREATE) {
            this.createField.onMouseClicked(mouseX, mouseY, button);
        }
    }

    private String sanitizeConfigName(String raw) {
        if (raw == null) {
            return "";
        }
        // Strip characters invalid in file names: \ / : * ? " < > |
        return raw.trim().replaceAll("[\\\\/:*?\"<>|]", "");
    }

    private boolean tryCreateConfig() {
        String name = this.sanitizeConfigName(this.createField.getBuiltText());
        if (name.isEmpty()) {
            DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.ERROR, "Введите название конфига");
            return false;
        }
        if (name.equalsIgnoreCase("autosave")) {
            DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.ERROR, "\"autosave\" — зарезервированное имя");
            return false;
        }

        ConfigManager configManager = DefineVisuals.getInstance().getConfigManager();
        if (configManager.getConfig(name, true) != null) {
            DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.ERROR, "Конфиг \"" + name + "\" уже существует");
            return false;
        }

        configManager.createConfig(name);
        DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.SUCCESS, "Конфиг " + name + " создан!");
        return true;
    }

    private void closeModal() {
        this.activeModal = ModalType.NONE;
        this.createField.setFocused(false);
    }

    private void resetAllSettings() {
        for (Module module : DefineVisuals.getInstance().getModuleManager().getModules()) {
            if (!(module instanceof MenuModule)) {
                module.setEnabled(false, false);
                module.setKey(-1);
            }
        }
        LocalCosmetics.clearAll();
        ConfigManager configManager = DefineVisuals.getInstance().getConfigManager();
        configManager.setCurrent(null);
        ConfigFile autosave = configManager.getConfig("autosave");
        if (autosave != null) {
            autosave.save();
        }
        DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.SUCCESS, "Настройки сброшены к заводским");
    }

    private void playClickSound() {
        Sounds sounds = DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class);
        if (sounds != null && sounds.isEnabled()) {
            ClientSounds.MODULE.play(sounds.getVolume().getCurrentValue(), 1.0f);
        }
    }

    private void syncCardStates(List<ConfigFile> configs) {
        this.cardStates.removeIf(state -> !configs.contains(state.configFile));
        for (ConfigFile cfg : configs) {
            if (this.cardStates.stream().noneMatch(s -> s.configFile == cfg)) {
                this.cardStates.add(new ConfigCardState(cfg));
            }
        }
    }

    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        if (this.createField.isFocused()) this.createField.onMouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.isModalOpen()) {
            this.scrollHandler.scroll(verticalAmount);
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.activeModal == ModalType.CREATE && this.createField.isFocused()) {
            if (keyCode == 257) { // Enter
                this.tryCreateConfig();
                this.closeModal();
                return true;
            }
            this.createField.onKeyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        if (this.isModalOpen() && keyCode == 256) { // Escape
            this.closeModal();
            return true;
        }
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.activeModal == ModalType.CREATE && this.createField.isFocused()) {
            this.createField.charTyped(chr, modifiers);
            return true;
        }
        return false;
    }
}
