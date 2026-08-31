package moscow.definevisuals.systems.modules.listeners;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.modules.Module;

public class ModuleTickListener implements EventListener<ClientPlayerTickEvent> {
   public void onEvent(ClientPlayerTickEvent event) {
      for (Module module : DefineVisuals.getInstance().getModuleManager().getModules()) {
         if (module.isEnabled()) {
            module.tick();
         }
      }
   }
}
