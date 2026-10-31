package moscow.definevisuals.systems.modules.modules.player;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.window.KeyPressEvent;
import moscow.definevisuals.systems.event.impl.window.MouseEvent;
import moscow.definevisuals.systems.friends.FriendManager;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BindSetting;
import net.minecraft.entity.player.PlayerEntity;
@ModuleInfo(name = "Click Friend", category = ModuleCategory.PLAYER, desc = "Добавляет друзей при нажатии на клавишу")
public class ClickFriend extends BaseModule {
   private final BindSetting clickFriendKey = new BindSetting(this, "Клавиша");
   private final EventListener<KeyPressEvent> onKeyPressEvent = event -> this.handleKey(event.getKey(), event.getAction());
   private final EventListener<MouseEvent> onMouseEvent = event -> this.handleKey(event.getButton(), event.getAction());

   private void handleKey(int key, int action) {
      if (mc.currentScreen == null && action == 1 && this.clickFriendKey.isKey(key) && mc.targetedEntity instanceof PlayerEntity) {
         String nick = mc.targetedEntity.getName().getString();
         FriendManager friend = DefineVisuals.getInstance().getFriendManager();
         if (friend.isFriend(nick)) {
            friend.remove(nick);
         } else {
            friend.add(nick);
         }
      }
   }
}
