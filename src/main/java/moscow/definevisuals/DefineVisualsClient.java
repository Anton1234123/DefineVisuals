package moscow.definevisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public final class DefineVisualsClient implements ClientModInitializer {
   @Override
   public void onInitializeClient() {
      DefineVisuals.INSTANCE.initialize();
      ClientLifecycleEvents.CLIENT_STOPPING.register(client -> DefineVisuals.INSTANCE.shutdown());
   }
}
