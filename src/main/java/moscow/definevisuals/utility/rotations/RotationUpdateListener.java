package moscow.definevisuals.utility.rotations;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.player.ClientPlayerTickEvent;
import moscow.definevisuals.systems.event.impl.player.InputEvent;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;

public class RotationUpdateListener {
   private final EventListener<ClientPlayerTickEvent> onTick = event -> DefineVisuals.getInstance().getRotationHandler().update();
   private final EventListener<Render3DEvent> onRender = event -> DefineVisuals.getInstance().getRotationHandler().updateRender(event.getTickDelta());
   private final EventListener<InputEvent> onInputEvent = event -> {
      RotationHandler rotationHandler = DefineVisuals.INSTANCE.getRotationHandler();
      RotationTask currentTask = rotationHandler.getCurrentTask();
      if (!rotationHandler.isIdling() && currentTask != null && currentTask.getMoveCorrection() == MoveCorrection.SILENT) {
         event.setYaw(DefineVisuals.getInstance().getRotationHandler().getCurrentRotation().getYaw());
      }
   };

   public RotationUpdateListener() {
      DefineVisuals.getInstance().getEventManager().subscribe(this);
   }
}
