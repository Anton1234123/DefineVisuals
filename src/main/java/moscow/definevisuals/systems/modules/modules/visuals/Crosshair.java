package moscow.definevisuals.systems.modules.modules.visuals;

import lombok.Generated;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.util.hit.EntityHitResult;

@ModuleInfo(name = "Crosshair", category = ModuleCategory.VISUALS, desc = "modules.descriptions.crosshair")
public class Crosshair extends BaseModule implements IMinecraft {
   private final SliderSetting gap = new SliderSetting(this, "modules.settings.crosshair.gap")
      .min(0.0f)
      .max(6.0f)
      .step(0.5f)
      .currentValue(0.0f);
   private final SliderSetting length = new SliderSetting(this, "modules.settings.crosshair.length")
      .min(2.0f)
      .max(5.0f)
      .step(0.5f)
      .currentValue(2.5f);
   private final BooleanSetting adaptive = new BooleanSetting(this, "modules.settings.crosshair.adaptive")
      .enabled(false);
   private final BooleanSetting outline = new BooleanSetting(this, "modules.settings.crosshair.outline")
      .enabled(true);
   private final BooleanSetting centerDot = new BooleanSetting(this, "modules.settings.crosshair.centerDot")
      .enabled(false);

   private final EventListener<HudRenderEvent> onRender = event -> {
      if (mc.options.getPerspective().isFirstPerson() && !mc.options.hudHidden) {
         CustomDrawContext context = event.getContext();
         float centerX = mc.getWindow().getScaledWidth() / 2.0f;
         float centerY = mc.getWindow().getScaledHeight() / 2f;
         float cooldown = 1.0f - mc.player.getAttackCooldownProgress(event.getTickDelta());
         float actualGap = this.adaptive.isEnabled() 
            ? this.gap.getCurrentValue() + (8.0f * cooldown) 
            : this.gap.getCurrentValue();
         
         ColorRGBA color = mc.crosshairTarget instanceof EntityHitResult 
            ? Colors.getAccent() 
            : Colors.getTextColor();
         
         this.renderCrosshair(context, centerX, centerY, actualGap, color);
      }
   };

   private void renderCrosshair(CustomDrawContext context, float centerX, float centerY, float actualGap, ColorRGBA color) {
      float segmentLength = this.length.getCurrentValue();
      
      if (this.outline.isEnabled()) {
         ColorRGBA outlineColor = new ColorRGBA(0, 0, 0, 255);
         
         // Horizontal segments outline
         context.drawRect(centerX + actualGap - 0.5f, centerY - 1.0f, segmentLength + 1.0f, 2.0f, outlineColor);
         context.drawRect(centerX - actualGap - segmentLength - 0.5f, centerY - 1.0f, segmentLength + 1.0f, 2.0f, outlineColor);
         
         // Vertical segments outline
         context.drawRect(centerX - 1.0f, centerY - actualGap - segmentLength - 0.5f, 2.0f, segmentLength + 1.0f, outlineColor);
         context.drawRect(centerX - 1.0f, centerY + actualGap - 0.5f, 2.0f, segmentLength + 1.0f, outlineColor);
      }
      
      // Horizontal segments
      context.drawRect(centerX + actualGap, centerY - 0.5f, segmentLength, 1.0f, color);
      context.drawRect(centerX - actualGap - segmentLength, centerY - 0.5f, segmentLength, 1.0f, color);
      
      // Vertical segments
      context.drawRect(centerX - 0.5f, centerY - actualGap - segmentLength, 1.0f, segmentLength, color);
      context.drawRect(centerX - 0.5f, centerY + actualGap, 1.0f, segmentLength, color);
      
      // Center dot
      if (this.centerDot.isEnabled() && actualGap > 0.0f) {
         if (this.outline.isEnabled()) {
            context.drawRect(centerX - 0.5f, centerY - 0.5f, 2.0f, 2.0f, new ColorRGBA(0, 0, 0, 255));
         }
         context.drawRect(centerX, centerY, 1.0f, 1.0f, color);
      }
   }

   @Generated
   public SliderSetting getGap() {
      return this.gap;
   }

   @Generated
   public SliderSetting getLength() {
      return this.length;
   }

   @Generated
   public BooleanSetting getAdaptive() {
      return this.adaptive;
   }

   @Generated
   public BooleanSetting getOutline() {
      return this.outline;
   }

   @Generated
   public BooleanSetting getCenterDot() {
      return this.centerDot;
   }

   @Generated
   public EventListener<HudRenderEvent> getOnRender() {
      return this.onRender;
   }
}
