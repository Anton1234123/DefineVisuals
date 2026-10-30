package moscow.definevisuals.ui.holyevents;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomScreen;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.framework.style.InterfaceStyleRenderer;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.modules.other.HolyEvents;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.cursor.CursorType;
import moscow.definevisuals.utility.game.cursor.CursorUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.gui.ScrollHandler;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.ScissorUtility;
import moscow.definevisuals.utility.render.obj.CustomSprite;
import moscow.definevisuals.utility.sounds.UiSound;
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

public class EventListScreen extends CustomScreen {
    private final Gson gson = new Gson();
    private final HolyEvents module;
    private Map<String, JsonArray> eventsData = new HashMap<>();
    private volatile boolean isLoading = true;
    private volatile String errorMessage = null;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 30_000;

    private final float windowWidth = 430.0f;
    private final float windowHeight = 340.0f;
    private float windowX, windowY;

    private final ScrollHandler scrollHandler = new ScrollHandler();
    private final Animation menuAnimation = new Animation(350, Easing.BAKEK);
    private boolean closing = false;

    private final List<ModernEventCard> eventCards = new ArrayList<>();

    private static final ExecutorService NETWORK_EXECUTOR = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "HolyEvents-Network");
        t.setDaemon(true);
        return t;
    });

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

    public EventListScreen(HolyEvents module) {
        this.module = module;
        this.scrollHandler.setSpeed(28.0);
        this.loadEventData();
    }

    @Override
    protected void init() {
        int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
        this.windowX = sw / 2.0f - this.windowWidth / 2.0f;
        this.windowY = sh / 2.0f - this.windowHeight / 2.0f;
        super.init();
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

    @Override
    public void tick() {
        this.handleMovementKeys();
        super.tick();
    }

    private void handleMovementKeys() {
        if (MinecraftClient.getInstance().player == null) return;
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        KeyBinding[] movementKeys = new KeyBinding[]{
                MinecraftClient.getInstance().options.forwardKey,
                MinecraftClient.getInstance().options.backKey,
                MinecraftClient.getInstance().options.leftKey,
                MinecraftClient.getInstance().options.rightKey,
                MinecraftClient.getInstance().options.jumpKey
        };
        for (KeyBinding key : movementKeys) {
            int keyCode = InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()).getCode();
            key.setPressed(InputUtil.isKeyPressed(windowHandle, keyCode));
        }
        if (MinecraftClient.getInstance().player.getAbilities().flying) {
            int keyCode = InputUtil.fromTranslationKey(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).getCode();
            MinecraftClient.getInstance().options.sneakKey.setPressed(InputUtil.isKeyPressed(windowHandle, keyCode));
        }
    }

    @Override
    public void render(UIContext ctx) {
        this.menuAnimation.update(this.closing ? 0.0f : 1.0f);
        this.menuAnimation.setEasing(!this.closing ? Easing.BAKEK : Easing.BAKEK_BACK);
        this.scrollHandler.update();

        if (!this.isLoading && System.currentTimeMillis() - this.lastUpdateTime > UPDATE_INTERVAL_MS) {
            this.loadEventData();
        }

        float alpha = Math.min(1.0f, this.menuAnimation.getValue());
        if (alpha <= 0.01f && this.closing) {
            super.close();
            return;
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderUtility.scale(ctx.getMatrices(), this.windowX + this.windowWidth / 2.0f, this.windowY + this.windowHeight / 2.0f, 0.7f + 0.3f * this.menuAnimation.getValue());

        ctx.drawShadow(this.windowX, this.windowY, this.windowWidth, this.windowHeight, 28.0f, BorderRadius.all(14.0f), ColorRGBA.BLACK.withAlpha(48.0f * alpha));
        InterfaceStyleRenderer.drawSurface(
                ctx,
                this.windowX,
                this.windowY,
                this.windowWidth,
                this.windowHeight,
                10.0f,
                BorderRadius.all(14.0f),
                alpha,
                0.0f
        );

        // Header
        float headerX = this.windowX + 14.0f;
        float headerY = this.windowY + 10.0f;
        float iconSize = 24.0f;
        InterfaceStyleRenderer.drawChip(ctx, headerX, headerY, iconSize, iconSize, BorderRadius.all(6.0f), alpha, 0.28f);
        ctx.drawSprite(CustomSprite.EVENTS, headerX + 7.0f, headerY + 7.0f, 10.0f, 10.0f, Colors.getAccent());

        float titleX = headerX + iconSize + 8.0f;
        Font titleFont = Fonts.SEMIBOLD.getFont(10.0f);
        Font subtitleFont = Fonts.REGULAR.getFont(6.0f);
        ctx.drawText(titleFont, "Holy Events", titleX, headerY + 2.0f, Colors.getTextColor());
        ctx.drawText(subtitleFont, Localizator.translate("Мониторинг событий HolyWorld"), titleX, headerY + 15.0f, Colors.getTextColor().mulAlpha(0.42f));

        // Compact status badge next to the title
        int totalCount = this.countVisibleEvents();
        String badgeText = this.isLoading ? "..." : String.valueOf(totalCount);
        Font badgeFont = Fonts.MEDIUM.getFont(6.0f);
        float badgeW = Math.max(30.0f, badgeFont.width(badgeText) + 20.0f);
        float badgeH = 18.0f;
        float badgeX = titleX + titleFont.width("Holy Events") + 10.0f;
        float badgeY = headerY + 3.0f;
        InterfaceStyleRenderer.drawChip(ctx, badgeX, badgeY, badgeW, badgeH, BorderRadius.all(5.0f), alpha, totalCount > 0 ? 0.20f : 0.08f);

        // Status indicator dot
        ColorRGBA dotColor = this.isLoading ? new ColorRGBA(255, 185, 30, 255) : (totalCount > 0 ? new ColorRGBA(50, 220, 80, 255) : Colors.getTextColor().mulAlpha(0.5f));
        ctx.drawRoundedRect(badgeX + 6.0f, badgeY + badgeH / 2.0f - 2.0f, 4.0f, 4.0f, BorderRadius.all(2.0f), dotColor);
        float badgeTextCenterX = badgeX + 14.0f + (badgeW - 14.0f) / 2.0f;
        ctx.drawCenteredText(badgeFont, badgeText, badgeTextCenterX, centeredTextY(badgeFont, badgeY, badgeH), Colors.getTextColor().mulAlpha(0.82f));

        // Buttons on top right
        float closeBtnW = 22.0f;
        float closeBtnH = 22.0f;
        float closeBtnX = this.windowX + this.windowWidth - closeBtnW - 14.0f;
        float closeBtnY = headerY + 1.0f;
        boolean closeHovered = GuiUtility.isHovered(closeBtnX, closeBtnY, closeBtnW, closeBtnH, ctx);
        if (closeHovered) {
            CursorUtility.set(CursorType.HAND);
        }

        float refreshBtnW = 66.0f;
        float refreshBtnH = 22.0f;
        float refreshBtnX = closeBtnX - refreshBtnW - 6.0f;
        float refreshBtnY = closeBtnY;
        boolean refreshHovered = GuiUtility.isHovered(refreshBtnX, refreshBtnY, refreshBtnW, refreshBtnH, ctx);
        if (refreshHovered) {
            CursorUtility.set(CursorType.HAND);
        }

        InterfaceStyleRenderer.drawChip(ctx, refreshBtnX, refreshBtnY, refreshBtnW, refreshBtnH, BorderRadius.all(6.0f), alpha, refreshHovered ? 0.34f : 0.12f);
        Font refreshFont = Fonts.MEDIUM.getFont(6.5f);
        ctx.drawCenteredText(refreshFont, Localizator.translate("Обновить"), refreshBtnX + refreshBtnW / 2.0f, centeredTextY(refreshFont, refreshBtnY, refreshBtnH),
                refreshHovered ? Colors.getAccent() : Colors.getTextColor().mulAlpha(0.80f));

        InterfaceStyleRenderer.drawChip(ctx, closeBtnX, closeBtnY, closeBtnW, closeBtnH, BorderRadius.all(6.0f), alpha, 0.08f);
        if (closeHovered) {
            ctx.drawRoundedRect(closeBtnX, closeBtnY, closeBtnW, closeBtnH, BorderRadius.all(6.0f), new ColorRGBA(220, 55, 65, 58.0f * alpha));
        }
        Font closeFont = Fonts.MEDIUM.getFont(8.0f);
        ctx.drawCenteredText(closeFont, "×", closeBtnX + closeBtnW / 2.0f, centeredTextY(closeFont, closeBtnY, closeBtnH),
                closeHovered ? new ColorRGBA(255, 115, 120, 255) : Colors.getTextColor().mulAlpha(0.72f));

        // Events content viewport
        float contentX = this.windowX + 14.0f;
        float contentWidth = this.windowWidth - 28.0f;
        float sectionY = this.windowY + 46.0f;
        Font sectionFont = Fonts.MEDIUM.getFont(6.5f);
        ctx.drawText(sectionFont, Localizator.translate("Активные события"), contentX, sectionY, Colors.getTextColor().mulAlpha(0.48f));
        Font hintFont = Fonts.REGULAR.getFont(5.5f);
        ctx.drawRightText(hintFont, Localizator.translate("Нажмите, чтобы перейти"), contentX + contentWidth, sectionY + 0.5f, Colors.getTextColor().mulAlpha(0.28f));

        float contentY = sectionY + 12.0f;
        float contentHeight = this.windowY + this.windowHeight - contentY - 11.0f;

        float scroll = (float) this.scrollHandler.getValue();

        ScissorUtility.push(ctx.getMatrices(), contentX, contentY, contentWidth, contentHeight);

        this.eventCards.clear();
        float currentY = contentY - scroll;
        float totalListHeight = 0.0f;

        if (this.isLoading) {
            this.renderLoading(ctx, contentX, contentY + contentHeight / 2.0f - 10.0f, contentWidth);
            totalListHeight = contentHeight;
        } else if (this.errorMessage != null) {
            this.renderError(ctx, contentX, contentY + contentHeight / 2.0f - 10.0f, contentWidth);
            totalListHeight = contentHeight;
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

            float cardW = contentWidth - 8.0f;
            float cardH = 44.0f;
            float gap = 6.0f;
            boolean hasAnyEvents = false;
            Font groupFont = Fonts.SEMIBOLD.getFont(6.5f);

            for (Rarity rarity : Rarity.values()) {
                List<EventInfo> events = eventsByRarity.get(rarity);
                if (events.isEmpty()) continue;

                hasAnyEvents = true;

                float groupHeight = 16.0f;
                if (currentY + groupHeight > contentY && currentY < contentY + contentHeight) {
                    float groupTextY = centeredTextY(groupFont, currentY, groupHeight);
                    ctx.drawRoundedRect(contentX + 2.0f, currentY + groupHeight / 2.0f - 2.0f, 4.0f, 4.0f, BorderRadius.all(2.0f), rarity.color);
                    ctx.drawText(groupFont, Localizator.translate(rarity.display), contentX + 11.0f, groupTextY, rarity.color);
                    ctx.drawRightText(groupFont, String.valueOf(events.size()), contentX + cardW, groupTextY, Colors.getTextColor().mulAlpha(0.38f));
                }
                currentY += groupHeight;
                totalListHeight += groupHeight;

                for (EventInfo ev : events) {
                    if (currentY + cardH > contentY && currentY < contentY + contentHeight) {
                        ModernEventCard card = this.renderEventCard(ctx, ev, contentX + 2.0f, currentY, cardW, cardH);
                        this.eventCards.add(card);
                    } else {
                        this.eventCards.add(new ModernEventCard(ev.displayName, ev.serverName, contentX + 2.0f, currentY, cardW, cardH));
                    }
                    currentY += cardH + gap;
                    totalListHeight += cardH + gap;
                }
                currentY += 6.0f;
                totalListHeight += 6.0f;
            }

            if (!hasAnyEvents) {
                this.renderEmptyState(ctx, contentX, contentY + contentHeight / 2.0f - 10.0f, contentWidth);
                totalListHeight = contentHeight;
            }

            totalListHeight += 6.0f;
        }

        ScissorUtility.pop();

        // Calculate and apply maximum scroll distance
        float maxScroll = Math.max(0.0f, totalListHeight - contentHeight);
        this.scrollHandler.setMax(-maxScroll);

        // Draw scrollbar if scrollable
        if (maxScroll > 0) {
            float scrollbarX = contentX + contentWidth - 4.0f;
            float scrollbarY = contentY;
            float scrollbarHeight = contentHeight;
            float scrollRatio = contentHeight / totalListHeight;
            float thumbHeight = Math.max(18.0f, scrollbarHeight * scrollRatio);
            float thumbY = scrollbarY + (float) (this.scrollHandler.getValue() / maxScroll) * (scrollbarHeight - thumbHeight);

            ctx.drawRoundedRect(scrollbarX, scrollbarY, 3.0f, scrollbarHeight, BorderRadius.all(1.5f), Colors.getBackgroundColor().withAlpha(60.0f));

            boolean thumbHovered = GuiUtility.isHovered(scrollbarX - 2.0f, thumbY, 7.0f, thumbHeight, ctx);
            ColorRGBA thumbColor = Colors.getAccent().withAlpha(thumbHovered ? 230.0f : 150.0f);
            ctx.drawRoundedRect(scrollbarX, thumbY, 3.0f, thumbHeight, BorderRadius.all(1.5f), thumbColor);
        }

        RenderUtility.end(ctx.getMatrices());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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

    private ModernEventCard renderEventCard(UIContext ctx, EventInfo ev, float x, float y, float w, float h) {
        boolean hovered = GuiUtility.isHovered(x, y, w, h, ctx);
        if (hovered) {
            CursorUtility.set(CursorType.HAND);
        }
        Rarity rarity = this.parseRarity(ev.rareValue);

        InterfaceStyleRenderer.drawCard(
                ctx,
                x,
                y,
                w,
                h,
                5.0f,
                BorderRadius.all(9.0f),
                1.0f,
                0.0f,
                hovered ? 1.0f : 0.0f
        );

        // Left colored accent indicator bar
        ctx.drawRoundedRect(x + 4.0f, y + 6.0f, 2.5f, h - 12.0f, BorderRadius.all(1.25f), rarity.color.mulAlpha(0.92f));

        // Rarity badge capsule (top right)
        Font rarityFont = Fonts.SEMIBOLD.getFont(6.5f);
        String rarityText = Localizator.translate(rarity.display);
        float rW = rarityFont.width(rarityText) + 12.0f;
        float rH = 14.0f;
        float rX = x + w - rW - 8.0f;
        float rY = y + 6.0f;
        ctx.drawRoundedRect(rX, rY, rW, rH, BorderRadius.all(4.0f), rarity.color.withAlpha(hovered ? 50.0f : 30.0f));
        ctx.drawCenteredText(rarityFont, rarityText, rX + rW / 2.0f, centeredTextY(rarityFont, rY, rH), rarity.color);

        // Event name
        Font titleFont = Fonts.SEMIBOLD.getFont(8.5f);
        float titleX = x + 14.0f;
        ctx.drawFadeoutText(titleFont, ev.displayName, titleX, centeredTextY(titleFont, y + 5.0f, 16.0f), Colors.getTextColor(), 0.85f, 1.0f, Math.max(30.0f, rX - titleX - 6.0f));

        // Action button (bottom right)
        String actionText = Localizator.translate("Перейти");
        Font actionFont = Fonts.MEDIUM.getFont(6.5f);
        float aW = actionFont.width(actionText) + 12.0f;
        float aH = 14.0f;
        float aX = x + w - aW - 8.0f;
        float aY = y + 24.0f;

        ctx.drawRoundedRect(aX, aY, aW, aH, BorderRadius.all(4.0f), Colors.getAccent().withAlpha(hovered ? 56.0f : 25.0f));
        ctx.drawCenteredText(actionFont, actionText, aX + aW / 2.0f, centeredTextY(actionFont, aY, aH), Colors.getAccent());

        String server = this.getReadableServerName(ev.serverName);
        Font serverFont = Fonts.REGULAR.getFont(6.5f);
        float serverX = x + 14.0f;
        float serverY = y + 24.0f;
        ctx.drawRoundedRect(serverX, serverY + aH / 2.0f - 1.5f, 3.0f, 3.0f, BorderRadius.all(1.5f), Colors.getTextColor().mulAlpha(0.34f));
        ctx.drawFadeoutText(serverFont, server, serverX + 7.0f, centeredTextY(serverFont, serverY, aH), Colors.getTextColor().mulAlpha(0.58f), 0.8f, 1.0f, Math.max(20.0f, aX - serverX - 12.0f));

        return new ModernEventCard(ev.displayName, ev.serverName, x, y, w, h);
    }

    private void renderLoading(UIContext ctx, float x, float y, float w) {
        float cx = x + w / 2.0f;
        ctx.drawCenteredText(Fonts.MEDIUM.getFont(9.0f), Localizator.translate("Загрузка событий..."), cx, y - 6.0f, Colors.getTextColor().mulAlpha(0.8f));
        float alpha = (float) (0.4 + 0.6 * Math.abs(Math.sin(System.currentTimeMillis() / 250.0)));
        ctx.drawCenteredText(Fonts.REGULAR.getFont(7.0f), Localizator.translate("Подключение к HolyWorld API"), cx, y + 8.0f, Colors.getAccent().mulAlpha(alpha));
    }

    private void renderError(UIContext ctx, float x, float y, float w) {
        float cx = x + w / 2.0f;
        ctx.drawCenteredText(Fonts.MEDIUM.getFont(9.0f), Localizator.translate("Не удалось загрузить события"), cx, y - 8.0f, new ColorRGBA(255, 90, 90, 255));
        ctx.drawCenteredText(Fonts.REGULAR.getFont(7.0f), this.errorMessage != null ? this.errorMessage : "Ошибка соединения", cx, y + 8.0f, Colors.getTextColor().mulAlpha(0.6f));
    }

    private void renderEmptyState(UIContext ctx, float x, float y, float w) {
        float cx = x + w / 2.0f;
        ctx.drawCenteredText(Fonts.MEDIUM.getFont(9.5f), Localizator.translate("Нет активных событий"), cx, y - 6.0f, Colors.getTextColor().mulAlpha(0.6f));
        ctx.drawCenteredText(Fonts.REGULAR.getFont(7.0f), Localizator.translate("Проверьте фильтры серверов в ClickGUI"), cx, y + 8.0f, Colors.getTextColor().mulAlpha(0.4f));
    }

    private static float centeredTextY(Font font, float y, float height) {
        return y + GuiUtility.getMiddleOfBox(font.height(), height) - 0.5f;
    }

    @Override
    public void onMouseClicked(double mx, double my, MouseButton btn) {
        float closeBtnW = 22.0f;
        float closeBtnH = 22.0f;
        float closeBtnX = this.windowX + this.windowWidth - closeBtnW - 14.0f;
        float closeBtnY = this.windowY + 11.0f;

        float refreshBtnW = 66.0f;
        float refreshBtnH = 22.0f;
        float refreshBtnX = closeBtnX - refreshBtnW - 6.0f;
        float refreshBtnY = closeBtnY;

        // Refresh button click
        if (GuiUtility.isHovered(refreshBtnX, refreshBtnY, refreshBtnW, refreshBtnH, mx, my) && btn == MouseButton.LEFT) {
            this.loadEventData();
            UiSound.CLICK.play();
            return;
        }

        // Close button click
        if (GuiUtility.isHovered(closeBtnX, closeBtnY, closeBtnW, closeBtnH, mx, my) && btn == MouseButton.LEFT) {
            this.close();
            return;
        }

        // Click outside window to close
        if (mx < this.windowX || mx > this.windowX + this.windowWidth || my < this.windowY || my > this.windowY + this.windowHeight) {
            this.close();
            return;
        }

        // Card interaction
        float contentX = this.windowX + 14.0f;
        float contentY = this.windowY + 58.0f;
        float contentWidth = this.windowWidth - 28.0f;
        float contentHeight = this.windowY + this.windowHeight - contentY - 11.0f;
        if (GuiUtility.isHovered(contentX, contentY, contentWidth, contentHeight, mx, my)) {
            for (ModernEventCard card : this.eventCards) {
                if (!card.isHovered(mx, my)) continue;
                if (btn == MouseButton.RIGHT || btn == MouseButton.LEFT) {
                    this.connectToServer(card.serverName);
                }
                return;
            }
        }
    }

    private void connectToServer(String serverName) {
        if (serverName == null || serverName.isEmpty()) return;
        String s = serverName.toUpperCase();
        boolean navigated = false;
        if (s.startsWith("LITE_ANARCHY_")) {
            String num = s.replace("LITE_ANARCHY_", "");
            String prefix = DefineVisuals.getInstance().getCommandManager().getPrefix();
            DefineVisuals.getInstance().getCommandManager().dispatch(prefix + "rct " + num);
            navigated = true;
        } else if (s.startsWith("LITE_NEW_ANARCHY_")) {
            String num = s.replace("LITE_NEW_ANARCHY_", "");
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.networkHandler.sendChatCommand("an20 " + num);
                navigated = true;
            }
        }
        if (navigated) {
            UiSound.NAVIGATION.play();
            this.closing = true;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (GuiUtility.isHovered(this.windowX, this.windowY, this.windowWidth, this.windowHeight, mouseX, mouseY)) {
            this.scrollHandler.scroll(verticalAmount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // Esc
            this.close();
            return true;
        }
        this.scrollHandler.onKeyPressed(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        if (!this.closing) {
            this.closing = true;
            UiSound.WINDOW_CLOSE.play();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
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

    private static class ModernEventCard {
        String displayName;
        String serverName;
        float x, y, w, h;

        ModernEventCard(String displayName, String serverName, float x, float y, float w, float h) {
            this.displayName = displayName;
            this.serverName = serverName;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        boolean isHovered(double mx, double my) {
            return mx >= this.x && mx <= this.x + this.w && my >= this.y && my <= this.y + this.h;
        }
    }

    private static class EventInfo {
        String serverName, displayName, rareValue;
        EventInfo(String s, String d, String r) {
            this.serverName = s;
            this.displayName = d;
            this.rareValue = r;
        }
    }
}
