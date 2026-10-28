package pulse;

/**
 * Заглушка данных аккаунта. Косметике нужен только ID плаща (b()).
 * В оригинале класс куда больше (токены, профиль, платёжки и т.п.).
 */
public class UserInfo {
   private final Integer capeId;

   public UserInfo(Integer capeId) {
      this.capeId = capeId;
   }

   /** ID плаща своего игрока (Nullable). */
   public Integer b() {
      return capeId;
   }
}