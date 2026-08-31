/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  lombok.Generated
 */
package moscow.definevisuals.ui.hud.impl.island;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Generated;
import moscow.definevisuals.framework.base.CustomDrawContext;
import moscow.definevisuals.systems.setting.settings.SelectSetting;
import moscow.definevisuals.ui.hud.impl.island.IslandSize;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.interfaces.IScaledResolution;

public abstract class IslandStatus
extends SelectSetting.Value
implements IScaledResolution {
    protected IslandSize size = new IslandSize(48.0f, 15.0f);
    protected final Animation animation = new Animation(500L, Easing.BAKEK_SIZE);

    public IslandStatus(SelectSetting parent, String name) {
        super(parent, "hud.dynamic_island.statuses." + name);
        this.select();
    }

    public void draw(CustomDrawContext context) {
    }

    public void drawWithAlpha(CustomDrawContext context) {
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.animation.getValue());
        this.draw(context);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public void click(float mouseX, float mouseY, int button) {
    }

    public abstract boolean canShow();

    public ColorRGBA getColor() {
        return Colors.getBackgroundColor();
    }

    @Generated
    public IslandSize getSize() {
        return this.size;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }
}

