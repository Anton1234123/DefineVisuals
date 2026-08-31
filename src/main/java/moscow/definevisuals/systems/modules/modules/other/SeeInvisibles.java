package moscow.definevisuals.systems.modules.modules.other;

import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;

@ModuleInfo(name = "See Invisibles", category = ModuleCategory.OTHER, desc = "Делает невидимых игроков видимыми")
public class SeeInvisibles extends BaseModule {
    
    public SeeInvisibles() {
    }
    
    public static boolean isActive() {
        SeeInvisibles module = moscow.definevisuals.DefineVisuals.getInstance()
            .getModuleManager()
            .getModule(SeeInvisibles.class);
        return module != null && module.isEnabled();
    }
}
