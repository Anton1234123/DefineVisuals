package pulse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.Identifier;

public final class CapeTextureCache {
   private static final CapeTextureCache INSTANCE = new CapeTextureCache();

   private final Map<Integer, Identifier> texturesByUserId = new ConcurrentHashMap<>();

   private CapeTextureCache() {
   }

   public static CapeTextureCache getInstance() {
      return INSTANCE;
   }

   public void putCapeTexture(int userId, Identifier texture) {
      if (texture == null) {
         texturesByUserId.remove(userId);
      } else {
         texturesByUserId.put(userId, texture);
      }
   }

   public Identifier getCapeTexture(int userId) {
      return texturesByUserId.get(userId);
   }

   public void clear() {
      texturesByUserId.clear();
   }
}
