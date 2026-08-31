package moscow.definevisuals.systems.commands.commands;

import java.util.List;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.systems.commands.Command;
import moscow.definevisuals.systems.commands.CommandBuilder;
import moscow.definevisuals.systems.commands.ParameterBuilder;
import moscow.definevisuals.systems.commands.ParameterValidator;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.systems.modules.Module;
import moscow.definevisuals.utility.game.MessageUtility;
import net.minecraft.text.Text;

public class ToggleCommand {
      public Command command() {
            List<String> moduleNames = DefineVisuals.getInstance().getModuleManager().getModules().stream()
                        .map(module -> module.getName().replace(" ", "")).toList();
            return CommandBuilder.begin("toggle")
                        .aliases("t")
                        .desc("commands.toggle.description")
                        .param("module",
                                    p -> p.validator(
                                                (moscow.definevisuals.systems.commands.ParameterValidator) ParameterBuilder.MODULE)
                                                .suggests(moduleNames))
                        .handler(context -> {
                              Module module = (Module) context.arguments().getFirst();
                              module.toggle();
                              MessageUtility.info(Text.of(Localizator
                                          .translate("commands.toggle." + (module.isEnabled() ? "enabled" : "disabled"),
                                                      module.getName())));
                        })
                        .build();
      }
}
