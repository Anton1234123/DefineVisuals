/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package moscow.definevisuals.ui.components.animated;

import lombok.Generated;
import moscow.definevisuals.framework.base.CustomComponent;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;

public class AnimatedText
extends CustomComponent {
    private final float offset;
    private final Font font;
    private String prev = "";
    private String number = "";
    private final Animation animation;
    private boolean centered;

    public AnimatedText(Font font, float offset, long speed, Easing easing) {
        this.font = font;
        this.offset = offset;
        this.animation = new Animation(speed, easing);
    }

    @Override
    public void renderComponent(UIContext context) {
        this.animation.update(1.0f);
        context.drawText(this.font, this.prev, this.x - (this.centered ? this.font.width(this.prev) / 2.0f : 0.0f), this.y + this.offset * this.animation.getValue(), ColorRGBA.WHITE.withAlpha(255.0f * (1.0f - this.animation.getValue())));
        context.drawText(this.font, this.number, this.x - (this.centered ? this.font.width(this.number) / 2.0f : 0.0f), this.y - this.offset + this.offset * this.animation.getValue(), ColorRGBA.WHITE.withAlpha(255.0f * this.animation.getValue()));
    }

    public AnimatedText centered() {
        this.centered = true;
        return this;
    }

    public void update(String updated) {
        if (this.number.equals(updated)) {
            return;
        }
        this.prev = this.number;
        this.number = updated;
        this.animation.setValue(0.0f);
    }

    @Generated
    public Font getFont() {
        return this.font;
    }
}

