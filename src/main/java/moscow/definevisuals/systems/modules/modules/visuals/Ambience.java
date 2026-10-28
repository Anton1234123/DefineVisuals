package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.shader.impl.SkyShaderProgram;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SelectSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.EntityUtility;
import moscow.definevisuals.utility.render.DynamicLightUtility;
import moscow.definevisuals.utility.render.DrawUtility;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@ModuleInfo(name = "Ambience", category = ModuleCategory.VISUALS, desc = "modules.descriptions.ambience")
public class Ambience extends BaseModule {
   private static final int CLIENT_NIGHT_VISION_DURATION = 400;
   private static final int CLIENT_NIGHT_VISION_REFRESH_TICKS = 220;
   private static final double DYNAMIC_UPDATE_DISTANCE_SQUARED = 0.0225;
   private static final float FOG_COLOR_BRIGHTNESS = 0.55F;

   public final BooleanSetting endSky = new BooleanSetting(this, "modules.settings.ambience.end_sky");

   private final SelectSetting changeColor = new SelectSetting(this, "modules.settings.ambience.change_color");
   private final SelectSetting.Value skyColorTarget = new SelectSetting.Value(this.changeColor, "modules.settings.ambience.change_color.sky").select();
   private final SelectSetting.Value cloudColorTarget = new SelectSetting.Value(this.changeColor, "modules.settings.ambience.change_color.clouds").select();
   private final SelectSetting.Value starsColorTarget = new SelectSetting.Value(this.changeColor, "modules.settings.ambience.change_color.stars").select();
   private final BooleanSetting themeSync = new BooleanSetting(this, "theme.sync").enable();
   private final ColorSetting skyColor = new ColorSetting(this, "modules.settings.ambience.sky_color", () -> this.themeSync.isEnabled() || !this.skyColorTarget.isSelected())
      .color(Colors.ACCENT)
      .alpha(false);
   private final ColorSetting cloudColor = new ColorSetting(this, "modules.settings.ambience.cloud_color", () -> this.themeSync.isEnabled() || !this.cloudColorTarget.isSelected())
      .color(Colors.ACCENT)
      .alpha(false);
   private final ColorSetting starsColor = new ColorSetting(this, "modules.settings.ambience.stars_color", () -> this.themeSync.isEnabled() || !this.starsColorTarget.isSelected())
      .color(Colors.ACCENT);

   private final BooleanSetting nightMode = new BooleanSetting(this, "modules.settings.ambience.night_mode");
   public final BooleanSetting bright = new BooleanSetting(this, "modules.settings.ambience.bright", () -> this.nightMode.isEnabled()).enable();
   private final BooleanSetting nightModeThemeSync = new BooleanSetting(this, "modules.settings.ambience.night_mode.sync", () -> !this.nightMode.isEnabled()).enable();
   private final ColorSetting nightModeColor = new ColorSetting(
         this,
         "modules.settings.ambience.night_mode.color",
         () -> !this.nightMode.isEnabled() || this.nightModeThemeSync.isEnabled()
      )
      .color(new ColorRGBA(80.0F, 120.0F, 220.0F, 255.0F))
      .alpha(false);
   private final SliderSetting nightModeStrength = new SliderSetting(this, "modules.settings.ambience.night_mode.strength", () -> !this.nightMode.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(1.0F)
      .currentValue(70.0F)
      .suffix("%");

   private final BooleanSetting colorIsolation = new BooleanSetting(this, "modules.settings.ambience.color_isolation");
   private final ColorSetting colorIsolationColor = new ColorSetting(this, "modules.settings.ambience.color_isolation.color", () -> !this.colorIsolation.isEnabled())
      .color(new ColorRGBA(0.0F, 122.0F, 255.0F, 255.0F))
      .alpha(false);
   private final ModeSetting colorIsolationMode = new ModeSetting(this, "modules.settings.ambience.color_isolation.mode", () -> !this.colorIsolation.isEnabled());
   private final ModeSetting.Value colorIsolationStrict = new ModeSetting.Value(this.colorIsolationMode, "modules.settings.ambience.color_isolation.mode.strict");
   private final ModeSetting.Value colorIsolationBalanced = new ModeSetting.Value(this.colorIsolationMode, "modules.settings.ambience.color_isolation.mode.balanced").select();
   private final ModeSetting.Value colorIsolationLoose = new ModeSetting.Value(this.colorIsolationMode, "modules.settings.ambience.color_isolation.mode.loose");
   private final ModeSetting.Value colorIsolationCustom = new ModeSetting.Value(this.colorIsolationMode, "modules.settings.ambience.color_isolation.mode.custom");
   private final SliderSetting colorIsolationSensitivity = new SliderSetting(
         this,
         "modules.settings.ambience.color_isolation.sensitivity",
         () -> !this.colorIsolation.isEnabled() || !this.colorIsolationCustom.isSelected()
      )
      .min(0.0F)
      .max(100.0F)
      .step(1.0F)
      .currentValue(50.0F);
   private final SliderSetting colorIsolationBackground = new SliderSetting(this, "modules.settings.ambience.color_isolation.background", () -> !this.colorIsolation.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(1.0F)
      .currentValue(0.0F);

   private final ModeSetting skybox = new ModeSetting(this, "modules.settings.ambience.skybox");
   private final ModeSetting.Value defaultSkybox = new ModeSetting.Value(this.skybox, "modules.settings.ambience.skybox.default");
   private final ModeSetting.Value brightCloudsSkybox = new ModeSetting.Value(this.skybox, "modules.settings.ambience.skybox.bright_clouds").select();
   private final ModeSetting.Value lakeSkybox = new ModeSetting.Value(this.skybox, "modules.settings.ambience.skybox.lake");
   private final ModeSetting.Value cloudSpaceSkybox = new ModeSetting.Value(this.skybox, "modules.settings.ambience.skybox.cloud_space");
   private final ModeSetting.Value clearEveningSkybox = new ModeSetting.Value(this.skybox, "modules.settings.ambience.skybox.clear_evening");
   private final ModeSetting.Value underwaterSkybox = new ModeSetting.Value(this.skybox, "modules.settings.ambience.skybox.underwater");

   private final BooleanSetting shader = new BooleanSetting(this, "modules.settings.ambience.shader");
   private final ModeSetting shaderType = new ModeSetting(this, "modules.settings.ambience.shader_type", () -> !this.shader.isEnabled());
   private final ModeSetting.Value nebulaShader = new ModeSetting.Value(this.shaderType, "modules.settings.ambience.shader_type.nebula").select();
   private final ModeSetting.Value causticShader = new ModeSetting.Value(this.shaderType, "modules.settings.ambience.shader_type.caustic");
   private final ModeSetting.Value galaxyShader = new ModeSetting.Value(this.shaderType, "modules.settings.ambience.shader_type.galaxy");
   private final ModeSetting.Value spaceShader = new ModeSetting.Value(this.shaderType, "modules.settings.ambience.shader_type.space");
   private final SliderSetting shaderOpacity = new SliderSetting(this, "modules.settings.ambience.shader_opacity", () -> !this.shader.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(5.0F)
      .currentValue(70.0F)
      .suffix("%");

   private final BooleanSetting customTime = new BooleanSetting(this, "modules.settings.ambience.custom_time");
   private final SliderSetting time = new SliderSetting(this, "modules.settings.ambience.time", () -> !this.customTime.isEnabled())
      .step(500.0F)
      .min(0.0F)
      .max(24000.0F)
      .currentValue(17000.0F);

   private final ModeSetting brightnessMode = new ModeSetting(this, "modules.settings.ambience.mode", () -> !this.isBrightnessEnabled());
   private final ModeSetting.Value gammaBrightness = new ModeSetting.Value(this.brightnessMode, "modules.settings.ambience.mode.gamma");
   private final ModeSetting.Value effectBrightness = new ModeSetting.Value(this.brightnessMode, "modules.settings.ambience.mode.effect");
   private final ModeSetting.Value dynamicBrightness = new ModeSetting.Value(this.brightnessMode, "modules.settings.ambience.mode.dynamic").select();
   private final SliderSetting dynamicRadius = new SliderSetting(
         this,
         "modules.settings.ambience.dynamic.radius",
         () -> !this.isBrightnessEnabled() || !this.dynamicBrightness.isSelected()
      )
      .min(4.0F)
      .max(12.0F)
      .step(0.5F)
      .currentValue(12.0F);
   private final SliderSetting dynamicLight = new SliderSetting(
         this,
         "modules.settings.ambience.dynamic.light",
         () -> !this.isBrightnessEnabled() || !this.dynamicBrightness.isSelected()
      )
      .min(8.0F)
      .max(15.0F)
      .step(1.0F)
      .currentValue(15.0F);
   private final BooleanSetting dynamicOnlyInCave = new BooleanSetting(
      this,
      "modules.settings.ambience.dynamic.only_in_cave",
      () -> !this.isBrightnessEnabled() || !this.dynamicBrightness.isSelected()
   );

   private final BooleanSetting fog = new BooleanSetting(this, "modules.settings.ambience.fog");
   private final ModeSetting fogColorMode = new ModeSetting(this, "modules.settings.ambience.fog.color_mode", () -> !this.fog.isEnabled());
   private final ModeSetting.Value fogColorBiome = new ModeSetting.Value(this.fogColorMode, "modules.settings.ambience.fog.color_mode.biome").select();
   private final ModeSetting.Value fogColorTheme = new ModeSetting.Value(this.fogColorMode, "modules.settings.ambience.fog.color_mode.theme");
   private final ModeSetting.Value fogColorCustom = new ModeSetting.Value(this.fogColorMode, "modules.settings.ambience.fog.color_mode.custom");
   private final ColorSetting fogColor = new ColorSetting(this, "modules.settings.ambience.fog.color", () -> !this.fog.isEnabled() || !this.fogColorCustom.isSelected())
      .color(new ColorRGBA(205.0F, 210.0F, 220.0F, 255.0F))
      .alpha(false);
   private final SliderSetting fogDensity = new SliderSetting(this, "modules.settings.ambience.fog.density", () -> !this.fog.isEnabled())
      .min(10.0F)
      .max(250.0F)
      .step(5.0F)
      .currentValue(90.0F)
      .suffix("%");
   private final SliderSetting fogCoverage = new SliderSetting(this, "modules.settings.ambience.fog.coverage", () -> !this.fog.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(1.0F)
      .currentValue(55.0F)
      .suffix("%");
   private final SliderSetting fogLevel = new SliderSetting(this, "modules.settings.ambience.fog.level", () -> !this.fog.isEnabled())
      .min(-64.0F)
      .max(320.0F)
      .step(1.0F)
      .currentValue(66.0F);
   private final SliderSetting fogThickness = new SliderSetting(this, "modules.settings.ambience.fog.thickness", () -> !this.fog.isEnabled())
      .min(2.0F)
      .max(48.0F)
      .step(1.0F)
      .currentValue(6.0F);
   private final ModeSetting fogQuality = new ModeSetting(this, "modules.settings.ambience.fog.quality", () -> !this.fog.isEnabled());
   private final ModeSetting.Value fogQualityLow = new ModeSetting.Value(this.fogQuality, "modules.settings.ambience.fog.quality.low");
   private final ModeSetting.Value fogQualityHigh = new ModeSetting.Value(this.fogQuality, "modules.settings.ambience.fog.quality.high").select();

   private final BooleanSetting wetWorld = new BooleanSetting(this, "modules.settings.ambience.wet_world");
   private final SliderSetting wetWorldReflection = new SliderSetting(this, "modules.settings.ambience.wet_world.reflection", () -> !this.wetWorld.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(5.0F)
      .currentValue(70.0F)
      .suffix("%");
   private final SliderSetting wetWorldAmount = new SliderSetting(this, "modules.settings.ambience.wet_world.amount", () -> !this.wetWorld.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(5.0F)
      .currentValue(60.0F)
      .suffix("%");
   private final ModeSetting wetWorldQuality = new ModeSetting(this, "modules.settings.ambience.wet_world.quality", () -> !this.wetWorld.isEnabled());
   private final ModeSetting.Value wetWorldQualityLow = new ModeSetting.Value(this.wetWorldQuality, "modules.settings.ambience.wet_world.quality.low");
   private final ModeSetting.Value wetWorldQualityMedium = new ModeSetting.Value(this.wetWorldQuality, "modules.settings.ambience.wet_world.quality.medium").select();
   private final ModeSetting.Value wetWorldQualityHigh = new ModeSetting.Value(this.wetWorldQuality, "modules.settings.ambience.wet_world.quality.high");

   private final BooleanSetting rain = new BooleanSetting(this, "modules.settings.ambience.rain");
   private final SliderSetting rainDensity = new SliderSetting(this, "modules.settings.ambience.rain.density", () -> !this.rain.isEnabled())
      .min(10.0F)
      .max(200.0F)
      .step(5.0F)
      .currentValue(100.0F)
      .suffix("%");
   private final SliderSetting rainOpacity = new SliderSetting(this, "modules.settings.ambience.rain.opacity", () -> !this.rain.isEnabled())
      .min(5.0F)
      .max(100.0F)
      .step(5.0F)
      .currentValue(45.0F)
      .suffix("%");
   private final SliderSetting rainDrops = new SliderSetting(this, "modules.settings.ambience.rain.drops", () -> !this.rain.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(5.0F)
      .currentValue(60.0F)
      .suffix("%");
   private final SliderSetting rainSplashes = new SliderSetting(this, "modules.settings.ambience.rain.splashes", () -> !this.rain.isEnabled())
      .min(0.0F)
      .max(100.0F)
      .step(5.0F)
      .currentValue(60.0F)
      .suffix("%");

   private long oldTime;
   private Vec3d dynamicLightPos;
   private int dynamicLightRadius = -1;
   private int dynamicLightLevel = -1;
   private boolean dynamicLightActive;
   private boolean isPlayerInCave;
   private final EventListener<ReceivePacketEvent> onReceivePacket = event -> {
      if (event.getPacket() instanceof WorldTimeUpdateS2CPacket && this.customTime.isEnabled()) {
         event.cancel();
      }
   };
   private final EventListener<Render3DEvent> onRender3D = event -> {
      if (mc.world != null && mc.player != null) {
         this.updateDynamicBrightness();
         Camera camera = event.getCamera();
         if (camera != null && camera.isReady() && camera.getSubmersionType() == CameraSubmersionType.NONE) {
            if (this.rain.isEnabled()) {
               this.renderRain(camera, event.getPositionMatrix(), event.getProjectionMatrix());
            }
            if (this.fog.isEnabled()) {
               this.renderFog(camera, event.getPositionMatrix(), event.getProjectionMatrix());
            }
         }
      } else {
         this.isPlayerInCave = false;
         this.clearDynamicBrightness();
      }
   };

   public boolean isBrightnessEnabled() {
      return this.bright.isEnabled() && !this.nightMode.isEnabled();
   }

   public boolean shouldUseGammaBrightness() {
      return this.isEnabled() && this.isBrightnessEnabled() && this.gammaBrightness.isSelected();
   }

   public boolean isNightModeActive() {
      return this.isEnabled() && this.nightMode.isEnabled();
   }

   public Vector3f getNightModeTint() {
      ColorRGBA color = this.nightModeThemeSync.isEnabled() ? Colors.getFlatColor() : this.nightModeColor.getColor();
      return new Vector3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
   }

   public float getNightModeStrengthValue() {
      return this.nightModeStrength.getCurrentValue() / 100.0F;
   }

   public boolean isColorIsolationEnabled() {
      return this.isEnabled() && this.colorIsolation.isEnabled();
   }

   public Vector3f getColorIsolationColorVector() {
      ColorRGBA color = this.colorIsolationColor.getColor();
      return new Vector3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
   }

   private float getColorIsolationSensitivityValue() {
      if (this.colorIsolationCustom.isSelected()) {
         return this.colorIsolationSensitivity.getCurrentValue() / 100.0F;
      } else if (this.colorIsolationStrict.isSelected()) {
         return 0.15F;
      } else {
         return this.colorIsolationLoose.isSelected() ? 0.85F : 0.5F;
      }
   }

   public float getColorIsolationHue() {
      return (8.0F + this.getColorIsolationSensitivityValue() * 47.0F) / 360.0F;
   }

   public float getColorIsolationSaturation() {
      return 0.55F - this.getColorIsolationSensitivityValue() * 0.43F;
   }

   public float getColorIsolationValue() {
      return 0.45F - this.getColorIsolationSensitivityValue() * 0.37F;
   }

   public float getColorIsolationBackgroundValue() {
      return this.colorIsolationBackground.getCurrentValue() / 100.0F;
   }

   public boolean hasCustomSkybox() {
      return !this.skybox.is(this.defaultSkybox);
   }

   public boolean hasShaderSkybox() {
      return this.shader.isEnabled();
   }

   public boolean shouldRenderCustomSkybox() {
      if (!this.isEnabled()) return false;
      return this.hasShaderSkybox() || this.hasCustomSkybox();
   }

   public boolean shouldTintSky() {
      return this.isEnabled() && this.skyColorTarget.isSelected();
   }

   public boolean shouldTintClouds() {
      return this.isEnabled() && this.cloudColorTarget.isSelected();
   }

   public boolean shouldTintStars() {
      return this.isEnabled() && this.starsColorTarget.isSelected();
   }

   public ColorRGBA getResolvedSkyColor() {
      return this.themeSync.isEnabled() ? Colors.getFlatColor() : this.skyColor.getColor();
   }

   public ColorRGBA getResolvedCloudColor() {
      return this.themeSync.isEnabled() ? Colors.getFlatColor() : this.cloudColor.getColor();
   }

   public ColorRGBA getResolvedStarsColor() {
      return this.themeSync.isEnabled() ? Colors.getFlatColor() : this.starsColor.getColor();
   }

   public Identifier getSkyboxTexture() {
      if (this.skybox.is(this.defaultSkybox)) {
         return null;
      }
      int index = this.skybox.getValues().indexOf(this.skybox.getValue());
      if (index <= 0) return null;
      return DefineVisuals.id("sky/" + index + ".png");
   }

   public SkyShaderProgram getSkyShaderProgram() {
      if (this.causticShader.isSelected()) {
         return DrawUtility.skyCausticProgram;
      } else {
         return DrawUtility.skyNebulaProgram;
      }
   }

   public float getShaderOpacity() {
      return this.shaderOpacity.getCurrentValue() / 100.0F;
   }

   public boolean isFogEnabled() {
      return this.isEnabled() && this.fog.isEnabled();
   }

   public boolean isWetWorldEnabled() {
      return this.isEnabled() && this.wetWorld.isEnabled();
   }

   public boolean isRainEnabled() {
      return this.isEnabled() && this.rain.isEnabled();
   }

   @Override
   public void tick() {
      if (mc.world == null) {
         this.removeClientNightVision();
         this.clearDynamicBrightness();
         this.isPlayerInCave = false;
         return;
      }

      if (this.customTime.isEnabled()) {
         mc.world.getLevelProperties().setTimeOfDay((long)this.time.getCurrentValue());
      }

      this.updateBrightnessEffect();
      this.updateCaveStatus();
      if (!this.shouldUseDynamicBrightness()) {
         this.clearDynamicBrightness();
      }

      super.tick();
   }

   @Override
   public void onEnable() {
      if (EntityUtility.isInGame() && mc.world != null) {
         this.oldTime = mc.world.getTime();
      }
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.removeClientNightVision();
      this.clearDynamicBrightness();
      if (EntityUtility.isInGame() && mc.world != null) {
         mc.world.getLevelProperties().setTimeOfDay(this.oldTime);
      }
      super.onDisable();
   }

   private void updateBrightnessEffect() {
      if (mc.player == null) {
         return;
      }

      if (!this.shouldUseEffectBrightness()) {
         this.removeClientNightVision();
         return;
      }

      StatusEffectInstance current = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
      if (current == null || this.isClientNightVision(current) && current.getDuration() <= CLIENT_NIGHT_VISION_REFRESH_TICKS) {
         mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, CLIENT_NIGHT_VISION_DURATION, 0, false, false, false));
      }
   }

   private boolean shouldUseEffectBrightness() {
      return this.isEnabled() && this.isBrightnessEnabled() && this.effectBrightness.isSelected();
   }

   private boolean shouldUseDynamicBrightness() {
      return this.isEnabled()
         && this.isBrightnessEnabled()
         && this.dynamicBrightness.isSelected()
         && (!this.dynamicOnlyInCave.isEnabled() || this.isPlayerInCave());
   }

   private void updateCaveStatus() {
      if (mc.player == null || mc.world == null) {
         this.isPlayerInCave = false;
         return;
      }

      BlockPos pos = BlockPos.ofFloored(mc.player.getEyePos());
      int skyLight = mc.world.getLightLevel(LightType.SKY, pos);
      if (mc.world.isSkyVisible(pos) || skyLight >= 8) {
         this.isPlayerInCave = false;
      } else if (skyLight <= 4) {
         this.isPlayerInCave = true;
      }
   }

   private boolean isPlayerInCave() {
      return this.isPlayerInCave;
   }

   private void removeClientNightVision() {
      if (mc.player == null) {
         return;
      }

      StatusEffectInstance current = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
      if (this.isClientNightVision(current)) {
         mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
      }
   }

   private boolean isClientNightVision(StatusEffectInstance effect) {
      return effect != null
         && effect.getEffectType() == StatusEffects.NIGHT_VISION
         && effect.getAmplifier() == 0
         && effect.getDuration() <= CLIENT_NIGHT_VISION_DURATION;
   }

   private void updateDynamicBrightness() {
      if (mc.player == null || mc.world == null || !this.shouldUseDynamicBrightness()) {
         this.clearDynamicBrightness();
         return;
      }

      Vec3d pos = mc.player.getPos().add(0.0, (double)mc.player.getHeight() * 0.5, 0.0);
      float radius = this.dynamicRadius.getCurrentValue();
      float light = this.dynamicLight.getCurrentValue();
      int radiusCeil = MathHelper.ceil(radius);
      int lightFloor = MathHelper.floor(light);
      DynamicLightUtility.set(pos.x, pos.y, pos.z, radius, light);

      if (this.shouldRefreshDynamicBrightness(pos, radiusCeil, lightFloor)) {
         this.refreshDynamicBrightnessArea(this.dynamicLightPos, this.dynamicLightRadius);
         this.refreshDynamicBrightnessArea(pos, radiusCeil);
         this.dynamicLightPos = pos;
         this.dynamicLightRadius = radiusCeil;
         this.dynamicLightLevel = lightFloor;
         this.dynamicLightActive = true;
      }
   }

   private boolean shouldRefreshDynamicBrightness(Vec3d pos, int radius, int light) {
      if (!this.dynamicLightActive || this.dynamicLightPos == null) {
         return true;
      }

      return this.dynamicLightRadius != radius || this.dynamicLightLevel != light || pos.squaredDistanceTo(this.dynamicLightPos) >= DYNAMIC_UPDATE_DISTANCE_SQUARED;
   }

   private void clearDynamicBrightness() {
      if (!this.dynamicLightActive) {
         DynamicLightUtility.clear();
         return;
      }

      DynamicLightUtility.clear();
      this.refreshDynamicBrightnessArea(this.dynamicLightPos, this.dynamicLightRadius);
      this.dynamicLightActive = false;
      this.dynamicLightPos = null;
      this.dynamicLightRadius = -1;
      this.dynamicLightLevel = -1;
   }

   private void refreshDynamicBrightnessArea(Vec3d pos, int radius) {
      if (pos == null || radius <= 0 || mc.worldRenderer == null) {
         return;
      }

      int padding = Math.max(1, MathHelper.ceil((float)radius / Math.max(this.dynamicLight.getCurrentValue(), 1.0F)));
      int minX = ChunkSectionPos.getSectionCoord(MathHelper.floor(pos.x) - radius - padding);
      int minY = ChunkSectionPos.getSectionCoord(MathHelper.floor(pos.y) - radius - padding);
      int minZ = ChunkSectionPos.getSectionCoord(MathHelper.floor(pos.z) - radius - padding);
      int maxX = ChunkSectionPos.getSectionCoord(MathHelper.floor(pos.x) + radius + padding);
      int maxY = ChunkSectionPos.getSectionCoord(MathHelper.floor(pos.y) + radius + padding);
      int maxZ = ChunkSectionPos.getSectionCoord(MathHelper.floor(pos.z) + radius + padding);
      mc.worldRenderer.scheduleChunkRenders(minX, minY, minZ, maxX, maxY, maxZ);
   }

   private void renderRain(Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
      float density = this.rainDensity.getCurrentValue() / 100.0F;
      float opacity = this.rainOpacity.getCurrentValue() / 100.0F;
      float drops = this.rainDrops.getCurrentValue() / 100.0F;
      float splashes = this.rainSplashes.getCurrentValue() / 100.0F;
      
      if (DrawUtility.rainRenderer != null) {
         DrawUtility.rainRenderer.render(positionMatrix, projectionMatrix, density, opacity, drops, splashes);
      }
   }

   private void renderFog(Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
      ColorRGBA fogColor = this.getFogColorValue();
      float density = this.fogDensity.getCurrentValue() / 100.0F;
      float coverage = this.fogCoverage.getCurrentValue() / 100.0F;
      float level = this.fogLevel.getCurrentValue();
      float thickness = this.fogThickness.getCurrentValue();
      boolean highQuality = this.fogQualityHigh.isSelected();
      
      if (DrawUtility.fogRenderer != null) {
         DrawUtility.fogRenderer.render(positionMatrix, projectionMatrix, fogColor, density, coverage, level, thickness, highQuality);
      }
   }

   private ColorRGBA getFogColorValue() {
      if (this.fogColorTheme.isSelected()) {
         return Colors.getFlatColor();
      }
      if (this.fogColorCustom.isSelected()) {
         return this.fogColor.getColor();
      }
      Vec3d pos = mc.gameRenderer.getCamera().getPos();
      int biomeColor = mc.world.getBiome(BlockPos.ofFloored(pos)).value().getFogColor();
      int r = MathHelper.clamp(Math.round((biomeColor >> 16 & 0xFF) + (255 - (biomeColor >> 16 & 0xFF)) * 0.55F), 0, 255);
      int g = MathHelper.clamp(Math.round((biomeColor >> 8 & 0xFF) + (255 - (biomeColor >> 8 & 0xFF)) * 0.55F), 0, 255);
      int b = MathHelper.clamp(Math.round((biomeColor & 0xFF) + (255 - (biomeColor & 0xFF)) * 0.55F), 0, 255);
      return new ColorRGBA(r, g, b, 255.0F);
   }

   @Generated
   public BooleanSetting getEndSky() {
      return this.endSky;
   }

   @Generated
   public SelectSetting getChangeColor() {
      return this.changeColor;
   }

   @Generated
   public SelectSetting.Value getSkyColorTarget() {
      return this.skyColorTarget;
   }

   @Generated
   public SelectSetting.Value getCloudColorTarget() {
      return this.cloudColorTarget;
   }

   @Generated
   public SelectSetting.Value getStarsColorTarget() {
      return this.starsColorTarget;
   }

   @Generated
   public BooleanSetting getThemeSync() {
      return this.themeSync;
   }

   @Generated
   public ColorSetting getSkyColor() {
      return this.skyColor;
   }

   @Generated
   public ColorSetting getCloudColor() {
      return this.cloudColor;
   }

   @Generated
   public ColorSetting getStarsColor() {
      return this.starsColor;
   }

   @Generated
   public ModeSetting getSkybox() {
      return this.skybox;
   }

   @Generated
   public BooleanSetting getShader() {
      return this.shader;
   }

   @Generated
   public ModeSetting getShaderType() {
      return this.shaderType;
   }

   @Generated
   public SliderSetting getShaderOpacitySetting() {
      return this.shaderOpacity;
   }

   @Generated
   public BooleanSetting getCustomTime() {
      return this.customTime;
   }

   @Generated
   public SliderSetting getTime() {
      return this.time;
   }

   @Generated
   public BooleanSetting getNightMode() {
      return this.nightMode;
   }

   @Generated
   public BooleanSetting getBright() {
      return this.bright;
   }

   @Generated
   public ModeSetting getBrightnessMode() {
      return this.brightnessMode;
   }

   @Generated
   public SliderSetting getDynamicRadius() {
      return this.dynamicRadius;
   }

   @Generated
   public SliderSetting getDynamicLight() {
      return this.dynamicLight;
   }

   @Generated
   public BooleanSetting getDynamicOnlyInCave() {
      return this.dynamicOnlyInCave;
   }

   @Generated
   public BooleanSetting getNightModeThemeSync() {
      return this.nightModeThemeSync;
   }

   @Generated
   public ColorSetting getNightModeColor() {
      return this.nightModeColor;
   }

   @Generated
   public SliderSetting getNightModeStrength() {
      return this.nightModeStrength;
   }

   @Generated
   public long getOldTime() {
      return this.oldTime;
   }

   @Generated
   public EventListener<ReceivePacketEvent> getOnReceivePacket() {
      return this.onReceivePacket;
   }

   @Generated
   public EventListener<Render3DEvent> getOnRender3D() {
      return this.onRender3D;
   }

   @Generated
   public BooleanSetting getColorIsolation() {
      return this.colorIsolation;
   }

   @Generated
   public ColorSetting getColorIsolationColor() {
      return this.colorIsolationColor;
   }

   @Generated
   public ModeSetting getColorIsolationMode() {
      return this.colorIsolationMode;
   }

   @Generated
   public SliderSetting getColorIsolationSensitivity() {
      return this.colorIsolationSensitivity;
   }

   @Generated
   public SliderSetting getColorIsolationBackground() {
      return this.colorIsolationBackground;
   }

   @Generated
   public BooleanSetting getFog() {
      return this.fog;
   }

   @Generated
   public ModeSetting getFogColorMode() {
      return this.fogColorMode;
   }

   @Generated
   public ColorSetting getFogColor() {
      return this.fogColor;
   }

   @Generated
   public SliderSetting getFogDensity() {
      return this.fogDensity;
   }

   @Generated
   public SliderSetting getFogCoverage() {
      return this.fogCoverage;
   }

   @Generated
   public SliderSetting getFogLevel() {
      return this.fogLevel;
   }

   @Generated
   public SliderSetting getFogThickness() {
      return this.fogThickness;
   }

   @Generated
   public ModeSetting getFogQuality() {
      return this.fogQuality;
   }

   @Generated
   public BooleanSetting getWetWorld() {
      return this.wetWorld;
   }

   @Generated
   public SliderSetting getWetWorldReflection() {
      return this.wetWorldReflection;
   }

   @Generated
   public SliderSetting getWetWorldAmount() {
      return this.wetWorldAmount;
   }

   @Generated
   public ModeSetting getWetWorldQuality() {
      return this.wetWorldQuality;
   }

   @Generated
   public BooleanSetting getRain() {
      return this.rain;
   }

   @Generated
   public SliderSetting getRainDensity() {
      return this.rainDensity;
   }

   @Generated
   public SliderSetting getRainOpacity() {
      return this.rainOpacity;
   }

   @Generated
   public SliderSetting getRainDrops() {
      return this.rainDrops;
   }

   @Generated
   public SliderSetting getRainSplashes() {
      return this.rainSplashes;
   }
}
