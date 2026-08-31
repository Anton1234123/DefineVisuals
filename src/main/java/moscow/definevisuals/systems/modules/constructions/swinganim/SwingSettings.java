package moscow.definevisuals.systems.modules.constructions.swinganim;

import java.util.ArrayList;
import java.util.List;
import moscow.definevisuals.systems.setting.Setting;
import moscow.definevisuals.systems.setting.SettingsContainer;

public class SwingSettings implements SettingsContainer {
   protected final List<Setting> settings = new ArrayList<>();

   @Override
   public List<Setting> getSettings() {
      return this.settings;
   }
}
