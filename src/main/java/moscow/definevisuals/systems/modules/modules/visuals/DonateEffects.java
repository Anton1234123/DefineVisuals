package moscow.definevisuals.systems.modules.modules.visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.game.SoundEvent;
import moscow.definevisuals.systems.event.impl.game.WorldChangeEvent;
import moscow.definevisuals.systems.event.impl.network.ReceivePacketEvent;
import moscow.definevisuals.systems.event.impl.render.PreHudRenderEvent;
import moscow.definevisuals.systems.event.impl.render.Render3DEvent;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.SelectSetting;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.render.Draw3DUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.Utils;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(name = "Donate Effects", category = ModuleCategory.VISUALS, desc = "Визуальные эффекты для донатеров (сонар, зелья, анимации)")
public class DonateEffects extends BaseModule {
   private final BooleanSetting sonar = new BooleanSetting(this, "Сонар");
   private final BooleanSetting potions = new BooleanSetting(this, "Зелья");
   private final SelectSetting targets = new SelectSetting(this, "Эффекты");
   private final SelectSetting.Value dez = new SelectSetting.Value(this.targets, "Dez");
   private final SelectSetting.Value aura = new SelectSetting.Value(this.targets, "Aura");
   private final SelectSetting.Value pil = new SelectSetting.Value(this.targets, "Pil");
   private final SelectSetting.Value fire = new SelectSetting.Value(this.targets, "Огонь");
   private final SelectSetting.Value boom = new SelectSetting.Value(this.targets, "Boom");
   private final SelectSetting.Value trapka = new SelectSetting.Value(this.targets, "Trapka");
   private final SelectSetting.Value stun = new SelectSetting.Value(this.targets, "Stun");
   private final Map<Integer, TrackedPotion> trackedPotions = new HashMap<>();
   private final List<EffectMarker> markers = new CopyOnWriteArrayList<>();
   private final EventListener<ReceivePacketEvent> onPacket = event -> {
      if (this.sonar.isEnabled() && event.getPacket() instanceof PlaySoundS2CPacket packet) {
         this.markers.add(new EffectMarker(this.soundPosition(packet), Colors.getAccent(), System.currentTimeMillis() + 3000L));
      }
   };
   private final EventListener<SoundEvent> onSound = event -> {
      if (this.sonar.isEnabled() && event.getSound() != null) {
         var sound = event.getSound();
         this.markers.add(new EffectMarker(new Vec3d(sound.getX(), sound.getY(), sound.getZ()), Colors.getAccent(), System.currentTimeMillis() + 3000L));
      }
   };
   private final EventListener<WorldChangeEvent> onWorldChange = event -> {
      this.trackedPotions.clear();
      this.markers.clear();
   };
   private final EventListener<Render3DEvent> onRender3D = event -> {
      this.updatePotions();
      long now = System.currentTimeMillis();
      this.markers.removeIf(marker -> marker.expiresAt <= now);
      if (this.markers.isEmpty()) {
         return;
      }

      Vec3d camera = event.getCamera().getPos();
      RenderSystem.enableBlend();
      RenderSystem.disableDepthTest();
      RenderSystem.disableCull();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      for (EffectMarker marker : this.markers) {
         Box box = new Box(marker.pos, marker.pos).expand(0.35).offset(-camera.x, -camera.y, -camera.z);
         Draw3DUtility.renderBoxInternalDiagonals(event.getMatrices(), buffer, box, marker.color.withAlpha(180.0F));
      }

      RenderUtility.buildBuffer(buffer);
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   };
   private final EventListener<PreHudRenderEvent> onHud = event -> {
      long now = System.currentTimeMillis();
      for (EffectMarker marker : this.markers) {
         Vec2f screen = Utils.worldToScreen(marker.pos);
         if (screen != null) {
            event.getContext().drawText(mc.textRenderer, "!", (int)screen.x, (int)screen.y, marker.color.getRGB(), true);
         }
      }
   };

   private void updatePotions() {
      if (!this.potions.isEnabled() || mc.player == null || mc.world == null) {
         this.trackedPotions.clear();
         return;
      }

      Set<Integer> active = new HashSet<>();
      for (var entity : mc.world.getEntities()) {
         if (!(entity instanceof PotionEntity potion)) {
            continue;
         }

         int id = potion.getId();
         active.add(id);
         TrackedPotion tracked = this.trackedPotions.get(id);
         if (tracked == null) {
            this.trackedPotions.put(id, new TrackedPotion(potion.getPos(), potion.getStack().copy()));
         } else {
            tracked.pos = potion.getPos();
         }
      }

      Iterator<Map.Entry<Integer, TrackedPotion>> iterator = this.trackedPotions.entrySet().iterator();
      while (iterator.hasNext()) {
         Map.Entry<Integer, TrackedPotion> entry = iterator.next();
         if (active.contains(entry.getKey())) {
            continue;
         }

         TrackedPotion tracked = entry.getValue();
         ColorRGBA color = this.potionColor(tracked.stack);
         for (int i = 0; i < 12; i++) {
            double angle = Math.random() * Math.PI * 2.0;
            double radius = Math.sqrt(Math.random()) * 1.5;
            Vec3d pos = tracked.pos.add(Math.cos(angle) * radius, 0.0, Math.sin(angle) * radius);
            this.markers.add(new EffectMarker(pos, color, System.currentTimeMillis() + 2500L));
         }

         iterator.remove();
      }
   }

   private ColorRGBA potionColor(ItemStack stack) {
      String name = stack.getName().getString().toLowerCase();
      if (name.contains("radiation")) {
         return new ColorRGBA(99.0F, 255.0F, 0.0F);
      }

      if (name.contains("paladin") || name.contains("shield")) {
         return new ColorRGBA(74.0F, 180.0F, 255.0F);
      }

      if (name.contains("assassin") || name.contains("sword")) {
         return new ColorRGBA(255.0F, 68.0F, 68.0F);
      }

      if (name.contains("holy")) {
         return new ColorRGBA(255.0F, 255.0F, 255.0F);
      }

      if (name.contains("popper") || name.contains("bomb")) {
         return new ColorRGBA(255.0F, 170.0F, 0.0F);
      }

      if (name.contains("drowsiness") || name.contains("moon")) {
         return new ColorRGBA(136.0F, 85.0F, 255.0F);
      }

      if (name.contains("rage") || name.contains("angry")) {
         return new ColorRGBA(255.0F, 69.0F, 0.0F);
      }

      return Colors.getAccent();
   }

   private Vec3d soundPosition(PlaySoundS2CPacket packet) {
      return BlockPos.ofFloored(packet.getX(), packet.getY(), packet.getZ()).toCenterPos();
   }

   private static final class TrackedPotion {
      private Vec3d pos;
      private final ItemStack stack;

      private TrackedPotion(Vec3d pos, ItemStack stack) {
         this.pos = pos;
         this.stack = stack;
      }
   }

   private static final class EffectMarker {
      private final Vec3d pos;
      private final ColorRGBA color;
      private final long expiresAt;

      private EffectMarker(Vec3d pos, ColorRGBA color, long expiresAt) {
         this.pos = pos;
         this.color = color;
         this.expiresAt = expiresAt;
      }
   }
}
