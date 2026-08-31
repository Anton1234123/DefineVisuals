package moscow.definevisuals.systems.modules.modules.player;

import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.client.gui.screen.DeathScreen;

@ModuleInfo(name = "Auto Respawn", category = ModuleCategory.PLAYER, desc = "Автоматически возрождает при смерти")
public class AutoRespawn extends BaseModule {
   @Override
   public void tick() {
      if (this.isEnabled() && mc.currentScreen instanceof DeathScreen && mc.player != null) {
         mc.player.requestRespawn();
         mc.setScreen(null);
      }
      super.tick();
   }
}
