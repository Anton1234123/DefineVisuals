package moscow.definevisuals.mixin.minecraft.client.gui.screen;

import java.util.Optional;
import java.util.function.Consumer;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.ui.components.gif.Gif;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import moscow.definevisuals.utility.interfaces.IScaledResolution;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin implements IScaledResolution, IMinecraft {
   @Unique
   private Gif daunGif;
   @Unique
   private Animation fadeOutAnimation;
   @Shadow
   private long reloadCompleteTime = -1L;
   @Final
   @Shadow
   private Consumer<Optional<Throwable>> exceptionHandler;
   @Shadow
   @Final
   private ResourceReload reload;
   @Shadow
   @Final
   private boolean reloading;
   @Shadow
   private long reloadStartTime;

   @Inject(method = "<init>", at = @At("RETURN"))
   public void init(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading, CallbackInfo ci) {
      this.daunGif = new Gif(DefineVisuals.id("gifs/loading.gif"), 100.0F, 100.0F, 100.0F, 100.0F);
      this.fadeOutAnimation = new Animation(3000L, 1.0F, Easing.CUBIC_IN_OUT);
   }

   @Inject(method = "render", at = @At("HEAD"), cancellable = true)
   private void replaceRendering(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      // Отключаем кастомный экран загрузки - используем ванильный
      if (DefineVisuals.getInstance().isPanic()) {
         return; // В панике тоже используем ванильный
      }
      
      // Не отменяем рендеринг - используем стандартный экран загрузки Minecraft
      // Убираем анимацию логотипа клиента
   }
}
