package moscow.definevisuals.systems.localization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import lombok.Generated;
import moscow.definevisuals.DefineVisuals;

public final class Localizator {
   private static final Language DEFAULT_LANG = Language.RU_RU;
   private static Language currentLanguage = DEFAULT_LANG;
   private static final Map<String, String> translations = new HashMap<>();

   public static void loadTranslations() {
      String langFile = "/assets/" + DefineVisuals.MOD_ID + "/lang/" + currentLanguage.getCode() + ".lang";

      try {
         InputStream inputStream = Localizator.class.getResourceAsStream(langFile);
         if (inputStream == null) {
            throw new RuntimeException("Language file not found: " + langFile);
         } else {
            translations.clear();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            int lineNumber = 0;

            String line;
            while ((line = reader.readLine()) != null) {
               lineNumber++;
               line = removeComments(line).trim();
               if (!line.isEmpty()) {
                  parseLine(line, lineNumber, langFile);
               }
            }

            reader.close();
            inputStream.close();
         }
      } catch (IOException var5) {
         throw new RuntimeException("Failed to load translations for language: " + currentLanguage.getCode(), var5);
      }
   }

   public static void setLanguage(@Nonnull Language lang) {
      currentLanguage = lang;
      loadTranslations();
   }

   public static String translate(String key) {
      return translations.getOrDefault(key, fallback(key, key));
   }

   public static String translate(String key, Object... args) {
      String format = translations.getOrDefault(key, fallback(key, key));
      return String.format(format, args);
   }

   public static String translateOrEmpty(String key) {
      return translations.getOrDefault(key, fallback(key, " "));
   }

   private static String fallback(String key, String defaultValue) {
      if (currentLanguage == Language.RU_RU) {
         return switch (key) {
            case "modules.descriptions.auto_farm" -> "Объединённый автофарм: яблоки, мечи, зелья, объединение зелий, продажа и культуры";
            case "modules.settings.auto_farm.mode" -> "Режим";
            case "modules.settings.auto_farm.modes.apple" -> "Фарм яблок";
            case "modules.settings.auto_farm.modes.sword" -> "Фарм мечей";
            case "modules.settings.auto_farm.modes.potion" -> "Варка зелий";
            case "modules.settings.auto_farm.modes.potion_combiner" -> "Объединение зелий";
            case "modules.settings.auto_farm.modes.auto_sell" -> "Авто-продажа";
            case "modules.settings.auto_farm.modes.crop" -> "Фарм культур";
            case "modules.settings.auto_farm.apple.bonemeal_delay" -> "Задержка костной муки";
            case "modules.settings.auto_farm.sword.price" -> "Цена продажи";
            case "modules.settings.auto_farm.sword.relist_cooldown" -> "КД перевыставления";
            case "modules.settings.auto_farm.sword.craft_all" -> "Крафтить всё сразу";
            case "modules.settings.crop_farm.crop" -> "Культура";
            case "modules.settings.crop_farm.crop.nether_wart" -> "Адский нарост";
            case "modules.settings.crop_farm.crop.wheat" -> "Пшеница";
            case "modules.settings.crop_farm.crop.carrots" -> "Морковь";
            case "modules.settings.crop_farm.crop.potatoes" -> "Картофель";
            case "modules.settings.crop_farm.crop.beetroots" -> "Свёкла";
            case "modules.settings.crop_farm.crop.sugar_cane" -> "Тростник";
            case "modules.settings.crop_farm.scan_radius" -> "Радиус сканирования";
            case "modules.settings.crop_farm.vertical_range" -> "Вертикальный диапазон";
            case "modules.settings.crop_farm.replant" -> "Засаживать обратно";
            case "modules.settings.crop_farm.use_hoe" -> "Собирать мотыгой";
            case "modules.settings.crop_farm.pickup" -> "Подбирать дроп";
            case "modules.settings.crop_farm.auto_deposit" -> "Складывать в сундук";
            case "modules.settings.crop_farm.target_esp" -> "Подсвечивать цель";
            case "modules.settings.crop_farm.action_delay" -> "Задержка действий";
            case "modules.settings.potion_combiner.potions" -> "Зелья";
            case "modules.settings.potion_combiner.potion.strength" -> "Зелье силы";
            case "modules.settings.potion_combiner.potion.speed" -> "Зелье скорости";
            case "modules.settings.potion_combiner.potion.strength_speed" -> "Сила 3 + Скорость 3";
            case "modules.settings.potion_combiner.auto_open" -> "Авто открытие наковальни";
            case "modules.settings.potion_combiner.auto_exp" -> "Авто бутылочки опыта";
            case "modules.settings.potion_combiner.refill_to" -> "Пополнять опыт до";
            case "modules.settings.auto_sell.quantity" -> "Количество";
            case "modules.settings.auto_sell.price" -> "Цена";
            case "modules.settings.auto_sell.relist_cooldown" -> "КД перевыставления";
            case "modules.settings.auto_sell.confirm" -> "Подтверждать продажу";
            default -> defaultValue;
         };
      }

      return defaultValue;
   }
   private static void parseLine(String line, int lineNumber, String fileName) {
      int equalIndex = line.indexOf(61);
      if (equalIndex == -1) {
         DefineVisuals.LOGGER.warn("Warning: Invalid line format at line {} in {}: {}", new Object[]{lineNumber, fileName, line});
      } else {
         String key = line.substring(0, equalIndex).trim();
         String value = line.substring(equalIndex + 1).trim();
         if (key.isEmpty()) {
            DefineVisuals.LOGGER.warn("Warning: Empty key at line {} in {}", lineNumber, fileName);
         } else {
            translations.put(key, value);
         }
      }
   }

   private static String removeComments(String line) {
      int commentIndex = line.indexOf("#");
      return commentIndex != -1 ? line.substring(0, commentIndex) : line;
   }

   @Generated
   private Localizator() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   @Generated
   public static Language getCurrentLanguage() {
      return currentLanguage;
   }
}
