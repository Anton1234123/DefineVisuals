/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 */
package moscow.definevisuals.ui.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.framework.msdf.Font;
import moscow.definevisuals.framework.msdf.Fonts;
import moscow.definevisuals.framework.objects.BorderRadius;
import moscow.definevisuals.framework.style.InterfaceStyleRenderer;
import moscow.definevisuals.framework.objects.MouseButton;
import moscow.definevisuals.systems.modules.modules.visuals.Interface;
import moscow.definevisuals.systems.setting.settings.BooleanSetting;
import moscow.definevisuals.systems.setting.settings.ModeSetting;
import moscow.definevisuals.systems.setting.settings.SliderSetting;
import moscow.definevisuals.systems.theme.Theme;
import moscow.definevisuals.ui.hud.HudElement;
import moscow.definevisuals.utility.animation.base.Animation;
import moscow.definevisuals.utility.animation.base.Easing;
import moscow.definevisuals.utility.colors.ColorRGBA;
import moscow.definevisuals.utility.colors.Colors;
import moscow.definevisuals.utility.game.EntityUtility;
import moscow.definevisuals.utility.game.TextUtility;
import moscow.definevisuals.utility.gui.GuiUtility;
import moscow.definevisuals.utility.interfaces.IScaledResolution;
import moscow.definevisuals.utility.render.RenderUtility;
import moscow.definevisuals.utility.render.ScissorUtility;
import moscow.definevisuals.utility.sounds.UiSound;
import moscow.definevisuals.utility.time.Timer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class TargetHud
extends HudElement {
    private final BooleanSetting rayTrace = new BooleanSetting(this, "hud.targethud.look").enabled(true);
    private final SliderSetting stayTime = new SliderSetting(this, "hud.targethud.staytime", "hud.targethud.staytime.description")
        .min(0.5f).max(5.0f).step(0.5f).currentValue(2.0f);
    private final ModeSetting armor = new ModeSetting(this, "hud.targethud.armor");
    private final ModeSetting.Value armorNone = new ModeSetting.Value(this.armor, "hud.targethud.armor.none");
    private final ModeSetting.Value armorNumber = new ModeSetting.Value(this.armor, "hud.targethud.armor.number").select();
    private final ModeSetting.Value armorIcon = new ModeSetting.Value(this.armor, "hud.targethud.armor.icon");
    private final Animation content = new Animation(300L, 0.0f, Easing.BAKEK_SIZE);
    private final Animation health = new Animation(300L, 0.0f, Easing.BAKEK);
    private final Animation golden = new Animation(300L, 0.0f, Easing.BAKEK);
    private final Animation number = new Animation(300L, 0.0f, Easing.FIGMA_EASE_IN_OUT);
    private final Animation itemsX = new Animation(300L, 0.0f, Easing.BAKEK);
    private final Animation copy = new Animation(300L, 0.0f, Easing.BAKEK);
    private final Animation success = new Animation(500L, 0.0f, Easing.BAKEK_SIZE);
    private final Animation eatingPulse = new Animation(150L, 0.0f, Easing.BAKEK);
    private final Animation pulseIntensity = new Animation(50L, 0.0f, Easing.SINE_IN_OUT);
    private final Animation[] items = new Animation[4];
    private LivingEntity target;
    private LivingEntity currentLookTarget;
    private long lastTargetTime;
    private final Timer copyTimer = new Timer();
    private boolean copied;

    public TargetHud() {
        super("hud.targethud", "icons/hud/target.png");
        this.showing = true;
        for (int i = 0; i < this.items.length; ++i) {
            this.items[i] = new Animation(300L, 0.0f, Easing.BAKEK);
        }
    }

    @Override
    public void update(UIContext context) {
        super.update(context);
        this.width = 103.0f;
        this.height = 31.0f;
        if (this.x == 0.0f && this.y == 0.0f && IScaledResolution.sr != null) {
            this.x = IScaledResolution.sr.getScaledWidth() / 2.0f + 10.0f;
            this.y = IScaledResolution.sr.getScaledHeight() / 2.0f + 10.0f;
        }
    }

    @Override
    protected void renderComponent(UIContext context) {
        float f;
        float f2;
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.target = target;
        }
        if (this.target == null) {
            return;
        }
        Font regular7 = Fonts.REGULAR.getFont(7.0f);
        Font semibold6 = Fonts.SEMIBOLD.getFont(6.0f);
        boolean hover = GuiUtility.isHovered(this.x + 30.0f, this.y + 3.0f + 6.0f * this.content.getValue(), 60.0, 6.0, context);
        if (!hover || this.copyTimer.finished(1000L)) {
            this.copied = false;
        }
        boolean isEating = this.target.isUsingItem() && this.target.getActiveItem().contains(DataComponentTypes.FOOD);
        this.eatingPulse.update(isEating);
        if (isEating) {
            float pulse = (float)Math.sin((double)System.currentTimeMillis() / 100.0) * 0.5f + 0.5f;
            this.pulseIntensity.setValue(pulse);
        }
        this.copy.update(hover);
        this.success.update(this.copied);
        this.content.update(this.animation.getValue() * this.visible.getValue() >= 0.5f);
        LivingEntity livingEntity = this.target;
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)livingEntity;
            f2 = EntityUtility.getHealth(player);
        } else {
            f2 = this.target.getHealth();
        }
        this.health.update(f2 / this.target.getMaxHealth());
        this.golden.update(this.target.getAbsorptionAmount() / 20.0f);
        LivingEntity livingEntity2 = this.target;
        if (livingEntity2 instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)livingEntity2;
            f = EntityUtility.getHealth(player);
        } else {
            f = this.target.getHealth();
        }
        float healthNum = f;
        this.number.update(healthNum);
        if (this.animation.getValue() == 0.0f) {
            return;
        }
        if (!this.armorNone.isSelected()) {
            float prev = RenderSystem.getShaderColor()[3];
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            context.drawItem(Items.DIAMOND_CHESTPLATE, -992.0f, 994.0f, 1.0f);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)prev);
            float animOff = 0.0f;
            int i = 0;
            ItemStack[] handItems = new ItemStack[]{this.target.getMainHandStack(), this.target.getOffHandStack()};
            for (ItemStack itemStack : this.target.getArmorItems()) {
                if (itemStack.isEmpty()) continue;
                animOff += (this.armorIcon.isSelected() ? 11.0f : 5.0f + semibold6.width(this.calculateDurabilityPercent(itemStack))) + 2.0f;
            }
            float itemSize = 11.0f;
            if (this.armorIcon.isSelected()) {
                for (ItemStack handItem : handItems) {
                    if (handItem.isEmpty()) continue;
                    animOff += 13.0f;
                }
            }
            this.itemsX.update(animOff - 2.0f);
            float xOffset = -this.itemsX.getValue() / 2.0f;
            for (ItemStack itemStack : this.target.getArmorItems()) {
                this.items[i].update(!itemStack.isEmpty());
                float anim = this.content.getValue() * this.items[i].getValue();
                String percent = this.calculateDurabilityPercent(itemStack);
                boolean isIcon = this.armorIcon.isSelected();
                float panelWidth = isIcon ? 11.0f : 5.0f + semibold6.width(percent);
                float panelHeight = isIcon ? 11.0f : 9.0f;
                InterfaceStyleRenderer.drawCard(
                    context,
                    this.x + this.width / 2.0f + xOffset,
                    this.y - 12.0f * this.content.getValue(),
                    panelWidth,
                    panelHeight,
                    2.0f,
                    BorderRadius.all(2.5f),
                    anim,
                    0.0f,
                    0.0f
                );
                if (isIcon) {
                    RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)anim);
                    context.drawItem(itemStack, this.x + this.width / 2.0f + xOffset + 1.0f, this.y - 11.0f * this.content.getValue(), 0.55f);
                    RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                } else {
                    context.drawCenteredText(semibold6, percent, this.x + this.width / 2.0f + xOffset + panelWidth / 2.0f, this.y - 12.0f * this.content.getValue() + 3.0f, Colors.ACCENT.withAlpha(255.0f * anim));
                }
                xOffset += (panelWidth + 2.0f) * this.content.getValue();
                ++i;
            }
            if (this.armorIcon.isSelected()) {
                for (ItemStack handItem : handItems) {
                    if (handItem.isEmpty()) continue;
                    InterfaceStyleRenderer.drawCard(
                        context,
                        this.x + this.width / 2.0f + xOffset,
                        this.y - 12.0f * this.content.getValue(),
                        itemSize,
                        itemSize,
                        2.0f,
                        BorderRadius.all(2.5f),
                        this.content.getValue(),
                        0.0f,
                        0.0f
                    );
                    RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.content.getValue());
                    context.drawItem(handItem, this.x + this.width / 2.0f + xOffset + 1.0f, this.y - 11.0f * this.content.getValue(), 0.55f);
                    RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    xOffset += (itemSize + 2.0f) * this.content.getValue();
                }
            }
        }
        context.drawShadow(this.x - 5.0f, this.y - 5.0f, this.width + 10.0f, this.height + 10.0f, 15.0f, BorderRadius.all(6.0f), ColorRGBA.BLACK.withAlpha(63.75f * this.dragAnim.getValue()));
        InterfaceStyleRenderer.drawSurface(
            context,
            this.x,
            this.y,
            this.width,
            this.height,
            7.0f,
            BorderRadius.all(6.0f),
            this.animation.getValue(),
            this.dragAnim.getValue()
        );
        float alpha = 255.0f * this.content.getValue();
        ScissorUtility.push(context.getMatrices(), this.x, this.y, this.width, this.height);
        LivingEntity livingEntity3 = this.target;
        if (livingEntity3 instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)livingEntity3;
            context.drawHead(player, this.x + 6.0f * this.content.getValue(), this.y + 6.0f, 19.0f, BorderRadius.all(3.0f), Colors.WHITE.withAlpha(alpha));
        } else {
            context.drawRoundedTexture(DefineVisuals.id(Interface.glassSelected() ? "icons/hud/whoglass.png" : (DefineVisuals.getInstance().getThemeManager().getCurrentTheme() == Theme.DARK ? "icons/hud/whodark.png" : "icons/hud/who.png")), this.x + 6.0f * this.content.getValue(), this.y + 6.0f, 19.0f, 19.0f, BorderRadius.all(3.0f), Colors.WHITE.withAlpha(alpha));
        }
        String numberText = healthNum == 1000.0f ? "?" : TextUtility.formatNumber(this.number.getValue()).replace(",", ".");
        context.drawFadeoutText(regular7, this.target.getName().getString(), this.x + 30.0f + 8.0f * this.copy.getValue(), this.y + 3.0f + 6.0f * this.content.getValue(), Colors.getTextColor().withAlpha(alpha), 0.7f, 1.0f, this.width - 40.0f - 8.0f * this.copy.getValue() - semibold6.width(numberText));
        RenderUtility.rotate(context.getMatrices(), this.x + 28.0f + 5.0f * this.copy.getValue(), this.y + 6.0f + 6.0f * this.content.getValue(), 90.0f * this.success.getValue());
        context.drawTexture(DefineVisuals.id("icons/hud/copy.png"), this.x + 25.0f + 5.0f * this.copy.getValue(), this.y + 3.0f + 6.0f * this.content.getValue(), 6.0f, 6.0f, Colors.getTextColor().withAlpha(alpha * this.copy.getValue() * (1.0f - this.success.getValue())));
        RenderUtility.end(context.getMatrices());
        RenderUtility.rotate(context.getMatrices(), this.x + 28.0f + 5.0f * this.copy.getValue(), this.y + 6.0f + 6.0f * this.content.getValue(), -90.0f + 90.0f * this.success.getValue());
        context.drawTexture(DefineVisuals.id("icons/check.png"), this.x + 25.0f + 5.0f * this.copy.getValue(), this.y + 3.0f + 6.0f * this.content.getValue(), 6.0f, 6.0f, Colors.GREEN.withAlpha(alpha * this.copy.getValue() * this.success.getValue()));
        RenderUtility.end(context.getMatrices());
        context.drawRightText(semibold6, numberText, this.x + this.width - 7.0f, this.y + 4.0f + 6.0f * this.content.getValue(), Colors.ACCENT.withAlpha(alpha));
        context.drawRoundedRect(this.x + 30.0f, this.y + this.height - 6.0f - 6.0f * this.content.getValue(), 65.0f, 3.0f, BorderRadius.all(0.7f), Colors.getAdditionalColor().withAlpha(alpha * (1.0f - 0.7f * Interface.glass())));
        context.drawRoundedRect(this.x + 30.0f, this.y + this.height - 6.0f - 6.0f * this.content.getValue(), 65.0f * Math.clamp(this.health.getValue(), 0.0f, 1.0f), 3.0f, BorderRadius.all(0.7f), Colors.ACCENT.withAlpha(alpha));
        context.drawRoundedRect(this.x + 95.0f - 65.0f * Math.clamp(this.golden.getValue(), 0.0f, 1.0f), this.y + this.height - 6.0f - 6.0f * this.content.getValue(), 65.0f * Math.clamp(this.golden.getValue(), 0.0f, 1.0f), 3.0f, BorderRadius.all(0.7f), new ColorRGBA(255.0f, 220.0f, 81.0f, alpha));
        ScissorUtility.pop();
    }

    private String calculateDurabilityPercent(ItemStack itemStack) {
        if (itemStack.isEmpty() || !itemStack.isDamageable()) {
            return "100%";
        }
        int maxDurability = itemStack.getMaxDamage();
        int currentDamage = itemStack.getDamage();
        if (currentDamage >= maxDurability) {
            return "0%";
        }
        double durabilityPercent = 100.0 - (double)currentDamage / (double)maxDurability * 100.0;
        return String.format("%.0f%%", durabilityPercent);
    }

    private LivingEntity findActiveTarget() {
        Entity target1 = DefineVisuals.getInstance().getTargetManager().getCurrentTarget();
        if (target1 instanceof LivingEntity living && living.isAlive()) {
            return living;
        }

        if (this.rayTrace.isEnabled() && mc.player != null && mc.world != null) {
            double reach = 3.0;
            Vec3d eyePos = mc.player.getEyePos();

            if (mc.targetedEntity instanceof LivingEntity living && living.isAlive() && living != mc.player) {
                if (mc.player.distanceTo(living) <= reach || (mc.crosshairTarget != null && mc.crosshairTarget.getPos().squaredDistanceTo(eyePos) <= reach * reach)) {
                    return living;
                }
            }

            Vec3d lookVec = mc.player.getRotationVec(1.0f);
            Vec3d reachVec = eyePos.add(lookVec.multiply(reach));
            Box searchBox = mc.player.getBoundingBox().stretch(lookVec.multiply(reach)).expand(1.0);

            EntityHitResult hitResult = ProjectileUtil.raycast(
                mc.player,
                eyePos,
                reachVec,
                searchBox,
                entity -> entity instanceof LivingEntity && entity.isAlive() && entity != mc.player && !entity.isSpectator(),
                reach * reach
            );

            if (hitResult != null && hitResult.getEntity() instanceof LivingEntity living) {
                if (mc.player.distanceTo(living) <= reach || hitResult.getPos().squaredDistanceTo(eyePos) <= reach * reach) {
                    return living;
                }
            }
        }

        return null;
    }

    private LivingEntity getTarget() {
        if (TargetHud.mc.currentScreen instanceof ChatScreen) {
            return TargetHud.mc.player;
        }

        LivingEntity active = this.findActiveTarget();
        long now = System.currentTimeMillis();

        if (active != null) {
            this.currentLookTarget = active;
            this.lastTargetTime = now;
            return active;
        }

        if (this.currentLookTarget != null) {
            long elapsed = now - this.lastTargetTime;
            long lingerMs = (long)(this.stayTime.getCurrentValue() * 1000.0f);
            if (this.currentLookTarget.isAlive() && !this.currentLookTarget.isRemoved() && elapsed < lingerMs) {
                return this.currentLookTarget;
            }
            this.currentLookTarget = null;
        }

        return null;
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (GuiUtility.isHovered((double)(this.x + 30.0f), (double)(this.y + 3.0f + 6.0f * this.content.getValue()), 60.0, 6.0, mouseX, mouseY)) {
            if (this.target != null) {
                TextUtility.copyText(this.target.getName().getString());
                this.copyTimer.reset();
                this.copied = true;
                UiSound.SUCCESS.play();
                return;
            }
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean show() {
        return this.getTarget() != null;
    }
}
