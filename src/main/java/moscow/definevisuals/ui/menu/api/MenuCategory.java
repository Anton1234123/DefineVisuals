package moscow.definevisuals.ui.menu.api;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.utility.render.obj.CustomSprite;

public enum MenuCategory {
    PLAYER("Player", ModuleCategory.PLAYER, CustomSprite.PLAYER, CustomSprite.BIG_PLAYER),
    VISUALS("Visuals", ModuleCategory.VISUALS, CustomSprite.VISUALS, CustomSprite.BIG_VISUALS),
    OTHER("Other", ModuleCategory.OTHER, CustomSprite.OTHER, CustomSprite.BIG_OTHER);

    private final String name;
    private final ModuleCategory category;
    private final CustomSprite menuSprite;
    private final CustomSprite bigMenuSprite;

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
    private MenuCategory(String name, ModuleCategory category, CustomSprite menuSprite, CustomSprite bigMenuSprite) {
        this.name = name;
        this.category = category;
        this.menuSprite = menuSprite;
        this.bigMenuSprite = bigMenuSprite;
    }
}

