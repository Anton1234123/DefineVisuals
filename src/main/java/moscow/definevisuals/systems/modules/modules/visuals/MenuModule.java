package moscow.definevisuals.systems.modules.modules.visuals;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.modules.modules.other.Sounds;
import moscow.definevisuals.ui.menu.api.MenuCloseListener;
import moscow.definevisuals.utility.sounds.ClientSounds;
// cracked by t.me/pasterendnew
@ModuleInfo(name = "Menu", category = ModuleCategory.VISUALS, key = 344, desc = "modules.descriptions.menu")
public class MenuModule extends BaseModule {
   private static final MenuCloseListener menuCloseListener = new MenuCloseListener();

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
