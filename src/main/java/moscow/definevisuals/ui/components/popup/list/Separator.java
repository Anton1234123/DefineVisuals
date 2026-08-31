/*
 * Decompiled with CFR 0.152.
 */
package moscow.definevisuals.ui.components.popup.list;

import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.ui.components.popup.PopupComponent;
import moscow.definevisuals.utility.colors.Colors;

public class Separator
extends PopupComponent {
    @Override
    protected void renderComponent(UIContext context) {
        context.drawRect(this.x, this.y, this.width, this.height, Colors.getSeparatorColor());
    }

    @Override
    public float getHeight() {
        this.height = 4.0f;
        return 4.0f;
    }
}

