package moscow.definevisuals.systems.modules.modules.visuals;

import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;

@ModuleInfo(name = "XRay", category = ModuleCategory.VISUALS, enabledByDefault = false, desc = "modules.descriptions.xray")
public class XRay extends BaseModule {
    private int ancient;
    private int diamonds;
    private int gold;
    private int lapis;

    public int getAncient() {
        return this.ancient;
    }

    public void setAncient(int ancient) {
        this.ancient = ancient;
    }

    public int getDiamonds() {
        return this.diamonds;
    }

    public void setDiamonds(int diamonds) {
        this.diamonds = diamonds;
    }

    public int getGold() {
        return this.gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getLapis() {
        return this.lapis;
    }

    public void setLapis(int lapis) {
        this.lapis = lapis;
    }
}
