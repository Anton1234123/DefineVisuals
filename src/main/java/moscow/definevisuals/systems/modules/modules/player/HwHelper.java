package moscow.definevisuals.systems.modules.modules.player;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Generated;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.game.GameTickEvent;
import moscow.definevisuals.systems.event.impl.game.SoundEvent;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.event.impl.window.KeyPressEvent;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.event.impl.network.SendPacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import moscow.definevisuals.systems.modules.modules.player.hwhelper.*;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.inventory.InventoryUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Hw Helper", category = ModuleCategory.PLAYER, desc = "modules.descriptions.hw_helper", enabledByDefault = true)
public class HwHelper extends BaseModule {
    private static final int MAX_SIMULATION_TICKS = 160;
    private static final int MAX_ACTIVE_ZONES = 8;
    private static final int CIRCLE_SEGMENTS = 64;
    private static final int[][] CUBE_EDGES = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
    private static final double SNOW_GRAVITY = 0.03;
    private static final double SNOW_DRAG = 0.99;
    private static final double SNOW_SPEED = 1.5;
    private static final long ACTIVATION_DEBOUNCE_MS = 90L;

    private final SliderSetting bindDelay = new SliderSetting(this, "Задержка биндов (мс)", "Задержка между использованиями биндов").min(10).max(500).step(10).currentValue(10);
    private final BooleanSetting previewEnabled = new BooleanSetting(this, "Предпросмотр зоны", "Показывать зону трапки при держании предмета").enabled(true);
    private final BooleanSetting requirePlayerInside = new BooleanSetting(this, "Только с игроком внутри", "Использовать трапку только если внутри есть другой игрок").enabled(false);
    private final moscow.definevisuals.systems.setting.settings.BindSetting snowBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Снежок");
    private final moscow.definevisuals.systems.setting.settings.BindSetting pumpkinBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Голова Джека");
    private final moscow.definevisuals.systems.setting.settings.BindSetting chorusBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Трапка");
    private final moscow.definevisuals.systems.setting.settings.BindSetting netherStarBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Стан");
    private final moscow.definevisuals.systems.setting.settings.BindSetting prismarineBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Взрывная трапка");
    private final moscow.definevisuals.systems.setting.settings.BindSetting fireChargeBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Взрывная штучка");
    private final moscow.definevisuals.systems.setting.settings.BindSetting autoSellBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Авто продажа");
    private final moscow.definevisuals.systems.setting.settings.BindSetting itemSearchBind = new moscow.definevisuals.systems.setting.settings.BindSetting(this, "Поиск предмета");

    private final List<ActiveZone> activeZones = new ArrayList<>();
    private final List<Vec3d> simulationPoints = new ArrayList<>(301);
    private Vec3d snowLanding;
    private long lastActivationMs;
    private long lastFrameNanos;
    private float frameDt;
    private ItemStack originalMainHand;
    private int originalSlot = -1;
    private int itemSourceSlot = -1;
    private boolean itemFromInventory = false;
    private long swapStartTime = 0L;
    private HolyWorldItem heldBindItem = null;
    private static final long SWAP_DELAY_MS = 200L;
    private boolean autoSellFirstCommandSent = false;
    private long autoSellFirstCommandTime = 0L;

    // ItemRadius additions
    private Vec3d plastSmoothedCenter = Vec3d.ZERO;
    private float plastSmoothedYawDeg = 0.0f;
    private float plastSmoothedPitchDeg = 0.0f;
    private boolean plastHasSmoothedPose = false;
    private Vec3d smoothedPreview = null;
    private int currentOutlineColor = 0xFFFFFFFF;
    private int targetOutlineColor = 0xFFFFFFFF;
    private float transitionTimer = 0.0f;
    private boolean lastPlayersInRadius = false;
    private static final float TRANSITION_DURATION = 0.5f;

    private final EventListener<GameTickEvent> onTick = event -> {
        if (mc.player == null || mc.world == null) {
            clearState();
            return;
        }
        if (matchHeld(mc.player) != HolyWorldItem.SNOW) {
            snowLanding = null;
            simulationPoints.clear();
        }
        
        long currentTime = Util.getMeasuringTimeMs();
        
        // Handle auto sell second command delay
        if (autoSellFirstCommandSent && currentTime - autoSellFirstCommandTime >= bindDelay.getCurrentValue()) {
            autoSellFirstCommandSent = false;
            sendCommand("/ah sell auto confirm");
        }
        
        // Handle item swap restoration
        if (originalSlot != -1 && currentTime - swapStartTime >= SWAP_DELAY_MS) {
            restoreOriginalItem();
        }
        
        for (int i = activeZones.size() - 1; i >= 0; --i) {
            ActiveZone activeZone = activeZones.get(i);
            if (currentTime >= activeZone.expiresAt) {
                activeZones.remove(i);
                continue;
            }
            if (!activeZone.item.followsPlayer) continue;
            activeZone.moveTo(zoneCenter(activeZone.item, mc.player.getPos()));
        }

        // Trap timer system
        StructureDetector.tick(mc);
        TimerTracker.tick();
    };

    private final EventListener<KeyPressEvent> onKeyPress = event -> {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) {
            return;
        }
        
        // Handle auto sell bind
        if (event.getKey() == autoSellBind.getKey()) {
            if (event.getAction() == 0) {
                long currentTime = Util.getMeasuringTimeMs();
                if (currentTime - lastActivationMs >= bindDelay.getCurrentValue()) {
                    lastActivationMs = currentTime;
                    autoSellFirstCommandSent = true;
                    autoSellFirstCommandTime = currentTime;
                    sendCommand("/ah sell auto");
                }
            }
            return;
        }
        
        // Handle item search bind
        if (event.getKey() == itemSearchBind.getKey()) {
            if (event.getAction() == 0) {
                long currentTime = Util.getMeasuringTimeMs();
                if (currentTime - lastActivationMs >= bindDelay.getCurrentValue()) {
                    lastActivationMs = currentTime;
                    sendCommand("/ah search");
                }
            }
            return;
        }
        
        HolyWorldItem item = null;
        if (event.getKey() == snowBind.getKey()) item = HolyWorldItem.SNOW;
        else if (event.getKey() == pumpkinBind.getKey()) item = HolyWorldItem.PUMPKIN;
        else if (event.getKey() == chorusBind.getKey()) item = HolyWorldItem.CHORUS;
        else if (event.getKey() == netherStarBind.getKey()) item = HolyWorldItem.NETHER_STAR;
        else if (event.getKey() == prismarineBind.getKey()) item = HolyWorldItem.PRISMARINE;
        else if (event.getKey() == fireChargeBind.getKey()) item = HolyWorldItem.FIRE_CHARGE;
        
        if (item == null) {
            return;
        }
        
        long currentTime = Util.getMeasuringTimeMs();
        
        if (event.getAction() == 1) {
            // Key pressed - check delay
            if (currentTime - lastActivationMs < bindDelay.getCurrentValue()) {
                return;
            }
            heldBindItem = item;
        } else if (event.getAction() == 0) {
            // Key released
            if (heldBindItem == item) {
                heldBindItem = null;
                lastActivationMs = currentTime;
                useItemFromInventory(item);
            }
        }
    };

    private final EventListener<Render3DEvent> onWorldRender = event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }
        RenderSystem.setShader(net.minecraft.client.gl.ShaderProgramKeys.POSITION_COLOR);
        Vec3d cameraPos = event.getCamera().getPos();
        long currentTime = System.nanoTime();
        frameDt = lastFrameNanos == 0L ? 0.016f : Math.min((float)(currentTime - lastFrameNanos) * 1.0E-9f, 0.1f);
        lastFrameNanos = currentTime;

        // Render trap timer preview
        if (previewEnabled.isEnabled()) {
            WorldOverlay.renderPreview(event.getMatrices(), cameraPos);
        }

        int baseColor = 0xFFFF0000;
        for (ActiveZone activeZone : activeZones) {
            boolean occupied = hasOtherPlayerIn(activeZone.item, activeZone.center);
            activeZone.occupiedBlend = approach(activeZone.occupiedBlend, occupied ? 1.0f : 0.0f);
            int zoneColor = blendOccupied(baseColor, activeZone.occupiedBlend);
            Vec3d zoneCenter = activeZone.center;
            double floorY = activeZone.floorY;
            if (activeZone.item.followsPlayer) {
                Vec3d playerPos = lerpPos(event.getTickDelta());
                zoneCenter = zoneCenter(activeZone.item, playerPos);
                floorY = zoneCenter.y - (activeZone.item.shape == Shape.CUBE ? (double)activeZone.item.size * 0.5 : 0.025);
            }
            drawZone(event, cameraPos, activeZone.item.shape, zoneCenter, activeZone.item.size, floorY, zoneColor);
        }
    };

    public HwHelper() {
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        clearState();
        // Reset trap timer system
        StructureDetector.reset();
        StructureSounds.reset();
        ItemNames.reset();
        PearlGuard.reset();
        TimerTracker.clear();
    }

    @Generated
    public EventListener<GameTickEvent> getOnTick() {
        return onTick;
    }

    @Generated
    public EventListener<KeyPressEvent> getOnKeyPress() {
        return onKeyPress;
    }

    @Generated
    public EventListener<Render3DEvent> getOnWorldRender() {
        return onWorldRender;
    }

    private final EventListener<HudRenderEvent> onHudRender = event -> {
        TimerHud.render(event.getContext());
    };

    @Generated
    public EventListener<HudRenderEvent> getOnHudRender() {
        return onHudRender;
    }

    private final EventListener<ReceivePacketEvent> onPacketReceive = PacketHandler.createPacketListener();

    @Generated
    public EventListener<ReceivePacketEvent> getOnPacketReceive() {
        return onPacketReceive;
    }

    private final EventListener<SoundEvent> onSound = event -> {
        if (event.sound != null && event.sound.getId() != null) {
            String soundId = event.sound.getId().toString();
            net.minecraft.client.sound.SoundInstance sound = event.sound;
            StructureSounds.onSound(soundId, sound.getX(), sound.getY(), sound.getZ(), sound.getVolume(), sound.getPitch());
        }
    };

    @Generated
    public EventListener<SoundEvent> getOnSound() {
        return onSound;
    }

    private final EventListener<SendPacketEvent> onPacketSend = event -> {
        if (!requirePlayerInside.isEnabled() || mc.player == null || mc.world == null) return;
        
        if (event.getPacket() instanceof PlayerInteractItemC2SPacket interactPacket) {
            HolyWorldItem heldItem = matchHeld(mc.player);
            if (heldItem != null && heldItem.isTrap) {
                Vec3d playerPos = mc.player.getPos();
                double floorY = heldItem.followsPlayer ? playerPos.y : resolveFloorY(playerPos);
                Vec3d zoneCenter = heldItem.followsPlayer ? zoneCenter(heldItem, playerPos) : placedCenter(heldItem, playerPos.x, floorY, playerPos.z);
                
                if (!hasOtherPlayerIn(heldItem, zoneCenter)) {
                    event.cancel();
                    moscow.definevisuals.DefineVisuals.getInstance().getNotificationManager().addNotification(
                        moscow.definevisuals.systems.notifications.NotificationType.ERROR,
                        "Внутри нет игроков"
                    );
                }
            }
        }
    };

    @Generated
    public EventListener<SendPacketEvent> getOnPacketSend() {
        return onPacketSend;
    }

    private float approach(float current, float target) {
        return current + (target - current) * Math.min(1.0f, frameDt * 6.0f);
    }

    private static Vec3d placedCenter(HolyWorldItem item, double x, double y, double z) {
        double centerY = item.floorAnchored ? y + (double)item.size * 0.5 : y;
        return new Vec3d(x, centerY, z);
    }

    private void renderHeldPreview(Render3DEvent event, Vec3d cameraPos) {
        ItemStack main = mc.player.getMainHandStack();
        ItemStack off = mc.player.getOffHandStack();

        // Original HwHelper logic
        HolyWorldItem item = matchHeld(mc.player);
        
        if (item == null) {
            return;
        }
        
        if (item == HolyWorldItem.SNOW) {
            int snowColor = multiplyAlpha(0xFFFF0000, 0.45f);
            updateSnowPrediction(event.getTickDelta());
            drawPath(event, cameraPos, simulationPoints, snowColor);
            if (snowLanding != null) {
                drawCircle(event, cameraPos, snowLanding.add(0.0, 0.02, 0.0), 0.42f, snowColor);
            }
            return;
        }
        Vec3d playerPos = lerpPos(event.getTickDelta());
        double floorY = playerPos.y + 0.012;
        Vec3d zoneCenter = item.followsPlayer ? zoneCenter(item, playerPos) : placedCenter(item, playerPos.x, floorY, playerPos.z);
        int zoneColor = multiplyAlpha(0xFFFF0000, 0.45f);
        drawZone(event, cameraPos, item.shape, zoneCenter, item.size, floorY, zoneColor);
    }

    private void renderCubeFill(Render3DEvent event, Vec3d cameraPos, Vec3d center, float size, int fillColor) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();

        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        org.joml.Matrix4f matrix = event.getMatrices().peek().getPositionMatrix();

        float half = size / 2.0f;
        float minX = (float) (center.x - cameraPos.x - half);
        float minY = (float) (center.y - cameraPos.y - half);
        float minZ = (float) (center.z - cameraPos.z - half);
        float maxX = minX + size;
        float maxY = minY + size;
        float maxZ = minZ + size;

        drawBoxFaces(builder, matrix, minX, minY, minZ, maxX, maxY, maxZ, fillColor);

        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static boolean isHolding(ItemStack stack, net.minecraft.item.Item item) {
        return stack != null && !stack.isEmpty() && stack.isOf(item);
    }

    private static boolean zoneContains(HolyWorldItem item, Vec3d zoneCenter, Vec3d pos) {
        if (item.shape == Shape.CIRCLE) {
            double dx = pos.x - zoneCenter.x;
            double dz = pos.z - zoneCenter.z;
            return dx * dx + dz * dz <= (double)(item.size * item.size);
        }
        double halfSize = (double)item.size * 0.5;
        return Math.abs(pos.x - zoneCenter.x) <= halfSize && Math.abs(pos.y - zoneCenter.y) <= (double)item.size && Math.abs(pos.z - zoneCenter.z) <= halfSize;
    }

    private Vec3d simulateLanding(Vec3d start, Vec3d velocity) {
        simulationPoints.clear();
        simulationPoints.add(start);
        Vec3d currentPos = start;
        Vec3d currentVel = velocity;
        for (int i = 0; i < MAX_SIMULATION_TICKS; ++i) {
            Vec3d nextPos = currentPos.add(currentVel);
            BlockHitResult hitResult = mc.world.raycast(new RaycastContext(currentPos, nextPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                Vec3d hitPos = hitResult.getPos();
                simulationPoints.add(hitPos);
                return hitPos;
            }
            simulationPoints.add(nextPos);
            currentPos = nextPos;
            currentVel = currentVel.multiply(SNOW_DRAG).add(0.0, -SNOW_GRAVITY, 0.0);
            if (currentPos.y < -64.0) break;
        }
        return null;
    }

    private void emitLandingMarker(Render3DEvent event, Vec3d cameraPos, Vec3d landingPos, int color) {
        double size = 0.13;
        double y = landingPos.y + 0.004;
        Vec3d[] corners = {
            new Vec3d(landingPos.x - size, y, landingPos.z - size),
            new Vec3d(landingPos.x + size, y, landingPos.z - size),
            new Vec3d(landingPos.x + size, y, landingPos.z + size),
            new Vec3d(landingPos.x - size, y, landingPos.z + size)
        };
        
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        org.joml.Matrix4f matrix = event.getMatrices().peek().getPositionMatrix();
        
        for (Vec3d corner : corners) {
            Vec3d relative = corner.subtract(cameraPos);
            builder.vertex(matrix, (float)relative.x, (float)relative.y, (float)relative.z).color(color);
        }
        
        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private int blendOccupied(int baseColor, float blend) {
        if (blend <= 0.001f) {
            return baseColor;
        }
        if (blend >= 0.999f) {
            return 0xFF41DC70;
        }
        return lerpColor(baseColor, 0xFF41DC70, blend);
    }

    private Vec3d smoothPreview(Vec3d pos) {
        if (smoothedPreview == null || smoothedPreview.squaredDistanceTo(pos) > 64.0) {
            smoothedPreview = pos;
            return pos;
        }
        smoothedPreview = smoothedPreview.add(pos.subtract(smoothedPreview).multiply(0.35));
        return smoothedPreview;
    }

    private double resolveFloorY(Vec3d pos) {
        if (mc.world == null || mc.player == null) {
            return pos.y;
        }
        BlockHitResult hitResult = mc.world.raycast(new RaycastContext(pos.add(0.0, 0.75, 0.0), pos.add(0.0, -8.0, 0.0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
        return hitResult != null && hitResult.getType() != HitResult.Type.MISS ? hitResult.getPos().y + 0.012 : pos.y;
    }

    private Vec3d lerpPos(float delta) {
        return new Vec3d(
            MathHelper.lerp(delta, mc.player.lastRenderX, mc.player.getX()),
            MathHelper.lerp(delta, mc.player.lastRenderY, mc.player.getY()),
            MathHelper.lerp(delta, mc.player.lastRenderZ, mc.player.getZ())
        );
    }

    private void updateSnowPrediction(float delta) {
        if (mc.player == null || mc.world == null) {
            snowLanding = null;
            simulationPoints.clear();
            return;
        }
        Vec3d rotation = mc.player.getRotationVec(delta);
        if (rotation.lengthSquared() < 1.0E-6) {
            snowLanding = null;
            simulationPoints.clear();
            return;
        }
        Vec3d start = mc.player.getCameraPosVec(delta).add(rotation.multiply(0.05));
        snowLanding = simulateLanding(start, rotation.multiply(SNOW_SPEED));
    }

    private boolean hasOtherPlayerIn(HolyWorldItem item, Vec3d zoneCenter) {
        if (!item.isTrap || mc.world == null) {
            return false;
        }
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || !zoneContains(item, zoneCenter, player.getPos())) continue;
            return true;
        }
        return false;
    }

    private void drawPath(Render3DEvent event, Vec3d cameraPos, List<Vec3d> points, int color) {
        if (points.size() < 2) {
            return;
        }
        int lastIndex = points.size() - 1;
        
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        org.joml.Matrix4f matrix = event.getMatrices().peek().getPositionMatrix();
        
        for (int i = 1; i < points.size(); ++i) {
            float alpha = 1.0f - (float)i / (float)lastIndex * 0.35f;
            int pointColor = multiplyAlpha(color, alpha);
            if ((pointColor >> 24 & 0xFF) <= 0) continue;
            
            Vec3d p1 = points.get(i - 1).subtract(cameraPos);
            Vec3d p2 = points.get(i).subtract(cameraPos);
            
            builder.vertex(matrix, (float)p1.x, (float)p1.y, (float)p1.z).color(pointColor);
            builder.vertex(matrix, (float)p2.x, (float)p2.y, (float)p2.z).color(pointColor);
        }
        
        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void activate(HolyWorldItem item, long currentTime) {
        activateWithDuration(item, currentTime, item.durationMs);
    }

    private void activateWithDuration(HolyWorldItem item, long currentTime, long duration) {
        Vec3d playerPos = mc.player.getPos();
        double floorY = item.followsPlayer ? playerPos.y : resolveFloorY(playerPos);
        Vec3d zoneCenter = item.followsPlayer ? zoneCenter(item, playerPos) : placedCenter(item, playerPos.x, floorY, playerPos.z);
        activeZones.removeIf(zone -> zone.item == item && zone.center.squaredDistanceTo(zoneCenter) < 2.25);
        if (activeZones.size() >= MAX_ACTIVE_ZONES) {
            activeZones.remove(0);
        }
        activeZones.add(new ActiveZone(item, zoneCenter, floorY, currentTime + duration));
    }

    private static HolyWorldItem matchHeld(PlayerEntity player) {
        HolyWorldItem item = HolyWorldItem.match(player.getMainHandStack());
        return item != null ? item : HolyWorldItem.match(player.getOffHandStack());
    }

    private static Vec3d zoneCenter(HolyWorldItem item, Vec3d pos) {
        return item.shape == Shape.CUBE ? pos.add(0.0, (double)item.size * 0.5, 0.0) : pos.add(0.0, 0.025, 0.0);
    }

    private void clearState() {
        activeZones.clear();
        simulationPoints.clear();
        snowLanding = null;
        lastActivationMs = 0L;
        lastFrameNanos = 0L;
        heldBindItem = null;
        plastHasSmoothedPose = false;
        autoSellFirstCommandSent = false;
        autoSellFirstCommandTime = 0L;
    }

    private void drawZone(Render3DEvent event, Vec3d cameraPos, Shape shape, Vec3d center, float size, double floorY, int color) {
        if (shape == Shape.STRUCTURE) {
            TrapStructure structure = TrapStructure.createExplosiveTrap();
            TrapStructureRenderer.render(event.getMatrices(), cameraPos, center, structure);
            return;
        }

        double squareSize = 3.0;
        double halfSize = squareSize * 0.5;

        Vec3d squareCenter = new Vec3d(center.x, floorY, center.z);

        Vec3d[] corners = new Vec3d[]{
            squareCenter.add(-halfSize, 0.0, -halfSize),
            squareCenter.add(halfSize, 0.0, -halfSize),
            squareCenter.add(halfSize, 0.0, halfSize),
            squareCenter.add(-halfSize, 0.0, halfSize),
            squareCenter.add(-halfSize, 0.0, -halfSize)
        };

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();

        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
        org.joml.Matrix4f matrix = event.getMatrices().peek().getPositionMatrix();

        for (Vec3d corner : corners) {
            Vec3d p = corner.subtract(cameraPos);
            builder.vertex(matrix, (float)p.x, (float)p.y, (float)p.z).color(color);
        }

        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void drawCircle(Render3DEvent event, Vec3d cameraPos, Vec3d center, float radius, int color) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        org.joml.Matrix4f matrix = event.getMatrices().peek().getPositionMatrix();
        
        for (int i = 0; i < CIRCLE_SEGMENTS; ++i) {
            double angle1 = Math.PI * 2 * (float)i / CIRCLE_SEGMENTS;
            double angle2 = Math.PI * 2 * (float)(i + 1) / CIRCLE_SEGMENTS;
            Vec3d p1 = center.add(Math.cos(angle1) * (double)radius, 0.0, Math.sin(angle1) * (double)radius).subtract(cameraPos);
            Vec3d p2 = center.add(Math.cos(angle2) * (double)radius, 0.0, Math.sin(angle2) * (double)radius).subtract(cameraPos);
            builder.vertex(matrix, (float)p1.x, (float)p1.y, (float)p1.z).color(color);
            builder.vertex(matrix, (float)p2.x, (float)p2.y, (float)p2.z).color(color);
        }
        
        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static int multiplyAlpha(int color, float alpha) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return ((int)(a * alpha) & 0xFF) << 24 | r << 16 | g << 8 | b;
    }

    // ItemRadius helper methods
    private static boolean containsCyrillicLetter(ItemStack stack, char letter) {
        if (stack == null || stack.isEmpty()) return false;
        String name = stack.getName().getString();
        if (name == null || name.isEmpty()) return false;
        String upper = name.toUpperCase();
        return upper.indexOf(Character.toUpperCase(letter)) >= 0;
    }

    private boolean checkFriendsInRadius(PlayerEntity self, Vec3d centerPos, double radius) {
        if (mc.world == null) return false;
        // TODO: Integrate with friend manager if available
        double r2 = radius * radius;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == null || p == self) continue;
            if (p.squaredDistanceTo(centerPos) > r2) continue;
            // Add friend check here when friend manager is available
            return false;
        }
        return false;
    }

    private void updateOutlineColor(boolean playersInRadius, float tickDelta) {
        if (playersInRadius != lastPlayersInRadius) {
            transitionTimer = 0.0f;
            lastPlayersInRadius = playersInRadius;
        }

        int baseOutline = 0xFFFFFFFF;
        int lightOutline = 0xFF00FF00;
        targetOutlineColor = playersInRadius ? lightOutline : baseOutline;

        float step = TRANSITION_DURATION <= 0.0001f ? 1.0f : (tickDelta / TRANSITION_DURATION);
        transitionTimer = Math.min(transitionTimer + step, 1.0f);
        currentOutlineColor = lerpColor(currentOutlineColor, targetOutlineColor, transitionTimer);
    }

    private static int lerpColor(int startColor, int endColor, float t) {
        int startA = (startColor >> 24) & 0xFF;
        int startR = (startColor >> 16) & 0xFF;
        int startG = (startColor >> 8) & 0xFF;
        int startB = startColor & 0xFF;

        int endA = (endColor >> 24) & 0xFF;
        int endR = (endColor >> 16) & 0xFF;
        int endG = (endColor >> 8) & 0xFF;
        int endB = endColor & 0xFF;

        int a = (int) (startA + (endA - startA) * t);
        int r = (int) (startR + (endR - startR) * t);
        int g = (int) (startG + (endG - startG) * t);
        int b = (int) (startB + (endB - startB) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static float lerpAngleDeg(float from, float to, float t) {
        float delta = MathHelper.wrapDegrees(to - from);
        return from + delta * t;
    }

    private record PlanePose(Vec3d center, float yawDeg, float pitchDeg) {}

    private PlanePose smoothPlastPose(PlanePose target, float tickDelta) {
        if (!plastHasSmoothedPose) {
            plastSmoothedCenter = target.center;
            plastSmoothedYawDeg = target.yawDeg;
            plastSmoothedPitchDeg = target.pitchDeg;
            plastHasSmoothedPose = true;
            return target;
        }

        float speed = 12.0f;
        float t = 1.0f - (float) Math.exp(-speed * Math.max(0.0f, tickDelta));

        plastSmoothedCenter = plastSmoothedCenter.lerp(target.center, t);
        plastSmoothedYawDeg = lerpAngleDeg(plastSmoothedYawDeg, target.yawDeg, t);
        plastSmoothedPitchDeg = lerpAngleDeg(plastSmoothedPitchDeg, target.pitchDeg, t);

        return new PlanePose(plastSmoothedCenter, plastSmoothedYawDeg, plastSmoothedPitchDeg);
    }

    private PlanePose computePlanePose(float tickDelta) {
        Vec3d playerPos = lerpPos(tickDelta);
        Vec3d start = playerPos.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);
        Vec3d lookVec = mc.player.getRotationVec(tickDelta);
        Vec3d end = start.add(lookVec.multiply(4.0));

        BlockHitResult hit = mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));

        float pitch = mc.player.getPitch(tickDelta);
        boolean isLookingDown = pitch > 45.0f;
        boolean isLookingUp = pitch < -45.0f;
        boolean isLookingHorizontal = !isLookingDown && !isLookingUp;

        float thickness = 1.5f;
        float halfThickness = thickness / 2.0f;

        Vec3d center;
        float yawDeg;
        float pitchDeg;

        if (hit.getType() == HitResult.Type.BLOCK && hit.getPos().distanceTo(start) <= 4.0) {
            Vec3d hitPos = hit.getPos();
            net.minecraft.util.math.Direction face = hit.getSide();

            if (isLookingDown) {
                center = new Vec3d(
                        Math.floor(hitPos.x) + 0.5,
                        Math.floor(hitPos.y + 1.0) - 1.8 + halfThickness,
                        Math.floor(hitPos.z) + 0.5
                );
                yawDeg = 0.0f;
                pitchDeg = 90.0f;
            } else if (isLookingUp) {
                center = new Vec3d(
                        Math.floor(hitPos.x) + 0.5,
                        Math.floor(hitPos.y) - halfThickness + 1.6,
                        Math.floor(hitPos.z) + 0.5
                );
                yawDeg = 0.0f;
                pitchDeg = -90.0f;
            } else {
                double offsetX = face.getOffsetX() != 0 ? face.getOffsetX() * halfThickness : 0.0;
                double offsetZ = face.getOffsetZ() != 0 ? face.getOffsetZ() * halfThickness : 0.0;
                center = new Vec3d(
                        Math.floor(hitPos.x) + 0.5 + offsetX,
                        Math.floor(hitPos.y) + 0.5 + 1.6,
                        Math.floor(hitPos.z) + 0.5 + offsetZ
                );

                yawDeg = switch (face) {
                    case NORTH -> 180.0f;
                    case SOUTH -> 0.0f;
                    case WEST -> 90.0f;
                    case EAST -> -90.0f;
                    default -> -mc.player.getYaw(tickDelta);
                };
                pitchDeg = 0.0f;
            }
        } else {
            Vec3d approx = start.add(lookVec.multiply(4.0));
            double y = Math.floor(approx.y) + (isLookingDown ? (-1.8 + halfThickness) : isLookingUp ? (-halfThickness + 1.6) : (0.5 + 1.6));
            center = new Vec3d(Math.floor(approx.x) + 0.5, y, Math.floor(approx.z) + 0.5);
            yawDeg = -mc.player.getYaw(tickDelta);
            pitchDeg = 0.0f;
        }

        if (!isLookingHorizontal) {
            yawDeg = -mc.player.getYaw(tickDelta);
        }

        return new PlanePose(center, yawDeg, pitchDeg);
    }

    private void renderCircleFill(Render3DEvent event, Vec3d cameraPos, Vec3d center, float radius, int fillColor) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();

        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        org.joml.Matrix4f matrix = event.getMatrices().peek().getPositionMatrix();

        float y = (float) (center.y + mc.player.getHeight());
        float cx = (float) (center.x - cameraPos.x);
        float cy = (float) (y - cameraPos.y);
        float cz = (float) (center.z - cameraPos.z);

        int r = (fillColor >> 16) & 0xFF;
        int g = (fillColor >> 8) & 0xFF;
        int b = fillColor & 0xFF;
        int a = (fillColor >> 24) & 0xFF;

        builder.vertex(matrix, cx, cy, cz).color(r, g, b, a);

        int stepDeg = 5;
        for (int deg = 0; deg <= 360; deg += stepDeg) {
            double rad = Math.toRadians(deg);
            float x = cx + (float) (MathHelper.sin((float) rad) * radius);
            float z = cz + (float) (-MathHelper.cos((float) rad) * radius);
            builder.vertex(matrix, x, cy, z).color(r, g, b, a);
        }

        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void renderPlaneFill(Render3DEvent event, Vec3d cameraPos, PlanePose pose, int fillColor) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();

        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        float width = 4.0f;
        float height = 4.0f;
        float thickness = 1.5f;

        float halfW = width / 2.0f;
        float halfH = height / 2.0f;
        float halfT = thickness / 2.0f;

        event.getMatrices().push();
        event.getMatrices().translate(pose.center.x - cameraPos.x, pose.center.y - cameraPos.y, pose.center.z - cameraPos.z);

        if (Math.abs(pose.pitchDeg) > 0.001f) {
            event.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(pose.pitchDeg));
        }
        if (Math.abs(pose.yawDeg) > 0.001f) {
            event.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(pose.yawDeg));
        }

        org.joml.Matrix4f mat = event.getMatrices().peek().getPositionMatrix();

        float minX = -halfW;
        float maxX = halfW;
        float minY = -halfH;
        float maxY = halfH;
        float minZ = -halfT;
        float maxZ = halfT;

        drawBoxFaces(builder, mat, minX, minY, minZ, maxX, maxY, maxZ, fillColor);
        event.getMatrices().pop();

        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void drawBoxFaces(BufferBuilder buffer, org.joml.Matrix4f matrix,
                                     float minX, float minY, float minZ,
                                     float maxX, float maxY, float maxZ,
                                     int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
    }

    private void renderPlaneOutline(Render3DEvent event, Vec3d cameraPos, PlanePose pose, int outlineColor) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();

        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float width = 4.0f;
        float height = 4.0f;
        float thickness = 1.5f;

        float halfW = width / 2.0f;
        float halfH = height / 2.0f;
        float halfT = thickness / 2.0f;

        event.getMatrices().push();
        event.getMatrices().translate(pose.center.x - cameraPos.x, pose.center.y - cameraPos.y, pose.center.z - cameraPos.z);

        if (Math.abs(pose.pitchDeg) > 0.001f) {
            event.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(pose.pitchDeg));
        }
        if (Math.abs(pose.yawDeg) > 0.001f) {
            event.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(pose.yawDeg));
        }

        org.joml.Matrix4f mat = event.getMatrices().peek().getPositionMatrix();

        float minX = -halfW;
        float maxX = halfW;
        float minY = -halfH;
        float maxY = halfH;
        float minZ = -halfT;
        float maxZ = halfT;

        drawBoxEdges(builder, mat, minX, minY, minZ, maxX, maxY, maxZ, outlineColor);
        event.getMatrices().pop();

        RenderUtility.buildBuffer(builder);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void drawBoxEdges(BufferBuilder buffer, org.joml.Matrix4f matrix,
                                     float minX, float minY, float minZ,
                                     float maxX, float maxY, float maxZ,
                                     int color) {
        drawLine(buffer, matrix, minX, minY, minZ, maxX, minY, minZ, color);
        drawLine(buffer, matrix, maxX, minY, minZ, maxX, minY, maxZ, color);
        drawLine(buffer, matrix, maxX, minY, maxZ, minX, minY, maxZ, color);
        drawLine(buffer, matrix, minX, minY, maxZ, minX, minY, minZ, color);

        drawLine(buffer, matrix, minX, maxY, minZ, maxX, maxY, minZ, color);
        drawLine(buffer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, color);
        drawLine(buffer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, color);
        drawLine(buffer, matrix, minX, maxY, maxZ, minX, maxY, minZ, color);

        drawLine(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, color);
        drawLine(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, color);
        drawLine(buffer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, color);
        drawLine(buffer, matrix, minX, minY, maxZ, minX, maxY, maxZ, color);
    }

    private static void drawLine(BufferBuilder buffer, org.joml.Matrix4f matrix,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
    }

    private void useItemFromInventory(HolyWorldItem item) {
        if (mc.player == null || mc.world == null) return;
        
        // Check if player inside is required for trap items
        if (requirePlayerInside.isEnabled() && item.isTrap) {
            Vec3d playerPos = mc.player.getPos();
            double floorY = item.followsPlayer ? playerPos.y : resolveFloorY(playerPos);
            Vec3d zoneCenter = item.followsPlayer ? zoneCenter(item, playerPos) : placedCenter(item, playerPos.x, floorY, playerPos.z);
            
            if (!hasOtherPlayerIn(item, zoneCenter)) {
                moscow.definevisuals.DefineVisuals.getInstance().getNotificationManager().addNotification(
                    moscow.definevisuals.systems.notifications.NotificationType.ERROR,
                    "Внутри нет игроков"
                );
                return;
            }
        }
        
        // Save original slot
        originalSlot = mc.player.getInventory().selectedSlot;
        
        // Find item in entire inventory
        int itemSlot = findItemInInventory(item.mcItem);
        if (itemSlot == -1) {
            moscow.definevisuals.DefineVisuals.getInstance().getNotificationManager().addNotification(
                moscow.definevisuals.systems.notifications.NotificationType.ERROR,
                "Предмет не найден в инвентаре"
            );
            return;
        }
        
        // Track item source
        itemSourceSlot = itemSlot;
        itemFromInventory = itemSlot >= 9;
        
        // If item is in hotbar, just select it
        if (itemSlot < 9) {
            InventoryUtility.selectHotbarSlot(itemSlot);
        } else {
            // Item is in main inventory, swap with current hotbar slot
            InventoryUtility.hotbarSwap(itemSlot, originalSlot);
        }
        
        // Simulate right-click using interaction manager
        if (mc.interactionManager != null) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }
        
        // Schedule restoration
        swapStartTime = Util.getMeasuringTimeMs();
        
        moscow.definevisuals.DefineVisuals.getInstance().getNotificationManager().addNotification(
            moscow.definevisuals.systems.notifications.NotificationType.SUCCESS,
            item.displayName + " активирован"
        );
    }

    private int findItemInInventory(net.minecraft.item.Item item) {
        if (mc.player == null) return -1;
        
        // Check main hand first
        if (mc.player.getMainHandStack().getItem() == item) {
            return mc.player.getInventory().selectedSlot;
        }
        
        // Check hotbar slots (0-8) first for performance
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        
        // Check main inventory slots (9-35)
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private void restoreOriginalItem() {
        if (mc.player == null || originalSlot == -1) return;
        
        // If item came from main inventory, perform reverse swap to restore original item
        if (itemFromInventory && itemSourceSlot >= 9) {
            InventoryUtility.hotbarSwap(itemSourceSlot, originalSlot);
        } else {
            // Item was in hotbar, just select the original slot
            InventoryUtility.selectHotbarSlot(originalSlot);
        }
        
        originalSlot = -1;
        itemSourceSlot = -1;
        itemFromInventory = false;
        originalMainHand = null;
    }

    private void sendCommand(String command) {
        if (mc.player != null && mc.player.networkHandler != null) {
            mc.player.networkHandler.sendChatMessage(command);
        }
    }


    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getSnowBind() {
        return snowBind;
    }

    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getPumpkinBind() {
        return pumpkinBind;
    }

    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getChorusBind() {
        return chorusBind;
    }

    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getNetherStarBind() {
        return netherStarBind;
    }

    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getPrismarineBind() {
        return prismarineBind;
    }

    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getFireChargeBind() {
        return fireChargeBind;
    }

    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getAutoSellBind() {
        return autoSellBind;
    }

    @Generated
    public moscow.definevisuals.systems.setting.settings.BindSetting getItemSearchBind() {
        return itemSearchBind;
    }

    @Generated
    public BooleanSetting getRequirePlayerInside() {
        return requirePlayerInside;
    }

    private static class ActiveZone {
        final HolyWorldItem item;
        Vec3d center;
        final double floorY;
        final long expiresAt;
        float occupiedBlend;

        ActiveZone(HolyWorldItem item, Vec3d center, double floorY, long expiresAt) {
            this.item = item;
            this.center = center;
            this.floorY = floorY;
            this.expiresAt = expiresAt;
        }

        void moveTo(Vec3d newCenter) {
            this.center = newCenter;
        }
    }

    public enum Shape {
        CIRCLE, CUBE, STRUCTURE
    }

    public static final class HolyWorldItem {
        public static final HolyWorldItem SNOW = new HolyWorldItem("Snow", "Снежок", 0, 1, 0.42f, Shape.CIRCLE, 0, false, false, false, net.minecraft.item.Items.SNOWBALL);
        public static final HolyWorldItem PUMPKIN = new HolyWorldItem("Pumpkin", "Голова Джека", 0, 1, 4.0f, Shape.CUBE, 0, true, false, false, net.minecraft.item.Items.CARVED_PUMPKIN);
        public static final HolyWorldItem CHORUS = new HolyWorldItem("Chorus", "Трапка", 0, 1, 10.0f, Shape.CIRCLE, 15000L, true, false, true, net.minecraft.item.Items.POPPED_CHORUS_FRUIT);
        public static final HolyWorldItem NETHER_STAR = new HolyWorldItem("NetherStar", "Стан", 0, 1, 4.0f, Shape.CUBE, 15000L, true, false, false, net.minecraft.item.Items.NETHER_STAR);
        public static final HolyWorldItem PRISMARINE = new HolyWorldItem("Prismarine", "Взрывная трапка", 0, 1, 10.0f, Shape.STRUCTURE, 15000L, true, false, true, net.minecraft.item.Items.PRISMARINE_SHARD);
        public static final HolyWorldItem FIRE_CHARGE = new HolyWorldItem("FireCharge", "Взрывная штучка", 0, 1, 10.0f, Shape.CIRCLE, 0, true, false, false, net.minecraft.item.Items.FIRE_CHARGE);

        public String name;
        public String displayName;
        public int price;
        public int count;
        public float size;
        public Shape shape;
        public long durationMs;
        public boolean floorAnchored;
        public boolean followsPlayer;
        public boolean isTrap;
        public net.minecraft.item.Item mcItem;

        public HolyWorldItem(String name, String displayName, int price, int count, float size, Shape shape, long durationMs, boolean floorAnchored, boolean followsPlayer, boolean isTrap, net.minecraft.item.Item mcItem) {
            this.name = name;
            this.displayName = displayName;
            this.price = price;
            this.count = count;
            this.size = size;
            this.shape = shape;
            this.durationMs = durationMs;
            this.floorAnchored = floorAnchored;
            this.followsPlayer = followsPlayer;
            this.isTrap = isTrap;
            this.mcItem = mcItem;
        }

        public static HolyWorldItem match(ItemStack stack) {
            if (stack == null || stack.isEmpty()) return null;
            
            // Match by item type directly - more aggressive matching
            net.minecraft.item.Item item = stack.getItem();
            if (item == net.minecraft.item.Items.SNOWBALL) return SNOW;
            if (item == net.minecraft.item.Items.CARVED_PUMPKIN) return PUMPKIN;
            if (item == net.minecraft.item.Items.POPPED_CHORUS_FRUIT) return CHORUS;
            if (item == net.minecraft.item.Items.NETHER_STAR) return NETHER_STAR;
            if (item == net.minecraft.item.Items.PRISMARINE_SHARD) return PRISMARINE;
            if (item == net.minecraft.item.Items.FIRE_CHARGE) return FIRE_CHARGE;
            
            // Fallback: match by display name (for custom items)
            String name = stack.getName().getString();
            if (name == null || name.isEmpty()) return null;
            
            String lowerName = name.toLowerCase();
            if (lowerName.contains("снежок") || lowerName.contains("snow")) return SNOW;
            if (lowerName.contains("голова джека") || lowerName.contains("pumpkin")) return PUMPKIN;
            if (lowerName.contains("трапка") || lowerName.contains("chorus")) return CHORUS;
            if (lowerName.contains("стан") || lowerName.contains("nether") || lowerName.contains("star")) return NETHER_STAR;
            if (lowerName.contains("взрывная трапка") || lowerName.contains("prismarine")) return PRISMARINE;
            if (lowerName.contains("взрывная штучка") || lowerName.contains("fire")) return FIRE_CHARGE;
            
            return null;
        }
    }
}
