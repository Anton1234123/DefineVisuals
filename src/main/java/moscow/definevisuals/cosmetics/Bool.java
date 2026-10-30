package moscow.definevisuals.cosmetics;

/** Утилита приведения значений к boolean. */
public final class Bool {
   private Bool() {
   }

   public static boolean from(boolean value) {
      return value;
   }

   public static boolean from(Boolean value) {
      return value != null && value;
   }

   public static boolean from(byte value) {
      return value != 0;
   }

   public static boolean from(short value) {
      return value != 0;
   }

   public static boolean from(int value) {
      return value != 0;
   }

   public static boolean from(long value) {
      return value != 0L;
   }

   public static boolean from(float value) {
      return value != 0.0F;
   }

   public static boolean from(double value) {
      return value != 0.0;
   }

   public static boolean from(Object value) {
      if (value instanceof Boolean booleanValue) {
         return booleanValue;
      }
      if (value instanceof Number numberValue) {
         return numberValue.doubleValue() != 0.0;
      }
      return value != null;
   }
}
