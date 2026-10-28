package moscow.definevisuals.systems.modules.modules.visuals;

import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.notifications.NotificationType;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ButtonSetting;
import moscow.definevisuals.DefineVisuals;
import pulse.cosmetic.LocalCosmetics;

@ModuleInfo(
   name = "Cosmetics",
   category = ModuleCategory.VISUALS,
   desc = "3D косметика для персонажа из Pulse (крылья, шляпы, питомцы)",
   enabledByDefault = true
)
public class Cosmetics extends BaseModule {
   private static Cosmetics INSTANCE;

   private final BooleanSetting capes = new BooleanSetting(this, "Плащи").enable();
   private final BooleanSetting models = new BooleanSetting(this, "3D Модели").enable();
   private final ButtonSetting nextCosmetic = new ButtonSetting(this, "Следующая косметика (F9)").action(() -> {
      int next = LocalCosmetics.cycleNext();
      if (next >= 0 && next < LocalCosmetics.size()) {
         DefineVisuals.getInstance().getNotificationManager().addNotification(
            NotificationType.INFO, "Косметика: " + LocalCosmetics.name(next)
         );
      }
   });

   public Cosmetics() {
      INSTANCE = this;
   }

   public static Cosmetics getInstance() {
      return INSTANCE;
   }

   public static boolean isActive() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   public static boolean isModelsEnabled() {
      return isActive() && INSTANCE.models.isEnabled();
   }

   public static boolean isCapesEnabled() {
      return isActive() && INSTANCE.capes.isEnabled();
   }
}
