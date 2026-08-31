package moscow.definevisuals.mixin.minecraft.client.gui.screen;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.systems.event.impl.render.ChatRenderEvent;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.event.impl.window.ChatClickEvent;
import moscow.definevisuals.systems.event.impl.window.ChatReleaseEvent;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements IMinecraft {
   @Shadow
   protected TextFieldWidget chatField;
   @Shadow
   private ChatInputSuggestor chatInputSuggestor;

   protected ChatScreenMixin(Text title) {
      super(title);
   }

   @Inject(method = "init", at = @At("RETURN"))
   private void onInit(CallbackInfo ci) {
   }

   @Inject(method = "sendMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
   private void onSendMessage(String text, boolean addToHistory, CallbackInfo ci) {
      if (!DefineVisuals.getInstance().getModuleManager().getModule("NoCommands").isEnabled() && DefineVisuals.getInstance().getCommandManager().dispatch(text)) {
         mc.inGameHud.getChatHud().addToMessageHistory(text);
         ci.cancel();
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   public void renderHead(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
   }

   @Inject(method = "render", at = @At("RETURN"))
   public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      CustomDrawContext customCtx = CustomDrawContext.of(context);
      // Тригерим HudRenderEvent из чата, иначе HUD-элементы не обновляют позицию при перетаскивании
      // (renderMainHud не вызывается когда открыт экран)
      DefineVisuals.getInstance().getEventManager().triggerEvent(new HudRenderEvent(customCtx, delta));
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ChatRenderEvent(customCtx, delta));
   }

   @Inject(method = "mouseClicked", at = @At("HEAD"))
   private void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ChatClickEvent((float)mouseX, (float)mouseY, button));
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      DefineVisuals.getInstance().getEventManager().triggerEvent(new ChatReleaseEvent((float)mouseX, (float)mouseY, button));
      return super.mouseReleased(mouseX, mouseY, button);
   }
}
