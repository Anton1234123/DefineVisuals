package moscow.definevisuals.systems.commands.commands.hubswap;

import java.util.ArrayList;

public final class AnReg {
   private static final RangeCfg rangesConfig = new RangeCfg();

   private AnReg() {
   }

   public static void applyRanges(RangeCfg config) {
      rangesConfig.setRange(new ArrayList<>(config.getRanges()), config.getTotal());
   }

   public static RangeCfg getRangeCfg() {
      return rangesConfig;
   }

   public static int getMinNumber() {
      return rangesConfig.getMin();
   }

   public static int getMaxNumber() {
      return rangesConfig.getMax();
   }

   public static boolean isValid(int number) {
      return number >= rangesConfig.getMin() && number <= rangesConfig.getMax();
   }

   public static String catOf(int number) {
      RangeCfg.RangeEntry entry = rangesConfig.find(number);
      if (entry != null) {
         return entry.key();
      } else {
         if (number >= 40) return "clans";
         return "solo";
      }
   }

   public static int catSlotOf(int number) {
      if (number >= 1 && number <= 12) return 0; // Solo
      if (number >= 13 && number <= 26) return 1; // Duo
      if (number >= 27 && number <= 39) return 2; // Trio
      return 3; // Clans (40-51+)
   }

   public static String keyOf(int number) {
      return number == 1 ? "lanarchy" : "lanarchy" + number;
   }

   public static String name(int number) {
      RangeCfg.RangeEntry entry = rangesConfig.find(number);
      String prefix = "";
      if (entry != null) {
         switch (entry.key()) {
            case "solo" -> prefix = "Соло";
            case "duo" -> prefix = "Дуо";
            case "trio" -> prefix = "Трио";
            case "clans" -> prefix = "Кланы";
            default -> prefix = "Анархия";
         }
      } else {
         if (number >= 40) prefix = "Кланы";
         else prefix = "Анархия";
      }

      return prefix + " #" + number;
   }
}
