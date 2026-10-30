package moscow.definevisuals.ui.menu.modern.components;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.framework.style.InterfaceStyleRenderer;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.modules.other.HolyEvents;
import moscow.definevisuals.ui.components.IosSwitch;
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
import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModernEventsPanel implements IMinecraft {
    private static final float FILTER_CARD_HEIGHT = 15.0f;
    private static final float FILTER_ROW_STEP = 18.0f;
    private final Gson gson = new Gson();
    private final HolyEvents module;
    private Map<String, JsonArray> eventsData = new HashMap<>();
    private volatile boolean isLoading = true;
    private volatile String errorMessage = null;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 30_000;

    private final ScrollHandler scrollHandler = new ScrollHandler();
    private final ScrollHandler settingsScrollHandler = new ScrollHandler();

    private final IosSwitch switchAnarchy120 = new IosSwitch(200L);
    private final IosSwitch switchAnarchy116 = new IosSwitch(200L);
    private final IosSwitch switchTrioLight = new IosSwitch(200L);
    private final IosSwitch switchClanLight = new IosSwitch(200L);
    private final IosSwitch switchSoloLight = new IosSwitch(200L);
    private final IosSwitch switchDuoLight = new IosSwitch(200L);
    
    private float layoutHeaderY;
    private float layoutSettingsY;
    private float layoutSettingsListY;
    private float layoutSettingsHeight;
    private float layoutRefreshX;
    private float layoutRefreshY;
    private float layoutRefreshWidth;
    private float layoutEventsListY;
    private float layoutEventsListHeight;
    private float layoutEventsListX;
    private float layoutEventsListWidth;
    private float layoutSettingWidth;
    
    private static final ExecutorService NETWORK_EXECUTOR = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "HolyEvents-Network");
        t.setDaemon(true);
        return t;
    });

    private final List<RenderedEventCard> renderedCards = new ArrayList<>();

    private enum Rarity {
        DEADLY(7, "СМЕРТЕЛЬНЫЙ", new ColorRGBA(230, 45, 45, 255)),
        EXPLOSIVE(6, "ВЗРЫВНОЙ", new ColorRGBA(255, 120, 30, 255)),
        LEGEND(5, "ЛЕГЕНДАРНЫЙ", new ColorRGBA(255, 190, 25, 255)),
        MYTHIC(4, "МИФИЧЕСКИЙ", new ColorRGBA(215, 55, 255, 255)),
        EPIC(3, "ЭПИЧЕСКИЙ", new ColorRGBA(155, 45, 255, 255)),
        RARE(2, "РЕДКИЙ", new ColorRGBA(55, 135, 255, 255)),
        PEACEFUL(1, "МИРНЫЙ", new ColorRGBA(45, 210, 65, 255)),
        NORMAL(0, "ОБЫЧНЫЙ", new ColorRGBA(150, 185, 160, 255)),
        SHIP_ROSKOSHNI(3, "РОСКОШНЫЙ", new ColorRGBA(255, 215, 0, 255)),
        SHIP_ZAJIT(2, "ЗАЖИТОЧНЫЙ", new ColorRGBA(225, 175, 45, 255)),
        SHIP_DEFAULT(1, "ОБЫЧНЫЙ КОРАБЛЬ", new ColorRGBA(176, 196, 222, 255));

        final int priority;
        final String display;
        final ColorRGBA color;

        Rarity(int priority, String display, ColorRGBA color) {
            this.priority = priority;
            this.display = display;
            this.color = color;
        }
    }

    private static final Map<String, String> SERVER_NAME_MAPPING = new HashMap<>();
    static {
        for (int i = 1; i <= 74; i++) SERVER_NAME_MAPPING.put("LITE_ANARCHY_" + i, "Анархия #" + i);
        SERVER_NAME_MAPPING.put("LITE_NEW_ANARCHY_1", "1.20 Анархия #1");
        SERVER_NAME_MAPPING.put("LITE_NEW_ANARCHY_2", "1.20 Анархия #2");
        SERVER_NAME_MAPPING.put("LITE_NEW_ANARCHY_3", "1.20 Анархия #3");
    }

    private static class RenderedEventCard {
        final String serverName;
        final float x;
        final float y;
        final float width;
        final float height;

        RenderedEventCard(String serverName, float x, float y, float width, float height) {
            this.serverName = serverName;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean isHovered(double mouseX, double mouseY) {
            return GuiUtility.isHovered(this.x, this.y, this.width, this.height, mouseX, mouseY);
        }
    }

    public ModernEventsPanel() {
        this.module = DefineVisuals.getInstance().getModuleManager().getModule(HolyEvents.class);
        this.scrollHandler.setSpeed(28.0);
        this.settingsScrollHandler.setSpeed(20.0);
        this.loadEventData();
    }

    public void render(UIContext context, float x, float y, float width, float height, float menuAlpha) {
        this.scrollHandler.update();
        this.settingsScrollHandler.update();

        float contentX = x + 10.0f;
        float contentWidth = width - 20.0f;
        this.layoutHeaderY = y + 9.0f;
        this.layoutSettingsY = y + 42.0f;
        this.layoutSettingsListY = this.layoutSettingsY + 10.0f;
        this.layoutSettingsHeight = FILTER_CARD_HEIGHT + FILTER_ROW_STEP * 2.0f;

        if (!this.isLoading && System.currentTimeMillis() - this.lastUpdateTime > UPDATE_INTERVAL_MS) {
            this.loadEventData();
        }

        ColorRGBA accent = Colors.getAccent();
        ColorRGBA textColor = Colors.getTextColor();

        float iconSize = 20.0f;
        InterfaceStyleRenderer.drawChip(context, contentX, this.layoutHeaderY, iconSize, iconSize, BorderRadius.all(5.0f), menuAlpha, 0.28f);
        context.drawSprite(CustomSprite.EVENTS, contentX + 5.5f, this.layoutHeaderY + 5.5f, 9.0f, 9.0f, accent);

        float titleX = contentX + iconSize + 7.0f;
        Font titleFont = Fonts.SEMIBOLD.getFont(8.5f);
        Font subtitleFont = Fonts.REGULAR.getFont(5.5f);
        context.drawText(titleFont, "Holy Events", titleX, this.layoutHeaderY + 2.0f, textColor);
        context.drawText(subtitleFont, Localizator.translate("События HolyWorld в реальном времени"), titleX, this.layoutHeaderY + 12.5f, textColor.mulAlpha(0.42f));

        float refreshW = 48.0f;
        float refreshH = 16.0f;
        float refreshX = x + width - 10.0f - refreshW;
        float refreshY = this.layoutHeaderY + 2.0f;
        this.layoutRefreshX = refreshX;
        this.layoutRefreshY = refreshY;
        this.layoutRefreshWidth = refreshW;
        boolean refreshHov = GuiUtility.isHovered(refreshX, refreshY, refreshW, refreshH, context);
        if (refreshHov) CursorUtility.set(CursorType.HAND);

        InterfaceStyleRenderer.drawChip(context, refreshX, refreshY, refreshW, refreshH, BorderRadius.all(4.0f), menuAlpha, refreshHov ? 0.34f : 0.12f);
        Font refreshFont = Fonts.MEDIUM.getFont(5.5f);
        context.drawCenteredText(refreshFont, Localizator.translate("Обновить"), refreshX + refreshW / 2.0f, centeredTextY(refreshFont, refreshY, refreshH), refreshHov ? accent : textColor.mulAlpha(0.78f));

        int totalCount = this.countVisibleEvents();
        String badgeText = this.isLoading ? "..." : String.valueOf(totalCount);
        Font badgeFont = Fonts.MEDIUM.getFont(5.5f);
        float badgeW = Math.max(25.0f, badgeFont.width(badgeText) + 17.0f);
        float badgeH = refreshH;
        float badgeX = refreshX - badgeW - 5.0f;
        float badgeY = refreshY;

        InterfaceStyleRenderer.drawChip(context, badgeX, badgeY, badgeW, badgeH, BorderRadius.all(4.0f), menuAlpha, totalCount > 0 ? 0.20f : 0.08f);
        ColorRGBA dotColor = this.isLoading ? new ColorRGBA(255, 185, 30, 255) : (totalCount > 0 ? new ColorRGBA(50, 220, 80, 255) : textColor.mulAlpha(0.5f));
        context.drawRoundedRect(badgeX + 5.0f, badgeY + badgeH / 2.0f - 1.5f, 3.0f, 3.0f, BorderRadius.all(1.5f), dotColor);
        float badgeTextCenterX = badgeX + 11.0f + (badgeW - 11.0f) / 2.0f;
        context.drawCenteredText(badgeFont, badgeText, badgeTextCenterX, centeredTextY(badgeFont, badgeY, badgeH), textColor.mulAlpha(0.82f));

        float settingsY = this.layoutSettingsY;
        Font sectionFont = Fonts.MEDIUM.getFont(5.5f);
        context.drawText(sectionFont, Localizator.translate("Фильтры серверов"), contentX, settingsY, textColor.mulAlpha(0.42f));

        float settingsListY = this.layoutSettingsListY;
        float settingsHeight = this.layoutSettingsHeight;

        ScissorUtility.push(context.getMatrices(), contentX, settingsListY, contentWidth, settingsHeight);

        float currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();
        float settingW = (contentWidth - 4.0f) / 2.0f;
        this.layoutSettingWidth = settingW;
        float colGap = 4.0f;

        // Левая колонка
        this.renderSettingToggle(context, contentX, currentSettingY, settingW, FILTER_CARD_HEIGHT, "1.20 Анархия", this.module.showAnarchy120.isEnabled(), this.switchAnarchy120, accent, textColor);
        currentSettingY += FILTER_ROW_STEP;
        this.renderSettingToggle(context, contentX, currentSettingY, settingW, FILTER_CARD_HEIGHT, "1.16 Анархия", this.module.showAnarchy116.isEnabled(), this.switchAnarchy116, accent, textColor);
        currentSettingY += FILTER_ROW_STEP;
        this.renderSettingToggle(context, contentX, currentSettingY, settingW, FILTER_CARD_HEIGHT, "Трио Лайт · 20–29", this.module.showTrioLight.isEnabled(), this.switchTrioLight, accent, textColor);

        // Правая колонка
        currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();
        this.renderSettingToggle(context, contentX + settingW + colGap, currentSettingY, settingW, FILTER_CARD_HEIGHT, "Клан Лайт · 30–36", this.module.showClanLight.isEnabled(), this.switchClanLight, accent, textColor);
        currentSettingY += FILTER_ROW_STEP;
        this.renderSettingToggle(context, contentX + settingW + colGap, currentSettingY, settingW, FILTER_CARD_HEIGHT, "Соло Лайт · 1–9", this.module.showSoloLight.isEnabled(), this.switchSoloLight, accent, textColor);
        currentSettingY += FILTER_ROW_STEP;
        this.renderSettingToggle(context, contentX + settingW + colGap, currentSettingY, settingW, FILTER_CARD_HEIGHT, "Дуо Лайт · 10–19", this.module.showDuoLight.isEnabled(), this.switchDuoLight, accent, textColor);

        this.settingsScrollHandler.setMax(0.0f);
        ScissorUtility.pop();

        float eventsHeaderY = settingsListY + settingsHeight + 8.0f;
        context.drawText(sectionFont, Localizator.translate("Активные события"), contentX, eventsHeaderY, textColor.mulAlpha(0.42f));

        float listY = eventsHeaderY + 11.0f;
        float listHeight = height - (listY - y) - 9.0f;
        float cardWidth = contentWidth;
        this.layoutEventsListX = contentX;
        this.layoutEventsListY = listY;
        this.layoutEventsListWidth = cardWidth;
        this.layoutEventsListHeight = listHeight;

        ScissorUtility.push(context.getMatrices(), contentX, listY, cardWidth, listHeight);

        float currentY = listY - (float)this.scrollHandler.getValue();
        float totalListHeight = 0.0f;
        this.renderedCards.clear();

        if (this.isLoading) {
            this.renderLoading(context, contentX + cardWidth / 2.0f, listY + listHeight / 2.0f - 10.0f, cardWidth, textColor, accent);
            totalListHeight = listHeight;
        } else if (this.errorMessage != null) {
            this.renderError(context, contentX + cardWidth / 2.0f, listY + listHeight / 2.0f - 10.0f, cardWidth, textColor);
            totalListHeight = listHeight;
        } else {
            Map<Rarity, List<EventInfo>> eventsByRarity = new LinkedHashMap<>();
            for (Rarity rarity : Rarity.values()) {
                eventsByRarity.put(rarity, new ArrayList<>());
            }

            for (Map.Entry<String, JsonArray> entry : this.eventsData.entrySet()) {
                String server = entry.getKey();
                JsonArray arr = entry.getValue();
                if (arr == null) continue;
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject ev = arr.get(i).getAsJsonObject();
                    if (ev == null) continue;
                    JsonObject meta = ev.has("metadata") ? ev.getAsJsonObject("metadata") : null;
                    if (meta == null) continue;

                    String name = meta.has("displayName") ? meta.get("displayName").getAsString() : "";
                    String rare = meta.has("rare") ? meta.get("rare").getAsString() : "normal";

                    if (!this.shouldShowServer(server)) continue;

                    EventInfo info = new EventInfo(server, name, rare);
                    Rarity rarity = this.parseRarity(rare);
                    eventsByRarity.get(rarity).add(info);
                }
            }

            for (Rarity rarity : eventsByRarity.keySet()) {
                eventsByRarity.get(rarity).sort(Comparator.comparing((EventInfo e) -> e.displayName));
            }

            float cardH = 36.0f;
            float gap = 4.0f;
            boolean hasAnyEvents = false;
            Font groupFont = Fonts.SEMIBOLD.getFont(5.5f);

            for (Rarity rarity : Rarity.values()) {
                List<EventInfo> events = eventsByRarity.get(rarity);
                if (events.isEmpty()) continue;

                hasAnyEvents = true;

                float groupHeight = 13.0f;
                if (currentY + groupHeight > listY && currentY < listY + listHeight) {
                    float groupTextY = centeredTextY(groupFont, currentY, groupHeight);
                    context.drawRoundedRect(contentX + 1.0f, currentY + groupHeight / 2.0f - 1.5f, 3.0f, 3.0f, BorderRadius.all(1.5f), rarity.color);
                    context.drawText(groupFont, Localizator.translate(rarity.display), contentX + 8.0f, groupTextY, rarity.color);
                    context.drawRightText(groupFont, String.valueOf(events.size()), contentX + cardWidth - 2.0f, groupTextY, textColor.mulAlpha(0.38f));
                }
                currentY += groupHeight;
                totalListHeight += groupHeight;

                for (EventInfo ev : events) {
                    this.renderedCards.add(new RenderedEventCard(ev.serverName, contentX, currentY, cardWidth, cardH));
                    if (currentY + cardH > listY && currentY < listY + listHeight) {
                        this.renderEventCard(context, ev, contentX, currentY, cardWidth, cardH, accent, textColor);
                    }
                    currentY += cardH + gap;
                    totalListHeight += cardH + gap;
                }
                currentY += 4.0f;
                totalListHeight += 4.0f;
            }

            if (!hasAnyEvents) {
                this.renderEmptyState(context, contentX + cardWidth / 2.0f, listY + listHeight / 2.0f - 10.0f, cardWidth, textColor);
                totalListHeight = listHeight;
            }

            totalListHeight += 4.0f;
        }

        float maxScroll = -Math.max(0.0f, totalListHeight - listHeight);
        this.scrollHandler.setMax(maxScroll);

        ScissorUtility.pop();
    }

    private void renderSettingToggle(UIContext context, float x, float y, float w, float h, String text, boolean enabled, IosSwitch iosSwitch, ColorRGBA accent, ColorRGBA textColor) {
        boolean hovered = GuiUtility.isHovered(x, y, w, h, context);
        if (hovered) CursorUtility.set(CursorType.HAND);

        iosSwitch.update(enabled);

        InterfaceStyleRenderer.drawCard(
                context,
                 x,
                 y,
                 w,
                 h,
                 2.5f,
                 BorderRadius.all(4.0f),
                 1.0f,
                 iosSwitch.getValue(),
                 hovered ? 1.0f : 0.0f
        );

        float switchX = x + w - 18.0f;
        float switchY = y + (h - 8.0f) / 2.0f;
        IosSwitch.render(context, switchX, switchY, iosSwitch.getValue(), 1.0f, accent);

        ColorRGBA nameColor = textColor.mulAlpha(0.6f + 0.4f * iosSwitch.getValue());
        Font nameFont = Fonts.MEDIUM.getFont(5.5f);
        context.drawFadeoutText(nameFont, Localizator.translate(text), x + 6.0f, centeredTextY(nameFont, y, h), nameColor, 0.8f, 1.0f, w - 29.0f);
    }

    private void renderEventCard(UIContext context, EventInfo ev, float x, float y, float w, float h, ColorRGBA accent, ColorRGBA textColor) {
        boolean hovered = GuiUtility.isHovered(x, y, w, h, context);
        if (hovered) CursorUtility.set(CursorType.HAND);

        Rarity rarity = this.parseRarity(ev.rareValue);

        BorderRadius cardRadius = BorderRadius.all(5.0f);
        InterfaceStyleRenderer.drawCard(context, x, y, w, h, 3.0f, cardRadius, 1.0f, 0.0f, hovered ? 1.0f : 0.0f);
        context.drawRoundedRect(x + 3.0f, y + 5.0f, 2.0f, h - 10.0f, BorderRadius.all(1.0f), rarity.color.mulAlpha(0.9f));

        Font rarityFont = Fonts.SEMIBOLD.getFont(5.5f);
        String rarityText = Localizator.translate(rarity.display);
        float rW = rarityFont.width(rarityText) + 10.0f;
        float rH = 11.0f;
        float rX = x + w - rW - 6.0f;
        float rY = y + 5.0f;
        context.drawRoundedRect(rX, rY, rW, rH, BorderRadius.all(3.0f), rarity.color.withAlpha(hovered ? 52.0f : 32.0f));
        context.drawCenteredText(rarityFont, rarityText, rX + rW / 2.0f, centeredTextY(rarityFont, rY, rH), rarity.color);

        Font titleFont = Fonts.SEMIBOLD.getFont(7.0f);
        float titleX = x + 10.0f;
        float titleWidth = Math.max(18.0f, rX - titleX - 5.0f);
        context.drawFadeoutText(titleFont, ev.displayName, titleX, centeredTextY(titleFont, y + 4.0f, 13.0f), textColor, 0.85f, 1.0f, titleWidth);

        String actionText = Localizator.translate("Перейти");
        Font actionFont = Fonts.MEDIUM.getFont(5.5f);
        float aW = actionFont.width(actionText) + 10.0f;
        float aH = 11.0f;
        float aX = x + w - aW - 6.0f;
        float aY = y + 20.0f;

        context.drawRoundedRect(aX, aY, aW, aH, BorderRadius.all(3.0f), accent.withAlpha(hovered ? 52.0f : 24.0f));
        context.drawCenteredText(actionFont, actionText, aX + aW / 2.0f, centeredTextY(actionFont, aY, aH), accent);

        String server = this.getReadableServerName(ev.serverName);
        Font serverFont = Fonts.REGULAR.getFont(5.5f);
        float serverX = x + 10.0f;
        float serverY = y + 20.0f;
        context.drawRoundedRect(serverX, serverY + aH / 2.0f - 1.5f, 3.0f, 3.0f, BorderRadius.all(1.5f), textColor.mulAlpha(0.34f));
        context.drawFadeoutText(serverFont, server, serverX + 6.0f, centeredTextY(serverFont, serverY, aH), textColor.mulAlpha(0.58f), 0.8f, 1.0f, Math.max(10.0f, aX - serverX - 10.0f));
    }

    private void renderLoading(UIContext context, float x, float y, float w, ColorRGBA textColor, ColorRGBA accent) {
        context.drawCenteredText(Fonts.MEDIUM.getFont(7.5f), Localizator.translate("Загрузка событий..."), x, y - 6.0f, textColor.mulAlpha(0.8f));
        float alpha = (float) (0.4 + 0.6 * Math.abs(Math.sin(System.currentTimeMillis() / 250.0)));
        context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), Localizator.translate("Подключение к HolyWorld API"), x, y + 8.0f, accent.mulAlpha(alpha));
    }

    private void renderError(UIContext context, float x, float y, float w, ColorRGBA textColor) {
        context.drawCenteredText(Fonts.MEDIUM.getFont(7.5f), Localizator.translate("Не удалось загрузить события"), x, y - 8.0f, new ColorRGBA(255, 90, 90, 255));
        context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), this.errorMessage != null ? this.errorMessage : "Ошибка соединения", x, y + 8.0f, textColor.mulAlpha(0.6f));
    }

    private void renderEmptyState(UIContext context, float x, float y, float w, ColorRGBA textColor) {
        context.drawCenteredText(Fonts.MEDIUM.getFont(8.0f), Localizator.translate("Нет активных событий"), x, y - 6.0f, textColor.mulAlpha(0.6f));
        context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), Localizator.translate("Проверьте фильтры серверов"), x, y + 8.0f, textColor.mulAlpha(0.4f));
    }

    private static float centeredTextY(Font font, float y, float height) {
        return y + GuiUtility.getMiddleOfBox(font.height(), height) - 0.5f;
    }

    private void loadEventData() {
        this.isLoading = true;
        this.errorMessage = null;
        this.lastUpdateTime = System.currentTimeMillis();

        CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://api.holyworld.me/v1/events");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(5_000);
                connection.setReadTimeout(5_000);

                int code = connection.getResponseCode();
                if (code == 200) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) response.append(line);

                        JsonObject json = this.gson.fromJson(response.toString(), JsonObject.class);
                        if (json != null) {
                            Map<String, JsonArray> parsed = new HashMap<>();
                            for (String key : json.keySet()) {
                                JsonArray arr = json.getAsJsonArray(key);
                                if (arr != null) parsed.put(key, arr);
                            }
                            MinecraftClient.getInstance().execute(() -> {
                                this.eventsData = parsed;
                                this.lastUpdateTime = System.currentTimeMillis();
                                this.isLoading = false;
                            });
                        } else {
                            this.setError("Некорректный ответ JSON");
                        }
                    }
                } else {
                    this.setError("HTTP Ошибка: " + code);
                }
            } catch (Exception e) {
                this.setError("Ошибка сети: " + (e.getMessage() != null ? e.getMessage() : "Unknown"));
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ignored) {}
                }
            }
        }, NETWORK_EXECUTOR);
    }

    private void setError(String msg) {
        MinecraftClient.getInstance().execute(() -> {
            this.errorMessage = msg;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isLoading = false;
        });
    }

    private int countVisibleEvents() {
        int count = 0;
        for (Map.Entry<String, JsonArray> entry : this.eventsData.entrySet()) {
            if (!this.shouldShowServer(entry.getKey())) continue;
            JsonArray arr = entry.getValue();
            if (arr != null) count += arr.size();
        }
        return count;
    }

    private Rarity parseRarity(String raw) {
        if (raw == null) return Rarity.NORMAL;
        String l = raw.toLowerCase();
        if (l.contains("deadly")) return Rarity.DEADLY;
        if (l.contains("explosive")) return Rarity.EXPLOSIVE;
        if (l.contains("legend")) return Rarity.LEGEND;
        if (l.contains("mythic")) return Rarity.MYTHIC;
        if (l.contains("epic")) return Rarity.EPIC;
        if (l.contains("rare")) return Rarity.RARE;
        if (l.contains("peaceful")) return Rarity.PEACEFUL;
        if (l.contains("ship_roskoshni")) return Rarity.SHIP_ROSKOSHNI;
        if (l.contains("ship_zajit")) return Rarity.SHIP_ZAJIT;
        if (l.contains("ship_default")) return Rarity.SHIP_DEFAULT;
        return Rarity.NORMAL;
    }

    private String getReadableServerName(String id) {
        return SERVER_NAME_MAPPING.getOrDefault(id, id.replace("_", " "));
    }

    private boolean shouldShowServer(String serverName) {
        String s = serverName.toLowerCase();
        if (s.contains("lite_new_anarchy")) {
            return this.module.showAnarchy120.isEnabled();
        }
        if (s.contains("lite_anarchy_")) {
            try {
                String[] parts = s.split("_");
                if (parts.length >= 3) {
                    int serverNum = Integer.parseInt(parts[2]);
                    if (serverNum >= 1 && serverNum <= 9) {
                        return this.module.showSoloLight.isEnabled();
                    }
                    if (serverNum >= 10 && serverNum <= 19) {
                        return this.module.showDuoLight.isEnabled();
                    }
                    if (serverNum >= 20 && serverNum <= 29) {
                        return this.module.showTrioLight.isEnabled();
                    }
                    if (serverNum >= 30 && serverNum <= 36) {
                        return this.module.showClanLight.isEnabled();
                    }
                    return this.module.showAnarchy116.isEnabled();
                }
            } catch (NumberFormatException ignored) {
                return this.module.showAnarchy116.isEnabled();
            }
        }
        return true;
    }

    private static class EventInfo {
        String serverName, displayName, rareValue;
        EventInfo(String s, String d, String r) {
            this.serverName = s;
            this.displayName = d;
            this.rareValue = r;
        }
    }

    public void onMouseClicked(double mouseX, double mouseY, MouseButton button, float x, float y, float width, float height) {
        if (GuiUtility.isHovered(this.layoutRefreshX, this.layoutRefreshY, this.layoutRefreshWidth, 16.0f, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.loadEventData();
            UiSound.CLICK.play();
            return;
        }

        float contentX = x + 10.0f;
        float settingsListY = this.layoutSettingsListY;
        float settingW = this.layoutSettingWidth;
        float colGap = 4.0f;
        float currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();

        // Левая колонка
        if (GuiUtility.isHovered(contentX, currentSettingY, settingW, FILTER_CARD_HEIGHT, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showAnarchy120.toggle();
            (this.module.showAnarchy120.isEnabled() ? UiSound.TOGGLE_ON : UiSound.TOGGLE_OFF).play();
            return;
        }
        currentSettingY += FILTER_ROW_STEP;
        if (GuiUtility.isHovered(contentX, currentSettingY, settingW, FILTER_CARD_HEIGHT, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showAnarchy116.toggle();
            (this.module.showAnarchy116.isEnabled() ? UiSound.TOGGLE_ON : UiSound.TOGGLE_OFF).play();
            return;
        }
        currentSettingY += FILTER_ROW_STEP;
        if (GuiUtility.isHovered(contentX, currentSettingY, settingW, FILTER_CARD_HEIGHT, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showTrioLight.toggle();
            (this.module.showTrioLight.isEnabled() ? UiSound.TOGGLE_ON : UiSound.TOGGLE_OFF).play();
            return;
        }

        // Правая колонка
        float rightX = contentX + settingW + colGap;
        currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();
        if (GuiUtility.isHovered(rightX, currentSettingY, settingW, FILTER_CARD_HEIGHT, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showClanLight.toggle();
            (this.module.showClanLight.isEnabled() ? UiSound.TOGGLE_ON : UiSound.TOGGLE_OFF).play();
            return;
        }
        currentSettingY += FILTER_ROW_STEP;
        if (GuiUtility.isHovered(rightX, currentSettingY, settingW, FILTER_CARD_HEIGHT, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showSoloLight.toggle();
            (this.module.showSoloLight.isEnabled() ? UiSound.TOGGLE_ON : UiSound.TOGGLE_OFF).play();
            return;
        }
        currentSettingY += FILTER_ROW_STEP;
        if (GuiUtility.isHovered(rightX, currentSettingY, settingW, FILTER_CARD_HEIGHT, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showDuoLight.toggle();
            (this.module.showDuoLight.isEnabled() ? UiSound.TOGGLE_ON : UiSound.TOGGLE_OFF).play();
            return;
        }

        if ((button == MouseButton.LEFT || button == MouseButton.RIGHT)
                && GuiUtility.isHovered(this.layoutEventsListX, this.layoutEventsListY, this.layoutEventsListWidth, this.layoutEventsListHeight, mouseX, mouseY)) {
            for (RenderedEventCard card : this.renderedCards) {
                if (!card.isHovered(mouseX, mouseY)) continue;
                this.connectToServer(card.serverName);
                return;
            }
        }
    }

    private void connectToServer(String serverName) {
        if (serverName == null || serverName.isEmpty()) return;
        String s = serverName.toLowerCase();
        if (s.startsWith("lite_anarchy_")) {
            String num = s.replace("lite_anarchy_", "");
            String prefix = DefineVisuals.getInstance().getCommandManager().getPrefix();
            DefineVisuals.getInstance().getCommandManager().dispatch(prefix + "rct " + num);
        } else if (s.startsWith("lite_new_anarchy_")) {
            String num = s.replace("lite_new_anarchy_", "");
            if (mc.player != null) {
                mc.player.networkHandler.sendChatCommand("an20 " + num);
            }
        }
        UiSound.NAVIGATION.play();
    }

    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseY >= this.layoutSettingsListY && mouseY <= this.layoutSettingsListY + this.layoutSettingsHeight) {
            this.settingsScrollHandler.scroll(verticalAmount);
        } else {
            this.scrollHandler.scroll(verticalAmount);
        }
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.settingsScrollHandler.onKeyPressed(keyCode);
        this.scrollHandler.onKeyPressed(keyCode);
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
}
