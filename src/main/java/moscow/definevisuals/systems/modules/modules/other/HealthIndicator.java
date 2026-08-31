package moscow.definevisuals.systems.modules.modules.other;

import lombok.Generated;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.game.AttackEvent;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

@ModuleInfo(name = "Health Indicator", category = ModuleCategory.OTHER, desc = "Показывает HP игрока после удара")
public class HealthIndicator extends BaseModule {
   private final float relativeX = 0.5F;
   private final float relativeY = 0.54F;
   private final float normalScale = 1.1F;
   private final float lowHpScale = 1.6F;
   private final float targetDelay = 8000.0F;

   private long lastAttack = 0L;
   private PlayerEntity targetPlayer = null;
   private final MinecraftClient client = MinecraftClient.getInstance();

   private final EventListener<HudRenderEvent> onHudRender = event -> {
      if (!this.isEnabled()) return;

      long timeSinceAttack = System.currentTimeMillis() - lastAttack;
      if (timeSinceAttack <= (long) targetDelay && targetPlayer != null) {
         renderHealth(event.getContext());
      }
   };

   private final EventListener<AttackEvent> onAttack = event -> {
      Entity entity = event.getEntity();
      if (entity instanceof PlayerEntity) {
         lastAttack = System.currentTimeMillis();
         targetPlayer = (PlayerEntity) entity;
      } else {
         lastAttack = 0L;
         targetPlayer = null;
      }
   };

   public HealthIndicator() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      targetPlayer = null;
      lastAttack = 0L;
   }

   private void renderHealth(CustomDrawContext context) {
      if (targetPlayer == null || !targetPlayer.isAlive()) return;

      int health = (int) targetPlayer.getHealth();
      Text text = Text.literal(String.valueOf(health)).styled(style -> style.withColor(getHealthColor(health)));

      int windowWidth = client.getWindow().getScaledWidth();
      int windowHeight = client.getWindow().getScaledHeight();
      int textWidth = client.textRenderer.getWidth(text) / 2;
      int textHeight = 9 / 2;

      int x = (int) (windowWidth * relativeX);
      int y = (int) (windowHeight * relativeY);

      float scale = health < 10 ? lowHpScale : normalScale;

      context.getMatrices().push();
      context.getMatrices().translate(x, y, 0.0F);
      context.getMatrices().scale(scale, scale, 1.0F);
      context.getMatrices().translate(-x, -y, 0.0F);
      context.drawText(client.textRenderer, text, x - textWidth, y - textHeight, -1, true);
      context.getMatrices().pop();
   }

   private int getHealthColor(float health) {
      if (health <= 5.0F) {
         return 0xFF5555; // Red
      } else if (health <= 10.0F) {
         return 0xFFAA00; // Orange
      } else if (health <= 15.0F) {
         return 0xFFFF55; // Yellow
      } else if (health <= 20.0F) {
         return 0x55FF55; // Green
      } else {
         return 0x55FFFF; // Cyan (for >20 HP)
      }
   }

}
