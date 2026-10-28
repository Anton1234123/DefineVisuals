package ru.pulse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pulse.UserInfo;

/**
 * Заглушка для внешней части клиента Pulse.
 * В оригинале здесь инициализация всего клиента; косметике нужны
 * только логгер и данные своего аккаунта (ID плаща).
 */
public final class Pulse {
   private static final Logger LOGGER = LogManager.getLogger("pulse-cosmetics");
   private static UserInfo userInfo;

   private Pulse() {
   }

   public static Logger getLOGGER() {
      return LOGGER;
   }

   public static UserInfo getUserInfo() {
      return userInfo;
   }

   /** Вспомогательный сеттер для тестов (в оригинале заполняется из аккаунт-системы). */
   public static void setUserInfo(UserInfo info) {
      userInfo = info;
   }
}