package moscow.definevisuals.utility.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public enum Fonts {
    MONTSERRAT_REGULAR("montserrat-regular"),
    MONTSERRAT_MEDIUM("montserrat-medium"),
    MONTSERRAT_SEMIBOLD("montserrat-semibold"),
    MONTSERRAT_BOLD("montserrat-bold"),
    MONTSERRAT_EXTRABOLD("montserrat-extrabold"),
    MONTSERRAT_BLACK("montserrat-black"),
    MONTSERRAT_LIGHT("montserrat-light"),
    MONTSERRAT_EXTRALIGHT("montserrat-extralight"),
    MONTSERRAT_THIN("montserrat-thin"),
    SF("sf-regular"),
    SF_MEDIUM("sf-medium"),
    SF_BOLD("sf-bold"),
    I2("i2"),
    EVENT_ICONS("event-icons"),
    INV_ICONS("inv-icons"),
    SMALL_PIXEL("small-pixel"),
    MAINMENU("mainmenu");

    private final String id;

    private Fonts(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public float width(String string, float f) {
        return 0;
    }

    public float msdfWidth(String string, float f) {
        return 0;
    }

    public void draw(DrawContext drawContext, String string, float f, float f2, float f3, int n) {
    }

    public void draw(MatrixStack matrices, String string, float f, float f2, float f3, int n) {
    }

    public void draw(String string, float f, float f2, float f3, int n) {
        // TODO: Implement via Render2D
    }
}
