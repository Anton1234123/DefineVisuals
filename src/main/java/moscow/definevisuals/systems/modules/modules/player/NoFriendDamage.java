package moscow.definevisuals.systems.modules.modules.player;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.game.InternalAttackEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.entity.player.PlayerEntity;

@ModuleInfo(name = "No Friend Damage", category = ModuleCategory.PLAYER, desc = "Не позволяет бить друзей")
public class NoFriendDamage extends BaseModule {
   private final EventListener<InternalAttackEvent> onAttackEvent = event -> {
      if (this.isEnabled()
         && event.getEntity() instanceof PlayerEntity
         && DefineVisuals.getInstance().getFriendManager().isFriend(event.getEntity().getName().getString())) {
         event.cancel();
      }
   };
}
