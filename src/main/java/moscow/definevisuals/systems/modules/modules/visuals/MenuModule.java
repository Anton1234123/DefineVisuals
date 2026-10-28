package moscow.definevisuals.systems.modules.modules.visuals;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.ui.menu.api.MenuCloseListener;
import moscow.definevisuals.utility.sounds.ClientSounds;

@ModuleInfo(name = "Menu", category = ModuleCategory.VISUALS, key = 344, desc = "modules.descriptions.menu")
public class MenuModule extends BaseModule {
   private static final MenuCloseListener menuCloseListener = new MenuCloseListener();
   private final ModeSetting mode = new ModeSetting(this, "Режим");
   private final ModeSetting.Value dropdown = new ModeSetting.Value(this.mode, "DropDown").select();
   private final ModeSetting.Value modern = new ModeSetting.Value(this.mode, "Modern");

   public ModeSetting getMode() {
      return this.mode;
   }

   public ModeSetting.Value getDropdown() {
      return this.dropdown;
   }

   public ModeSetting.Value getModern() {
      return this.modern;
   }

   public MenuCloseListener getMenuCloseListener() {
      return menuCloseListener;
   }

   public MenuModule() {
   }

   @Override
   public void onEnable() {
      mc.setScreen(DefineVisuals.getInstance().getMenuScreen());
      
      Sounds soundsModule = DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class);
      if (soundsModule.isEnabled()) {
         ClientSounds.CLICKGUI_OPEN.play(soundsModule.getVolume().getCurrentValue());
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }
}
