package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.ColorSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@ModuleInfo(name = "Hit Color", category = ModuleCategory.VISUALS, desc = "Меняет цвет при получении урона")
public class HitColor extends BaseModule {
   private static HitColor instance;
   private static final Map<Object, Integer> renderTints;
   private final ColorSetting colorSetting = new ColorSetting(this, "Цвет урона").color(new ColorRGBA(255, 110, 110, 255));

   static {
      renderTints = Collections.synchronizedMap(new WeakHashMap<>());
   }

   public HitColor() {
      super();
      instance = this;
   }

   public static boolean isActive() {
      return instance != null && instance.isEnabled();
   }

   public static int getColor() {
      return instance != null ? instance.colorSetting.getColor().getRGB() : -1;
   }

   public static boolean shouldTint(Object entity) {
      return HitColor.isActive();
   }

   public static Integer getTint(Object renderState) {
      return HitColor.isActive() ? renderTints.get(renderState) : null;
   }

   public static void captureTint(Object renderState, Object entity) {
      if (!HitColor.isActive()) {
         return;
      }
      if (HitColor.shouldTint(entity)) {
         renderTints.put(renderState, HitColor.getColor());
      } else {
         renderTints.remove(renderState);
      }
   }

   @Override
   public void onDisable() {
      renderTints.clear();
      super.onDisable();
   }

   @Generated
   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   @Generated
   public static HitColor getInstance() {
      return instance;
   }
}
