package moscow.definevisuals.systems.commands.commands;

import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.CommandContext;
import moscow.definevisuals.systems.commands.ValidationResult;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class VclipCommand implements IMinecraft {
   private Vec3d target;
   public Command command() {
      return CommandBuilder.begin("vclip", b -> b.aliases("v", "verticalclip").desc("commands.vclip.description").param("distance", p -> p.validator(text -> {
         try {
            return ValidationResult.ok(Double.parseDouble(text));
         } catch (NumberFormatException var2) {
            return ValidationResult.error(Localizator.translate("commands.vclip.invalid"));
         }
      })).handler(this::handle)).build();
   }
   private void handle(CommandContext ctx) {
      double distance = (Double)ctx.arguments().getFirst();
      MinecraftClient mc = MinecraftClient.getInstance();
      Vec3d pos = mc.player.getPos();
      mc.player.setPosition(pos.add(0.0, distance, 0.0));
   }
}
