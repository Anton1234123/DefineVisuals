package moscow.definevisuals.utility.animation;

public class SmoothAnimation {
   private float value;
   private float target;
   private long startTime;
   private double duration;
   private float startValue;
   private boolean done = true;

   public SmoothAnimation() {
      this.value = 0;
      this.target = 0;
   }

   public void set(float value) {
      this.value = value;
      this.target = value;
      this.done = true;
   }

   public void run(double target, double duration, EasingFunction easing) {
      this.startValue = this.value;
      this.target = (float)target;
      this.duration = duration;
      this.startTime = System.currentTimeMillis();
      this.done = false;
   }

   public void run(double target, double duration, EasingFunction easing, boolean reset) {
      if (reset) {
         this.value = (float)target;
      }
      this.run(target, duration, easing);
   }

   public void update() {
      if (this.done) return;

      long elapsed = System.currentTimeMillis() - this.startTime;
      if (elapsed >= this.duration * 1000) {
         this.value = this.target;
         this.done = true;
         return;
      }

      float progress = (float)(elapsed / (this.duration * 1000));
      float eased = Easings.easeOutCubic(progress);
      this.value = this.startValue + (this.target - this.startValue) * eased;
   }

   public float get() {
      return this.value;
   }

   @FunctionalInterface
   public interface EasingFunction {
      float ease(float t);
   }
}
