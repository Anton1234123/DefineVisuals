package moscow.definevisuals.mixin.minecraft.client;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.event.impl.game.SoundEvent;
import moscow.definevisuals.systems.modules.modules.visuals.Removals;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
   @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
   private void onPlaySound(SoundInstance sound, CallbackInfo ci) {
      Removals removals = DefineVisuals.getInstance().getModuleManager().getModule(Removals.class);
      if (removals.isEnabled()
         && removals.getBeacon().isSelected()
         && (
            sound.getId().equals(SoundEvents.BLOCK_BEACON_ACTIVATE.id())
               || sound.getId().equals(SoundEvents.BLOCK_BEACON_AMBIENT.id())
               || sound.getId().equals(SoundEvents.BLOCK_BEACON_POWER_SELECT.id())
               || sound.getId().equals(SoundEvents.BLOCK_BEACON_DEACTIVATE.id())
         )) {
         ci.cancel();
      }

      if (removals.isEnabled()
         && removals.getWeatherSound().isSelected()
         && (
            sound.getId().equals(SoundEvents.WEATHER_RAIN.id())
               || sound.getId().equals(SoundEvents.WEATHER_RAIN_ABOVE.id())
               || sound.getId().equals(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER.id())
         )) {
         ci.cancel();
      }

      if (removals.isEnabled()
         && removals.getPhantoms().isSelected()
         && (
            sound.getId().equals(SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM.id())
               || sound.getId().equals(SoundEvents.ENTITY_PHANTOM_AMBIENT.id())
               || sound.getId().equals(SoundEvents.ENTITY_PHANTOM_BITE.id())
               || sound.getId().equals(SoundEvents.ENTITY_PHANTOM_FLAP.id())
               || sound.getId().equals(SoundEvents.ENTITY_PHANTOM_DEATH.id())
               || sound.getId().equals(SoundEvents.ENTITY_PHANTOM_HURT.id())
               || sound.getId().equals(SoundEvents.ENTITY_PHANTOM_SWOOP.id())
         )) {
         ci.cancel();
      }

      DefineVisuals.getInstance().getEventManager().triggerEvent(new SoundEvent(sound));
   }
}
