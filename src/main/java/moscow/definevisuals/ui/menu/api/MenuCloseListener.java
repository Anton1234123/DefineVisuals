/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package moscow.definevisuals.ui.menu.api;

import moscow.definevisuals.DefineVisuals;
import moscow.definevisuals.framework.base.UIContext;
import moscow.definevisuals.systems.event.EventListener;
import moscow.definevisuals.systems.event.impl.render.HudRenderEvent;
import moscow.definevisuals.systems.modules.modules.visuals.MenuModule;
import moscow.definevisuals.ui.menu.MenuScreen;
import moscow.definevisuals.ui.menu.dropdown.DropDownScreen;
import moscow.definevisuals.ui.menu.modern.ModernScreen;
import moscow.definevisuals.utility.interfaces.IMinecraft;
import net.minecraft.client.MinecraftClient;

public class MenuCloseListener
implements IMinecraft {
    private final EventListener<HudRenderEvent> onHudRender = event -> {
        MenuScreen menuScreen = DefineVisuals.getInstance().getMenuScreen();
        if (menuScreen == null) {
            return;
        }
        menuScreen.getMenuAnimation().update(menuScreen.isClosing() ? 0.0f : 1.0f);
        if (!(MenuCloseListener.mc.currentScreen instanceof MenuScreen) && DefineVisuals.getInstance().getModuleManager().getModule(MenuModule.class).isEnabled()) {
            DefineVisuals.getInstance().getModuleManager().getModule(MenuModule.class).setEnabled(false);
        }
        if (menuScreen.getMenuAnimation().getValue() > 0.1f && !(MenuCloseListener.mc.currentScreen instanceof MenuScreen) && menuScreen.isClosing()) {
            UIContext context = UIContext.of(event.getContext(), -1, -1, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));
            menuScreen.render(context);
        }
    };

    public MenuCloseListener() {
        DefineVisuals.getInstance().getEventManager().subscribe(this);
    }
}

