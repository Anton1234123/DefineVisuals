package pulse;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Заглушка кэша плащей по нику игрока.
 * В оригинале запрашивает ID плащей через AccountServiceClient (сеть);
 * здесь — просто локальный кэш, заполняемый вручную.
 */
public final class PlayerListCache {
   private static final PlayerListCache INSTANCE = new PlayerListCache();

   private final Map<String, Integer> capeIdsByName = new ConcurrentHashMap<>();

   private PlayerListCache() {
   }

   public static PlayerListCache c() {
      return INSTANCE;
   }

   /** ID плаща по нику (Nullable). */
   public Integer b(String playerName) {
      if (playerName == null || playerName.isBlank()) {
         return null;
      }
      return capeIdsByName.get(playerName.toLowerCase(Locale.ROOT));
   }

   public void registerCape(String playerName, Integer capeId) {
      capeIdsByName.put(playerName.toLowerCase(Locale.ROOT), capeId);
   }
}