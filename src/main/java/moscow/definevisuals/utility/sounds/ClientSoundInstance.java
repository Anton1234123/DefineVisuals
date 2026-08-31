package moscow.definevisuals.utility.sounds;

import lombok.Generated;
import moscow.definevisuals.DefineVisuals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstance.AttenuationType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class ClientSoundInstance extends PositionedSoundInstance {
   private static final float DEFAULT_PITCH = 1.0F;
   private final String fileName;
   private SoundInstance currentInstance;

   public ClientSoundInstance(String fileName, float volume) {
      super(
         Identifier.of(DefineVisuals.MOD_ID + ":" + fileName),
         SoundCategory.MASTER,
         volume,
         1.0F,
         SoundInstance.createRandom(),
         false,
         0,
         AttenuationType.NONE,
         0.0,
         0.0,
         0.0,
         true
      );
      this.fileName = fileName;
   }

   public ClientSoundInstance(String fileName, float volume, float pitch) {
      super(
         Identifier.of(DefineVisuals.MOD_ID + ":" + fileName),
         SoundCategory.MASTER,
         volume,
         pitch,
         SoundInstance.createRandom(),
         false,
         0,
         AttenuationType.NONE,
         0.0,
         0.0,
         0.0,
         true
      );
      this.fileName = fileName;
   }

   public void play(float volume) {
      this.currentInstance = new ClientSoundInstance(this.fileName, volume);
      MinecraftClient.getInstance().getSoundManager().play(this.currentInstance);
   }

   public void play(float volume, float pitch) {
      this.currentInstance = new ClientSoundInstance(this.fileName, volume, pitch);
      MinecraftClient.getInstance().getSoundManager().play(this.currentInstance);
   }

   public void stop() {
      if (this.currentInstance != null) {
         MinecraftClient.getInstance().getSoundManager().stop(this.currentInstance);
         this.currentInstance = null;
      }
   }

   @Generated
   public String getFileName() {
      return this.fileName;
   }
}
