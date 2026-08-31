package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;

@ModuleInfo(name = "Self Tag", category = ModuleCategory.VISUALS, desc = "Показывает тег над своим персонажем")
public class SelfTag extends BaseModule {
   private static SelfTag instance;

   public SelfTag() {
      super();
      instance = this;
   }

   public static boolean isActive() {
      return instance != null && instance.isEnabled();
   }

   @Generated
   public static SelfTag getInstance() {
      return instance;
   }
}
