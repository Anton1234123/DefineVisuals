package moscow.definevisuals.utility.animation.types;

import lombok.Generated;
import moscow.definevisuals.utility.animation.base.Easing;

public class TweenFloat {
   private float value;
   private float target;
   private long duration;
   private long startTime;
   private boolean done;
   private Easing easing;

   public TweenFloat(float initialValue) {
      this.value = initialValue;
      this.target = initialValue;
      this.done = true;
      this.easing = Easing.FIGMA_EASE_IN_OUT;
      this.duration = 200L;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public void snap(float value) {
      this.value = value;
      this.target = value;
      this.done = true;
   }

   public void animateTo(float target) {
      this.target = target;
      this.startTime = System.currentTimeMillis();
      this.done = false;
   }

   public float update() {
      if (this.done) {
         return this.value;
      }

      long elapsed = System.currentTimeMillis() - this.startTime;
      if (elapsed >= this.duration) {
         this.value = this.target;
         this.done = true;
         return this.value;
      }

      float progress = (float) elapsed / (float) this.duration;
      float easedProgress = this.easing.ease(progress, 0.0F, 1.0F, 1.0F);
      this.value = this.value + (this.target - this.value) * easedProgress;
      return this.value;
   }

   public float get() {
      return this.value;
   }

   public float getTarget() {
      return this.target;
   }

   public boolean isDone() {
      return this.done;
   }

   @Generated
   public void setEasing(Easing easing) {
      this.easing = easing;
   }
}
