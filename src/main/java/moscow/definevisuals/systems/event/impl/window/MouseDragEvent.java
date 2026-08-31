package moscow.definevisuals.systems.event.impl.window;

import lombok.Generated;
import moscow.definevisuals.systems.event.EventCancellable;

public class MouseDragEvent extends EventCancellable {
   private int delay;

   @Generated
   public int getDelay() {
      return this.delay;
   }

   @Generated
   public void setDelay(int delay) {
      this.delay = delay;
   }

   @Generated
   public MouseDragEvent(int delay) {
      this.delay = delay;
   }
}
