package moscow.definevisuals.ui.menu.api;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.utility.render.obj.CustomSprite;

public enum MenuCategory {
    MODULES("Modules", ModuleCategory.MODULES, CustomSprite.MODULES, CustomSprite.BIG_MODULES, null),
    PLAYER("Player", ModuleCategory.PLAYER, CustomSprite.PLAYER, CustomSprite.BIG_PLAYER, MODULES),
    VISUALS("Visuals", ModuleCategory.VISUALS, CustomSprite.VISUALS, CustomSprite.BIG_VISUALS, MODULES),
    UTILS("Utils", ModuleCategory.UTILS, CustomSprite.UTILS, CustomSprite.BIG_UTILS, MODULES),
    SERVER("Server", ModuleCategory.SERVER, CustomSprite.SERVER, CustomSprite.BIG_SERVER, null),
    EVENTS("Events", ModuleCategory.EVENTS, CustomSprite.EVENTS, CustomSprite.BIG_EVENTS, SERVER),
    CLIENT("Client", ModuleCategory.CLIENT, CustomSprite.CLIENT, CustomSprite.BIG_CLIENT, null),
    CONFIGS("Configs", ModuleCategory.CONFIGS, CustomSprite.CONFIGS, CustomSprite.BIG_CONFIGS, CLIENT),
    THEMES("Themes", ModuleCategory.THEMES, CustomSprite.THEMES, CustomSprite.BIG_THEMES, CLIENT),
    OTHER("Other", ModuleCategory.OTHER, CustomSprite.OTHER, CustomSprite.BIG_OTHER, CLIENT),
    INTERFACE("Interface", ModuleCategory.INTERFACE, CustomSprite.INTERFACE, CustomSprite.BIG_INTERFACE, CLIENT);

    private final String name;
    private final ModuleCategory category;
    private final CustomSprite menuSprite;
    private final CustomSprite bigMenuSprite;
    private final MenuCategory parent;

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public ModuleCategory getCategory() {
        return this.category;
    }

    @Generated
    public CustomSprite getMenuSprite() {
        return this.menuSprite;
    }

    @Generated
    public CustomSprite getBigMenuSprite() {
        return this.bigMenuSprite;
    }

    @Generated
    public MenuCategory getParent() {
        return this.parent;
    }

    @Generated
    public boolean isSubCategory() {
        return this.parent != null;
    }

    @Generated
    private MenuCategory(String name, ModuleCategory category, CustomSprite menuSprite, CustomSprite bigMenuSprite, MenuCategory parent) {
        this.name = name;
        this.category = category;
        this.menuSprite = menuSprite;
        this.bigMenuSprite = bigMenuSprite;
        this.parent = parent;
    }
}

