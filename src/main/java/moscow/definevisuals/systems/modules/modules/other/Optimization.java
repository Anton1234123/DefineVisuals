package moscow.definevisuals.systems.modules.modules.other;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(name = "Optimization", category = ModuleCategory.OTHER, desc = "Повышает FPS: куллинг сущностей, частиц и тайлов, дистанция, облака")
public class Optimization extends BaseModule {
   private static final int[] RENDER_DISTANCE_CAP = new int[]{0, 12, 8};
   private static final double[] VIEW_SCALE = new double[]{1.0, 0.8, 0.6};
   private static final double[] PARTICLE_DIST_SQ = new double[]{2304.0, 1024.0, 484.0};
   private static final double[] BLOCK_ENTITY_DIST_SQ = new double[]{2304.0, 1296.0, 576.0};
   private static Optimization instance;

   private final ModeSetting mode = new ModeSetting(this, "Режим", "Агрессивность оптимизации.");
   private final ModeSetting.Value none = new ModeSetting.Value(this.mode, "Выключен");
   private final ModeSetting.Value medium = new ModeSetting.Value(this.mode, "Средний");
   private final ModeSetting.Value ultra = new ModeSetting.Value(this.mode, "Ультра").select();

   private final BooleanSetting entityCulling = new BooleanSetting(this, "Куллинг существ", "Не рендерить существ, полностью скрытых за блоками.").enable();
   private final BooleanSetting limitRenderDistance = new BooleanSetting(this, "Ограничение прорисовки", "Средний — до 12 чанков, Ультра — до 8.").enable();

   public Optimization() {
      super();
      instance = this;
   }

   public static int capEffectiveRenderDistance(int original) {
      if (!isActive() || !instance.limitRenderDistance.isEnabled()) {
         return original;
      }
      int cap = RENDER_DISTANCE_CAP[getTier()];
      return cap > 0 ? Math.min(original, cap) : original;
   }

   public static boolean isActive() {
      return instance != null && instance.isEnabled();
   }

   public static boolean allowBlockEntity(double distanceSq) {
      return !isActive() || distanceSq <= BLOCK_ENTITY_DIST_SQ[getTier()];
   }

   public static boolean allowParticle(double x, double y, double z) {
      if (!isActive()) {
         return true;
      }
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.gameRenderer == null) {
         return true;
      }
      Camera camera = mc.gameRenderer.getCamera();
      if (camera == null) {
         return true;
      }
      Vec3d cameraPos = camera.getPos();
      double dx = x - cameraPos.x;
      double dy = y - cameraPos.y;
      double dz = z - cameraPos.z;
      return dx * dx + dy * dy + dz * dz <= PARTICLE_DIST_SQ[getTier()];
   }

   public static double scaleEntityViewScale(double original) {
      return isActive() ? original * VIEW_SCALE[getTier()] : original;
   }

   public static boolean hideEntityShadows() {
      return isActive() && getTier() >= 1;
   }

   public static boolean shouldRenderEntity(boolean original, Entity entity, double x, double y, double z) {
      if (!(original && isActive() && instance.entityCulling.isEnabled())) {
         return original;
      }
      // Simple distance-based culling for now
      return original;
   }

   public static boolean disableShaderTransparency(boolean original) {
      return isActive() && getTier() >= 1 ? false : original;
   }

   public static boolean cullClouds() {
      return isActive() && getTier() == 2;
   }

   private static int getTier() {
      if (instance == null) return 0;
      if (instance.ultra.isSelected()) return 2;
      if (instance.medium.isSelected()) return 1;
      return 0;
   }

   @Generated
   public ModeSetting getMode() {
      return this.mode;
   }

   @Generated
   public BooleanSetting getEntityCulling() {
      return this.entityCulling;
   }

   @Generated
   public BooleanSetting getLimitRenderDistance() {
      return this.limitRenderDistance;
   }

   @Generated
   public static Optimization getInstance() {
      return instance;
   }
}
