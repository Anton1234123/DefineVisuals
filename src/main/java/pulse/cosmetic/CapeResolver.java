package pulse;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import ru.pulse.Pulse;

public final class CapeResolver {
   private CapeResolver() {
   }

   public static Identifier a(AbstractClientPlayerEntity player) {
      Integer capeId = resolveCapeId(player);
      return capeId == null ? null : CapeTextureCache.getInstance().getCapeTexture(capeId);
   }

   public static boolean b(AbstractClientPlayerEntity player) {
      if (player == null || a(player) == null) {
         return false;
      }

      if (player.isInvisible() || !player.isPartVisible(PlayerModelPart.CAPE)) {
         return false;
      }

      return player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA;
   }

   private static Integer resolveCapeId(AbstractClientPlayerEntity player) {
      if (player == null) {
         return null;
      }

      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null && client.player.getId() == player.getId()) {
         UserInfo userInfo = Pulse.getUserInfo();
         return userInfo == null ? null : userInfo.b();
      }

      String playerName = player.getGameProfile() == null ? null : player.getGameProfile().getName();
      return PlayerListCache.c().b(playerName);
   }
}
