package moscow.definevisuals.mixin.minecraft.client.gui.screen;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.mixin.accessors.HandledScreenAccessor;
import moscow.definevisuals.systems.event.impl.render.ScreenRenderEvent;
import moscow.definevisuals.systems.event.impl.window.ContainerClickEvent;
import moscow.definevisuals.systems.event.impl.window.ContainerReleaseEvent;
import moscow.definevisuals.systems.event.impl.window.MouseDragEvent;
import moscow.definevisuals.systems.modules.modules.player.ItemScroller;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements IMinecraft {
   @Shadow @Final protected ScreenHandler handler;

   @Inject(method = "render", at = @At("TAIL"))
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      CustomDrawContext customDrawContext = CustomDrawContext.of(context);
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ScreenRenderEvent(customDrawContext, delta));
   }

   @Inject(method = "close", at = @At("HEAD"), cancellable = true)
   private void onClose(CallbackInfo ci) {
      this.defineVisuals$getItemScroller().resetDrag();
   }

   @Inject(method = "mouseClicked", at = @At("HEAD"))
   private void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ContainerClickEvent((float)mouseX, (float)mouseY, button));
      ItemScroller itemScroller = this.defineVisuals$getItemScroller();
      Slot slot = ((HandledScreenAccessor)this).invokeGetSlotAt(mouseX, mouseY);
      if (itemScroller.isEnabled()) {
         itemScroller.beginDrag(this.handler, slot, button);
      } else {
         itemScroller.resetDrag();
      }
   }

   @Inject(method = "mouseReleased", at = @At("HEAD"))
   public void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ContainerReleaseEvent((float)mouseX, (float)mouseY, button));
      this.defineVisuals$getItemScroller().resetDrag();
   }

   @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
   private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
      MouseDragEvent event = new MouseDragEvent(0);
      DefineVisuals.getInstance().getEventManager().triggerEvent(event);
      ItemScroller itemScroller = this.defineVisuals$getItemScroller();
      Slot slot = ((HandledScreenAccessor)this).invokeGetSlotAt(mouseX, mouseY);
      if (itemScroller.handleDrag(this.handler, slot, button)) {
         cir.setReturnValue(true);
      }
   }

   @Unique
   private ItemScroller defineVisuals$getItemScroller() {
      return DefineVisuals.getInstance().getModuleManager().getModule(ItemScroller.class);
   }
}
