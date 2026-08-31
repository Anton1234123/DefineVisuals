package moscow.definevisuals.utility.animation;

public class Easings {
   public static float linear(float t) {
      return t;
   }

   public static float easeInQuad(float t) {
      return t * t;
   }

   public static float easeOutQuad(float t) {
      return t * (2 - t);
   }

   public static float easeInOutQuad(float t) {
      return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
   }

   public static float easeInCubic(float t) {
      return t * t * t;
   }

   public static float easeOutCubic(float t) {
      return (--t) * t * t + 1;
   }

   public static float easeInOutCubic(float t) {
      return t < 0.5f ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
   }

   public static float easeOutBack(float t) {
      float c1 = 1.70158f;
      float c3 = c1 + 1;
      return 1 + c3 * (float)Math.pow(t - 1, 3) + c1 * (float)Math.pow(t - 1, 2);
   }

   public static float easeOutElastic(float t) {
      if (t == 0) return 0;
      if (t == 1) return 1;
      double c4 = (2 * Math.PI) / 3;
      return (float)(Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1);
   }
}
