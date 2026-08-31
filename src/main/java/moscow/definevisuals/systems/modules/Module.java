package moscow.definevisuals.systems.modules;

import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.setting.SettingsContainer;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.interfaces.IScaledResolution;
import moscow.definevisuals.utility.interfaces.Toggleable;

public interface Module extends Toggleable, IMinecraft, IScaledResolution, SettingsContainer {
   void disable();

   void enable();

   void tick();

   ModuleInfo getInfo();

   String getName();

   default String getDescription() {
      String translationKey = "modules.descriptions.%s".formatted(this.getName().toLowerCase().replace(" ", "_"));
      return Localizator.translate(translationKey);
   }

   int getKey();

   ModuleCategory getCategory();

   boolean isEnabled();

   boolean isHidden();

   Animation getKeybindsAnimation();

   void setKey(int var1);

   void setEnabled(boolean var1, boolean var2);
}
