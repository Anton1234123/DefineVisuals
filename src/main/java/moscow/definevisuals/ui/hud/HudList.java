/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 */
package moscow.definevisuals.ui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.systems.localization.Localizator;
import moscow.definevisuals.ui.hud.HudElement;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.ScissorUtility;

public abstract class HudList
extends HudElement {
    public HudList(String name, String icon) {
        super(name, icon);
    }

    @Override
    public void render(UIContext context) {
        this.update(context);
        float anim = this.animation.getValue() * this.visible.getValue();
        if (anim == 0.0f) {
            return;
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)Math.min(1.0f, anim));
        float scale = 0.5f + anim * 0.5f - 0.05f * this.selecting.getValue();
        RenderUtility.scale(context.getMatrices(), this.x + this.width / 2.0f, this.y + this.height / 2.0f, scale);
        context.drawShadow(this.x - 5.0f, this.y - 5.0f, this.width + 10.0f, this.height + 10.0f, 15.0f, BorderRadius.all(6.0f), ColorRGBA.BLACK.withAlpha(63.75f * this.dragAnim.getValue()));
        ScissorUtility.push(context.getMatrices(), this.x, this.y, this.width, Math.max(20.0f, this.height));
        this.renderComponent(context);
        ScissorUtility.pop();
        RenderUtility.end(context.getMatrices());
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    @Override
    protected void renderComponent(UIContext context) {
        Font font = Fonts.REGULAR.getFont(7.0f);
        context.drawClientRect(this.x, this.y, this.width, Math.max(20.0f, this.height), this.animation.getValue(), this.dragAnim.getValue(), 7.0f);
        float iconSize = 8.0f;
        context.drawText(Fonts.MEDIUM.getFont(7.0f), Localizator.translate(this.name), this.x + 7.0f, this.y + GuiUtility.getMiddleOfBox(font.height(), 18.0f) + 0.5f, Colors.getTextColor());
        context.drawTexture(DefineVisuals.id(this.icon), this.x + this.width - iconSize - 7.0f, this.y + 6.0f, iconSize, iconSize, Colors.getTextColor());
        if (this.height >= 23.0f) {
            context.drawRect(this.x, this.y + 18.0f, this.width, 4.0f, Colors.getSeparatorColor());
        }
    }
}

