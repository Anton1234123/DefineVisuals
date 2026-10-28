package moscow.definevisuals.ui.menu.modern.components;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.modules.modules.other.HolyEvents;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
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
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.ScissorUtility;
import moscow.definevisuals.utility.render.obj.CustomSprite;
import moscow.definevisuals.utility.sounds.ClientSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModernEventsPanel implements IMinecraft {
    private final Gson gson = new Gson();
    private final HolyEvents module;
    private Map<String, JsonArray> eventsData = new HashMap<>();
    private volatile boolean isLoading = true;
    private volatile String errorMessage = null;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 30_000;

    private final ScrollHandler scrollHandler = new ScrollHandler();
    private final ScrollHandler settingsScrollHandler = new ScrollHandler();
    
    private float layoutY;
    private float layoutHeaderY;
    private float layoutSettingsY;
    private float layoutSettingsListY;
    private float layoutSettingsHeight = 80.0f;
    
    private static final ExecutorService NETWORK_EXECUTOR = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "HolyEvents-Network");
        t.setDaemon(true);
        return t;
    });

    private final List<EventCardState> cardStates = new ArrayList<>();

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

    private static class EventCardState {
        final String displayName;
        final String serverName;
        final String rareValue;
        final Animation hoverAnim = new Animation(200L, Easing.QUAD_OUT);

        EventCardState(String displayName, String serverName, String rareValue) {
            this.displayName = displayName;
            this.serverName = serverName;
            this.rareValue = rareValue;
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
        
        this.layoutY = y;
        this.layoutHeaderY = y + 14.0f;
        this.layoutSettingsY = this.layoutHeaderY + 22.0f;
        this.layoutSettingsListY = this.layoutSettingsY + 10.0f;

        if (!this.isLoading && System.currentTimeMillis() - this.lastUpdateTime > UPDATE_INTERVAL_MS) {
            this.loadEventData();
        }

        boolean dark = DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK;
        ColorRGBA accent = Colors.getAccent();
        ColorRGBA textColor = Colors.getTextColor();

        float headerY = this.layoutHeaderY;
        float headerX = x + 12.0f;

        context.drawSprite(CustomSprite.EVENTS, headerX, headerY + 1.0f, 9.0f, 9.0f, textColor);
        context.drawText(Fonts.SEMIBOLD.getFont(9.0f), "HolyEvents", headerX + 13.0f, headerY + 1.0f, textColor);

        float refreshW = 50.0f;
        float refreshH = 14.0f;
        float refreshX = x + width - 14.0f - refreshW;
        float refreshY = headerY - 1.0f;
        boolean refreshHov = GuiUtility.isHovered((double)refreshX, (double)refreshY, refreshW, refreshH, (double)context.getMouseX(), (double)context.getMouseY());
        if (refreshHov) CursorUtility.set(CursorType.HAND);

        ColorRGBA refreshBg = refreshHov ? accent.mulAlpha(0.3f) : Colors.getBackgroundColor().mulAlpha(dark ? 0.4f : 0.25f);
        context.drawSquircle(refreshX, refreshY, refreshW, refreshH, 3.0f, BorderRadius.all(3.0f), refreshBg);
        context.drawCenteredText(Fonts.MEDIUM.getFont(5.5f), "Обновить", refreshX + refreshW / 2.0f, refreshY + 4.5f, refreshHov ? accent : textColor.mulAlpha(0.85f));

        int totalCount = this.countVisibleEvents();
        String badgeText = this.isLoading ? "Загрузка..." : totalCount + (totalCount == 1 ? " ивент" : (totalCount >= 2 && totalCount <= 4 ? " ивента" : " ивентов"));
        Font badgeFont = Fonts.MEDIUM.getFont(5.5f);
        float badgeW = badgeFont.width(badgeText) + 10.0f;
        float badgeH = 12.0f;
        float badgeX = refreshX - badgeW - 6.0f;
        float badgeY = headerY + 1.0f;

        context.drawRoundedRect(badgeX, badgeY, badgeW, badgeH, BorderRadius.all(3.0f), Colors.getBackgroundColor().withAlpha(140.0f));
        ColorRGBA dotColor = this.isLoading ? new ColorRGBA(255, 185, 30, 255) : (totalCount > 0 ? new ColorRGBA(50, 220, 80, 255) : textColor.mulAlpha(0.5f));
        context.drawRoundedRect(badgeX + 3.0f, badgeY + badgeH / 2.0f - 1.5f, 3.0f, 3.0f, BorderRadius.all(1.5f), dotColor);
        context.drawText(badgeFont, badgeText, badgeX + 9.0f, badgeY + 2.5f, textColor.mulAlpha(0.85f));

        float settingsY = this.layoutSettingsY;
        context.drawText(Fonts.MEDIUM.getFont(6.0f), "Фильтры серверов", headerX, settingsY, textColor.mulAlpha(0.35f));

        float settingsListY = this.layoutSettingsListY;
        float settingsHeight = this.layoutSettingsHeight;

        ScissorUtility.push(context.getMatrices(), headerX - 2.0f, settingsListY, width - 24.0f + 4.0f, settingsHeight);
        
        float currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();
        float settingW = (width - 24.0f - 4.0f) / 2.0f;
        float colGap = 4.0f;
        
        // Левая колонка
        this.renderSettingToggle(context, headerX, currentSettingY, settingW, "1.20 Анархия", this.module.showAnarchy120.isEnabled(), dark, accent, textColor);
        currentSettingY += 14.0f;
        this.renderSettingToggle(context, headerX, currentSettingY, settingW, "1.16 Анархия", this.module.showAnarchy116.isEnabled(), dark, accent, textColor);
        currentSettingY += 14.0f;
        this.renderSettingToggle(context, headerX, currentSettingY, settingW, "Трио Лайт (27-39)", this.module.showTrioLight.isEnabled(), dark, accent, textColor);
        
        // Правая колонка
        currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();
        this.renderSettingToggle(context, headerX + settingW + colGap, currentSettingY, settingW, "Клан Лайт (40-51)", this.module.showClanLight.isEnabled(), dark, accent, textColor);
        currentSettingY += 14.0f;
        this.renderSettingToggle(context, headerX + settingW + colGap, currentSettingY, settingW, "Соло Лайт (1-12)", this.module.showSoloLight.isEnabled(), dark, accent, textColor);
        currentSettingY += 14.0f;
        this.renderSettingToggle(context, headerX + settingW + colGap, currentSettingY, settingW, "Дуо Лайт (13-26)", this.module.showDuoLight.isEnabled(), dark, accent, textColor);

        this.settingsScrollHandler.setMax(0.0f);
        ScissorUtility.pop();

        float eventsHeaderY = this.layoutSettingsY + 62.0f;
        context.drawText(Fonts.MEDIUM.getFont(6.0f), "Активные ивенты", headerX, eventsHeaderY, textColor.mulAlpha(0.35f));

        float listY = eventsHeaderY + 11.0f;
        float listHeight = height - (listY - y) - 10.0f;
        float cardWidth = width - 24.0f;

        ScissorUtility.push(context.getMatrices(), headerX - 2.0f, listY, cardWidth + 4.0f, listHeight);

        float currentY = listY - (float)this.scrollHandler.getValue();
        float totalListHeight = 0.0f;

        if (this.isLoading) {
            this.renderLoading(context, headerX + cardWidth / 2.0f, listY + listHeight / 2.0f - 10.0f, cardWidth, textColor, accent);
            totalListHeight = listHeight;
        } else if (this.errorMessage != null) {
            this.renderError(context, headerX + cardWidth / 2.0f, listY + listHeight / 2.0f - 10.0f, cardWidth, textColor);
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

            for (Rarity rarity : Rarity.values()) {
                List<EventInfo> events = eventsByRarity.get(rarity);
                if (events.isEmpty()) continue;

                hasAnyEvents = true;

                if (currentY + 14.0f > listY && currentY < listY + listHeight) {
                    context.drawText(Fonts.SEMIBOLD.getFont(7.0f), rarity.display + " (" + events.size() + ")", headerX + 2.0f, currentY + 2.0f, rarity.color);
                }
                currentY += 16.0f;
                totalListHeight += 16.0f;

                for (EventInfo ev : events) {
                    if (currentY + cardH > listY && currentY < listY + listHeight) {
                        this.renderEventCard(context, ev, headerX + 2.0f, currentY, cardWidth - 4.0f, cardH, dark, accent, textColor);
                    }
                    currentY += cardH + gap;
                    totalListHeight += cardH + gap;
                }
                currentY += 4.0f;
                totalListHeight += 4.0f;
            }

            if (!hasAnyEvents) {
                this.renderEmptyState(context, headerX + cardWidth / 2.0f, listY + listHeight / 2.0f - 10.0f, cardWidth, textColor);
                totalListHeight = listHeight;
            }

            totalListHeight += 4.0f;
        }

        float maxScroll = -Math.max(0.0f, totalListHeight - listHeight);
        this.scrollHandler.setMax(maxScroll);

        ScissorUtility.pop();
    }

    private void renderSettingToggle(UIContext context, float x, float y, float w, String text, boolean enabled, boolean dark, ColorRGBA accent, ColorRGBA textColor) {
        boolean hovered = GuiUtility.isHovered((double)x, (double)y, w, 12.0f, (double)context.getMouseX(), (double)context.getMouseY());
        if (hovered) CursorUtility.set(CursorType.HAND);

        ColorRGBA bg = hovered ? Colors.getBackgroundColor().mulAlpha(dark ? 0.5f : 0.35f) : Colors.getBackgroundColor().mulAlpha(dark ? 0.35f : 0.2f);
        context.drawRoundedRect(x, y, w, 12.0f, BorderRadius.all(3.0f), bg);

        float checkboxSize = 10.0f;
        float checkboxX = x + w - checkboxSize - 6.0f;
        float checkboxY = y + 1.0f;

        ColorRGBA checkboxBg = enabled ? accent : textColor.mulAlpha(0.25f);
        ColorRGBA checkboxBorder = enabled ? accent : textColor.mulAlpha(0.35f);
        context.drawRoundedRect(checkboxX, checkboxY, checkboxSize, checkboxSize, BorderRadius.all(2.0f), checkboxBg.mulAlpha(0.3f));
        context.drawRoundedBorder(checkboxX, checkboxY, checkboxSize, checkboxSize, 0.7f, BorderRadius.all(2.0f), checkboxBorder);
        if (enabled) {
            float checkX = checkboxX + 2.0f;
            float checkY = checkboxY + 2.0f;
            context.drawRoundedRect(checkX, checkY + 2.0f, 2.0f, 4.0f, BorderRadius.all(1.0f), Colors.WHITE);
            context.drawRoundedRect(checkX + 2.0f, checkY + 4.0f, 4.0f, 2.0f, BorderRadius.all(1.0f), Colors.WHITE);
        }

        context.drawText(Fonts.MEDIUM.getFont(5.5f), text, x + 6.0f, y + 3.5f, textColor.mulAlpha(0.85f));
    }

    private void renderEventCard(UIContext context, EventInfo ev, float x, float y, float w, float h, boolean dark, ColorRGBA accent, ColorRGBA textColor) {
        boolean hovered = GuiUtility.isHovered((double)x, (double)y, w, h, (double)context.getMouseX(), (double)context.getMouseY());
        if (hovered) CursorUtility.set(CursorType.HAND);

        Rarity rarity = this.parseRarity(ev.rareValue);

        BorderRadius cardRadius = BorderRadius.all(4.0f);
        ColorRGBA cardBg = Colors.getBackgroundColor().withAlpha(hovered ? 150.0f : 105.0f);
        context.drawRoundedRect(x, y, w, h, cardRadius, cardBg);
        context.drawRoundedBorder(x, y, w, h, 0.7f, cardRadius, Colors.getOutlineColor().mulAlpha(hovered ? 0.62f : 0.42f));

        context.drawRoundedRect(x + 3.0f, y + 5.0f, 2.5f, h - 10.0f, BorderRadius.all(1.25f), rarity.color);

        context.drawText(Fonts.SEMIBOLD.getFont(7.5f), ev.displayName, x + 10.0f, y + 7.0f, textColor);

        String server = this.getReadableServerName(ev.serverName);
        Font serverFont = Fonts.REGULAR.getFont(5.5f);
        float sW = serverFont.width(server) + 8.0f;
        float sH = 12.0f;
        float sX = x + 10.0f;
        float sY = y + 21.0f;
        context.drawRoundedRect(sX, sY, sW, sH, BorderRadius.all(3.0f), Colors.getBackgroundColor().withAlpha(120.0f));
        context.drawText(serverFont, server, sX + 4.0f, sY + 2.5f, textColor.mulAlpha(0.75f));

        Font rarityFont = Fonts.SEMIBOLD.getFont(5.5f);
        float rW = rarityFont.width(rarity.display) + 10.0f;
        float rH = 12.0f;
        float rX = x + w - rW - 6.0f;
        float rY = y + 6.0f;
        context.drawRoundedRect(rX, rY, rW, rH, BorderRadius.all(3.0f), rarity.color.withAlpha(hovered ? 50.0f : 30.0f));
        context.drawRoundedRect(rX, rY, rW, rH, BorderRadius.all(3.0f), rarity.color.mulAlpha(0.5f));
        context.drawCenteredText(rarityFont, rarity.display, rX + rW / 2.0f, rY + 2.5f, rarity.color);

        String actionText = "Перейти ➜";
        Font actionFont = Fonts.MEDIUM.getFont(5.5f);
        float aW = actionFont.width(actionText) + 10.0f;
        float aH = 12.0f;
        float aX = x + w - aW - 6.0f;
        float aY = y + 20.0f;

        ColorRGBA actionBg = accent.withAlpha(hovered ? 60.0f : 25.0f);
        context.drawRoundedRect(aX, aY, aW, aH, BorderRadius.all(3.0f), actionBg);
        context.drawRoundedRect(aX, aY, aW, aH, BorderRadius.all(3.0f), accent.mulAlpha(hovered ? 0.8f : 0.35f));
        context.drawCenteredText(actionFont, actionText, aX + aW / 2.0f, aY + 2.5f, accent);
    }

    private void renderLoading(UIContext context, float x, float y, float w, ColorRGBA textColor, ColorRGBA accent) {
        context.drawCenteredText(Fonts.MEDIUM.getFont(7.5f), "Загрузка ивентов...", x, y - 6.0f, textColor.mulAlpha(0.8f));
        float alpha = (float) (0.4 + 0.6 * Math.abs(Math.sin(System.currentTimeMillis() / 250.0)));
        context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), "Подключение к HolyWorld API", x, y + 8.0f, accent.mulAlpha(alpha));
    }

    private void renderError(UIContext context, float x, float y, float w, ColorRGBA textColor) {
        context.drawCenteredText(Fonts.MEDIUM.getFont(7.5f), "Не удалось загрузить ивенты", x, y - 8.0f, new ColorRGBA(255, 90, 90, 255));
        context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), this.errorMessage != null ? this.errorMessage : "Ошибка соединения", x, y + 8.0f, textColor.mulAlpha(0.6f));
    }

    private void renderEmptyState(UIContext context, float x, float y, float w, ColorRGBA textColor) {
        context.drawCenteredText(Fonts.MEDIUM.getFont(8.0f), "Нет активных ивентов", x, y - 6.0f, textColor.mulAlpha(0.6f));
        context.drawCenteredText(Fonts.REGULAR.getFont(5.5f), "Проверьте фильтры серверов", x, y + 8.0f, textColor.mulAlpha(0.4f));
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
                    if (serverNum >= 1 && serverNum <= 12) {
                        return this.module.showSoloLight.isEnabled();
                    }
                    if (serverNum >= 13 && serverNum <= 26) {
                        return this.module.showDuoLight.isEnabled();
                    }
                    if (serverNum >= 27 && serverNum <= 39) {
                        return this.module.showTrioLight.isEnabled();
                    }
                    if (serverNum >= 40 && serverNum <= 51) {
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
        float headerX = x + 12.0f;

        float refreshW = 50.0f;
        float refreshH = 14.0f;
        float refreshX = x + width - 14.0f - refreshW;
        float refreshY = this.layoutHeaderY - 1.0f;
        if (GuiUtility.isHovered((double)refreshX, (double)refreshY, refreshW, refreshH, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.loadEventData();
            this.playClickSound();
            return;
        }

        float settingsListY = this.layoutSettingsListY;
        float settingW = (width - 24.0f - 4.0f) / 2.0f;
        float colGap = 4.0f;
        float currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();

        // Левая колонка
        if (GuiUtility.isHovered((double)headerX, (double)currentSettingY, settingW, 12.0f, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showAnarchy120.toggle();
            this.playClickSound();
            return;
        }
        currentSettingY += 14.0f;
        if (GuiUtility.isHovered((double)headerX, (double)currentSettingY, settingW, 12.0f, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showAnarchy116.toggle();
            this.playClickSound();
            return;
        }
        currentSettingY += 14.0f;
        if (GuiUtility.isHovered((double)headerX, (double)currentSettingY, settingW, 12.0f, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showTrioLight.toggle();
            this.playClickSound();
            return;
        }
        
        // Правая колонка
        currentSettingY = settingsListY - (float)this.settingsScrollHandler.getValue();
        if (GuiUtility.isHovered((double)(headerX + settingW + colGap), (double)currentSettingY, settingW, 12.0f, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showClanLight.toggle();
            this.playClickSound();
            return;
        }
        currentSettingY += 14.0f;
        if (GuiUtility.isHovered((double)(headerX + settingW + colGap), (double)currentSettingY, settingW, 12.0f, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showSoloLight.toggle();
            this.playClickSound();
            return;
        }
        currentSettingY += 14.0f;
        if (GuiUtility.isHovered((double)(headerX + settingW + colGap), (double)currentSettingY, settingW, 12.0f, mouseX, mouseY) && button == MouseButton.LEFT) {
            this.module.showDuoLight.toggle();
            this.playClickSound();
            return;
        }

        float eventsHeaderY = this.layoutSettingsY + 62.0f;
        float listY = eventsHeaderY + 11.0f;
        float listHeight = height - (listY - y) - 10.0f;
        float cardWidth = width - 24.0f;
        float currentY = listY - (float)this.scrollHandler.getValue();

        if (!this.isLoading && this.errorMessage == null) {
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

                    float cardH = 36.0f;
                    if (GuiUtility.isHovered((double)(headerX + 2.0f), (double)currentY, cardWidth - 4.0f, cardH, mouseX, mouseY)) {
                        if (button == MouseButton.RIGHT || button == MouseButton.LEFT) {
                            this.connectToServer(server);
                        }
                        return;
                    }
                    currentY += cardH + 4.0f;
                }
            }
        }
    }

    private void connectToServer(String serverName) {
        if (serverName == null || serverName.isEmpty()) return;
        String s = serverName.toLowerCase();
        if (s.startsWith("lite_anarchy_")) {
            String num = s.replace("lite_anarchy_", "");
            DefineVisuals.getInstance().getCommandManager().dispatch(".rct " + num);
        } else if (s.startsWith("lite_new_anarchy_")) {
            String num = s.replace("lite_new_anarchy_", "");
            if (mc.player != null) {
                mc.player.networkHandler.sendChatCommand("an20 " + num);
            }
        }
        this.playClickSound();
    }

    private void playClickSound() {
        Sounds sounds = DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class);
        if (sounds != null && sounds.isEnabled()) {
            ClientSounds.CLICKGUI_OPEN.play(sounds.getVolume().getCurrentValue(), 1.0f);
        }
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
