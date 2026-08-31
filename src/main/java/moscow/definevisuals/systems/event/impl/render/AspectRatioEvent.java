package moscow.definevisuals.systems.event.impl.render;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventCancellable;

public class AspectRatioEvent extends EventCancellable {
   private float aspectRatio;

   @Generated
   public float getAspectRatio() {
      return this.aspectRatio;
   }

   @Generated
   public void setAspectRatio(float aspectRatio) {
      this.aspectRatio = aspectRatio;
   }

   @Generated
   public AspectRatioEvent(float aspectRatio) {
      this.aspectRatio = aspectRatio;
   }
}
