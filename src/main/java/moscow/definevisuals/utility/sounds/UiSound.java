package moscow.definevisuals.utility.sounds;

import java.util.concurrent.ThreadLocalRandom;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.modules.modules.other.Sounds;

/**
 * Semantic UI sound roles.
 *
 * <p>Every role maps to one existing sample, but uses its own volume and pitch.
 * This keeps the palette small while still giving different actions a distinct
 * sound: state changes, navigation, windows, success, delete, errors and binds
 * no longer share a single click.</p>
 */
public enum UiSound {
   CLICK(ClientSounds.UI_CLICK, 0.68F, 1.00F),
   NAVIGATION(ClientSounds.UI_NAV, 0.72F, 1.00F),
   WELCOME(ClientSounds.WELCOME, 1.00F, 1.00F),
   TYPE(ClientSounds.TYPING, 0.30F, 1.00F),
   PAYMENT(ClientSounds.APPLEPAY, 1.00F, 1.00F),
   MENU_OPEN(ClientSounds.CLICKGUI_OPEN, 1.00F, 1.00F),
   MENU_CLOSE(ClientSounds.CLICKGUI_OPEN, 0.85F, 0.90F),
   WINDOW_OPEN(ClientSounds.UI_NAV, 0.70F, 1.10F),
   WINDOW_CLOSE(ClientSounds.UI_NAV, 0.62F, 0.90F),
   SUCCESS(ClientSounds.APPLEPAY, 0.75F, 1.00F),
   DELETE(ClientSounds.UI_CLICK, 0.78F, 0.76F),
   ERROR(ClientSounds.CRITICAL, 0.80F, 1.00F),
   BIND(ClientSounds.UI_BIND, 0.70F, 1.00F),
   BIND_SET(ClientSounds.UI_BIND, 0.75F, 1.12F),
   TOGGLE_ON(ClientSounds.UI_CLICK, 0.65F, 1.10F),
   TOGGLE_OFF(ClientSounds.UI_CLICK, 0.60F, 0.90F),
   TICK(ClientSounds.UI_CLICK, 0.30F, 1.32F),
   MODULE_ON(ClientSounds.MODULE, 1.00F, 1.00F),
   MODULE_OFF(ClientSounds.MODULE, 0.90F, 0.90F);

   private static final long TICK_COOLDOWN_MS = 60L;
   private static long lastTickAt;

   private final ClientSoundInstance sound;
   private final float volume;
   private final float pitch;

   UiSound(ClientSoundInstance sound, float volume, float pitch) {
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
   }

   public void play() {
      this.playPitched(1.0F);
   }

   public void playPitched(float pitchMultiplier) {
      Sounds sounds;
      try {
         sounds = DefineVisuals.getInstance().getModuleManager().getModule(Sounds.class);
      } catch (RuntimeException ignored) {
         return;
      }
      if (sounds == null || !sounds.isEnabled()) {
         return;
      }
      if (this == TICK && !tickReady()) {
         return;
      }

      float finalVolume = this.volume * sounds.getVolume().getCurrentValue();
      float finalPitch = this.pitch * pitchMultiplier * pitchVariance();
      this.sound.play(finalVolume, finalPitch);
   }

   private static boolean tickReady() {
      long now = System.currentTimeMillis();
      if (now - lastTickAt < TICK_COOLDOWN_MS) {
         return false;
      }
      lastTickAt = now;
      return true;
   }

   private static float pitchVariance() {
      return 0.98F + ThreadLocalRandom.current().nextFloat() * 0.04F;
   }
}
