/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 */
package moscow.definevisuals.ui.components.popup.list;

import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.ui.components.popup.PopupComponent;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.gui.GuiUtility;

public class Text
extends PopupComponent {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    protected void renderComponent(UIContext context) {
        Font nameFont = Fonts.REGULAR.getFont(8.0f);
        float nameLeftPadding = 8.0f;
        float nameHeight = nameFont.height();
        context.drawFadeoutText(nameFont, this.text, this.x + nameLeftPadding, this.y + GuiUtility.getMiddleOfBox(nameHeight, this.height), Colors.getTextColor().withAlpha(RenderSystem.getShaderColor()[3] * 255.0f * 0.75f), 0.8f, 1.0f, this.width - 12.0f);
    }

    @Override
    public float getHeight() {
        this.height = 18.0f;
        return 18.0f;
    }
}

