package moscow.definevisuals.ui.menu.modern.components;

import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.framework.style.InterfaceStyleRenderer;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.notifications.NotificationType;
import moscow.definevisuals.systems.theme.Theme;
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
import moscow.definevisuals.utility.sounds.UiSound;
import moscow.definevisuals.cosmetics.LocalCosmetics;

public class ModernCosmeticsPanel implements IMinecraft {

    private static class FilterTab {
        final String id;
        final String label;
        final Animation hoverAnim = new Animation(200L, Easing.QUAD_OUT);

        FilterTab(String id, String label) {
            this.id = id;
            this.label = label;
        }
    }

    private static class CosmeticCardState {
        final int index;
        final String name;
        final String type;
        final Animation hoverAnim = new Animation(200L, Easing.QUAD_OUT);
        final Animation selectAnim = new Animation(250L, Easing.FIGMA_EASE_IN_OUT);

        CosmeticCardState(int index, String name, String type) {
            this.index = index;
            this.name = name;
            this.type = type;
        }
    }

    private final List<FilterTab> filterTabs = List.of(
            new FilterTab("wings", "Крылья"),
            new FilterTab("hat", "Шляпы"),
            new FilterTab("pet", "Питомцы"),
            new FilterTab("bodywear", "Тело")
    );

    private String currentFilter = "wings";
    private final ScrollHandler scrollHandler = new ScrollHandler();
    private final List<CosmeticCardState> allCards = new ArrayList<>();

    public ModernCosmeticsPanel() {
        this.initCards();
    }

    private void initCards() {
        this.allCards.clear();
        int count = LocalCosmetics.size();
        for (int i = 0; i < count; i++) {
            this.allCards.add(new CosmeticCardState(i, LocalCosmetics.name(i), LocalCosmetics.type(i)));
        }
    }

    public void render(UIContext context, float x, float y, float width, float height, float menuAlpha) {
        if (this.allCards.size() != LocalCosmetics.size()) {
            this.initCards();
        }

        this.scrollHandler.update();

        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        ColorRGBA accent = Colors.getAccent();
        ColorRGBA textColor = Colors.getTextColor();

        // 1. Header Bar
        float headerY = y + 14.0f;
        float headerX = x + 12.0f;

        // Title: [Visuals Icon] Косметика
        context.drawSprite(CustomSprite.VISUALS, headerX, headerY + 1.0f, 9.0f, 9.0f, textColor);
        context.drawText(Fonts.SEMIBOLD.getFont(9.0f), Localizator.translate("Косметика"), headerX + 13.0f, headerY + 1.0f, textColor);

        // Header Action: Reset All ("Сбросить всё")
        float resetW = 60.0f;
        float resetH = 14.0f;
        float resetX = x + width - 14.0f - resetW;
        float resetY = headerY - 1.0f;
        boolean resetHov = GuiUtility.isHovered((double)resetX, (double)resetY, resetW, resetH, (double)context.getMouseX(), (double)context.getMouseY());
        if (resetHov) CursorUtility.set(CursorType.HAND);

        ColorRGBA resetBg = resetHov ? accent.mulAlpha(0.35f) : Colors.getBackgroundColor().mulAlpha(dark ? 0.4f : 0.25f);
        context.drawSquircle(resetX, resetY, resetW, resetH, 3.0f, BorderRadius.all(3.0f), resetBg);
        context.drawCenteredText(Fonts.MEDIUM.getFont(5.5f), Localizator.translate("Сбросить всё"), resetX + resetW / 2.0f, resetY + 4.5f, resetHov ? accent : textColor.mulAlpha(0.75f));

        // 2. Filter Pills Bar
        float filterY = headerY + 20.0f;
        float currentFilterX = headerX;
        float filterH = 14.0f;

        for (FilterTab tab : this.filterTabs) {
            String tabLabel = Localizator.translate(tab.label);
            float textW = Fonts.MEDIUM.getFont(6.0f).width(tabLabel);
            float tabW = textW + 12.0f;

            boolean isFilterSelected = tab.id.equalsIgnoreCase(this.currentFilter);
            boolean isFilterHovered = GuiUtility.isHovered((double)currentFilterX, (double)filterY, tabW, filterH, (double)context.getMouseX(), (double)context.getMouseY());
            tab.hoverAnim.update(isFilterHovered);
            if (isFilterHovered) CursorUtility.set(CursorType.HAND);

            ColorRGBA tabBg = isFilterSelected 
                    ? accent.mulAlpha(0.25f) 
                    : Colors.getBackgroundColor().mulAlpha((dark ? 0.2f : 0.15f) + 0.1f * tab.hoverAnim.getValue());
            context.drawSquircle(currentFilterX, filterY, tabW, filterH, 3.0f, BorderRadius.all(3.0f), tabBg);

            ColorRGBA tabTextColor = isFilterSelected 
                    ? accent 
                    : textColor.mulAlpha(0.55f + 0.35f * tab.hoverAnim.getValue());
            context.drawCenteredText(Fonts.MEDIUM.getFont(6.0f), tabLabel, currentFilterX + tabW / 2.0f, filterY + 4.5f, tabTextColor);

            currentFilterX += tabW + 4.0f;
        }

        // 3. Grid of Cosmetic Cards
        float listY = filterY + filterH + 8.0f;
        float listHeight = height - (listY - y) - 10.0f;
        float totalWidth = width - 24.0f;
        float colGap = 6.0f;
        float cardWidth = (totalWidth - colGap) / 2.0f;
        float cardHeight = 28.0f;
        float rowGap = 5.0f;

        List<CosmeticCardState> visibleCards = this.allCards.stream()
                .filter(c -> c.type.equalsIgnoreCase(this.currentFilter))
                .toList();

        ScissorUtility.push(context.getMatrices(), headerX - 2.0f, listY, totalWidth + 4.0f, listHeight);

        float currentScroll = (float)this.scrollHandler.getValue();

        for (int i = 0; i < visibleCards.size(); i++) {
            CosmeticCardState card = visibleCards.get(i);
            int col = i % 2;
            int row = i / 2;

            float cardX = headerX + col * (cardWidth + colGap);
            float cardY = listY - currentScroll + row * (cardHeight + rowGap);

            boolean isHovered = GuiUtility.isHovered((double)cardX, (double)cardY, cardWidth, cardHeight, (double)context.getMouseX(), (double)context.getMouseY());
            card.hoverAnim.update(isHovered);
            if (isHovered) CursorUtility.set(CursorType.HAND);

            boolean isSelected = LocalCosmetics.isSelected(card.index);
            card.selectAnim.update(isSelected);

            BorderRadius cardRadius = BorderRadius.all(4.0f);
            InterfaceStyleRenderer.drawCard(
                    context,
                    cardX,
                    cardY,
                    cardWidth,
                    cardHeight,
                    3.0f,
                    cardRadius,
                    1.0f,
                    card.selectAnim.getValue(),
                    card.hoverAnim.getValue()
            );

            // Cosmetic Name
            context.drawFadeoutText(Fonts.MEDIUM.getFont(6.5f), card.name, cardX + 7.0f, cardY + 5.0f, textColor, 0.9f, 1.0f, cardWidth - 14.0f);

            // Subtitle: Type description and active status
            String typeLabel = getLocalizedType(card.type);
            String subInfo = isSelected ? typeLabel + " · " + Localizator.translate("Активно") : typeLabel;
            ColorRGBA subColor = isSelected ? accent.mulAlpha(0.9f) : textColor.mulAlpha(0.45f);
            context.drawText(Fonts.REGULAR.getFont(5.5f), subInfo, cardX + 7.0f, cardY + 16.5f, subColor);
        }

        int totalRows = (visibleCards.size() + 1) / 2;
        float totalListHeight = totalRows * (cardHeight + rowGap);
        float maxScroll = -Math.max(0.0f, totalListHeight - listHeight);
        this.scrollHandler.setMax(maxScroll);

        ScissorUtility.pop();
    }

    public void onMouseClicked(double mouseX, double mouseY, MouseButton button, float x, float y, float width, float height) {
        float headerY = y + 14.0f;
        float headerX = x + 12.0f;

        // Reset button click
        float resetW = 60.0f;
        float resetH = 14.0f;
        float resetX = x + width - 14.0f - resetW;
        float resetY = headerY - 1.0f;
        if (GuiUtility.isHovered((double)resetX, (double)resetY, resetW, resetH, mouseX, mouseY)) {
            LocalCosmetics.clearAll();
            UiSound.DELETE.play();
            DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.INFO, Localizator.translate("Косметика сброшена"));
            return;
        }

        // Filter tabs click
        float filterY = headerY + 20.0f;
        float currentFilterX = headerX;
        float filterH = 14.0f;

        for (FilterTab tab : this.filterTabs) {
            float textW = Fonts.MEDIUM.getFont(6.0f).width(Localizator.translate(tab.label));
            float tabW = textW + 12.0f;

            if (GuiUtility.isHovered((double)currentFilterX, (double)filterY, tabW, filterH, mouseX, mouseY)) {
                if (!this.currentFilter.equals(tab.id)) {
                    this.currentFilter = tab.id;
                    this.scrollHandler.setValue(0.0f);
                    this.scrollHandler.setTargetValue(0.0f);
                    UiSound.NAVIGATION.play();
                }
                return;
            }
            currentFilterX += tabW + 4.0f;
        }

        // Cards click
        float listY = filterY + filterH + 8.0f;
        float listHeight = height - (listY - y) - 10.0f;
        float totalWidth = width - 24.0f;
        float colGap = 6.0f;
        float cardWidth = (totalWidth - colGap) / 2.0f;
        float cardHeight = 28.0f;
        float rowGap = 5.0f;
        float currentScroll = (float)this.scrollHandler.getValue();

        List<CosmeticCardState> visibleCards = this.allCards.stream()
                .filter(c -> c.type.equalsIgnoreCase(this.currentFilter))
                .toList();

        for (int i = 0; i < visibleCards.size(); i++) {
            CosmeticCardState card = visibleCards.get(i);
            int col = i % 2;
            int row = i / 2;

            float cardX = headerX + col * (cardWidth + colGap);
            float cardY = listY - currentScroll + row * (cardHeight + rowGap);

            if (GuiUtility.isHovered((double)cardX, (double)cardY, cardWidth, cardHeight, mouseX, mouseY)) {
                LocalCosmetics.toggle(card.index);

                boolean nowSelected = LocalCosmetics.isSelected(card.index);
                (nowSelected ? UiSound.TOGGLE_ON : UiSound.TOGGLE_OFF).play();
                String msg = nowSelected 
                        ? "Выбрано: " + card.name + " (" + getLocalizedType(card.type) + ")"
                        : "Отключено: " + card.name;
                DefineVisuals.getInstance().getNotificationManager().addNotification(NotificationType.INFO, msg);
                return;
            }
        }
    }

    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scrollHandler.scroll(verticalAmount);
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    private static String getLocalizedType(String type) {
        if (type == null) return Localizator.translate("Другое");
        String label = switch (type.toLowerCase()) {
            case "wings" -> "Крылья";
            case "hat" -> "Шляпа";
            case "pet" -> "Питомец";
            case "bodywear" -> "Тело";
            default -> type;
        };
        return Localizator.translate(label);
    }
}
