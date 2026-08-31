package moscow.definevisuals.mixin.minecraft.client.gui.screen;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.systems.event.impl.render.ScreenRenderEvent;
import moscow.definevisuals.systems.event.impl.window.ContainerClickEvent;
import moscow.definevisuals.systems.event.impl.window.ContainerReleaseEvent;
import moscow.definevisuals.systems.event.impl.window.MouseDragEvent;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements IMinecraft {

   @Inject(method = "render", at = @At("TAIL"))
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      CustomDrawContext customDrawContext = CustomDrawContext.of(context);
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ScreenRenderEvent(customDrawContext, delta));
   }

   @Inject(method = "close", at = @At("HEAD"), cancellable = true)
   private void onClose(CallbackInfo ci) {
   }

   @Inject(method = "mouseClicked", at = @At("HEAD"))
   private void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ContainerClickEvent((float)mouseX, (float)mouseY, button));
   }

   @Inject(method = "mouseReleased", at = @At("HEAD"))
   public void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ContainerReleaseEvent((float)mouseX, (float)mouseY, button));
   }

   @Inject(method = "mouseDragged", at = @At("HEAD"))
   private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
      MouseDragEvent event = new MouseDragEvent(0);
      DefineVisuals.getInstance().getEventManager().triggerEvent(event);
   }
}
