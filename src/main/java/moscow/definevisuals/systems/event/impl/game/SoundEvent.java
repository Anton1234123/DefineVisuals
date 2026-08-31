package moscow.definevisuals.systems.event.impl.game;

import lombok.Generated;
import moscow.definevisuals.systems.event.Event;
import net.minecraft.client.sound.SoundInstance;

public class SoundEvent extends Event {
   public SoundInstance sound;

   public SoundEvent(SoundInstance sound) {
      this.sound = sound;
   }

   @Generated
   public SoundInstance getSound() {
      return this.sound;
   }
}
