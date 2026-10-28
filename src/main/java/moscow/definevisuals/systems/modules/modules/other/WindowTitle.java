package moscow.definevisuals.systems.modules.modules.other;

import lombok.Generated;
import moscow.definevisuals.systems.modules.api.ModuleCategory;
import moscow.definevisuals.systems.modules.api.ModuleInfo;
import moscow.definevisuals.systems.modules.impl.BaseModule;
import moscow.definevisuals.systems.setting.settings.StringSetting;

@ModuleInfo(name = "Window Title", category = ModuleCategory.OTHER, desc = "Меняет название окна Minecraft")
public class WindowTitle extends BaseModule {
    private final StringSetting customTitle = new StringSetting(this, "modules.settings.window_title.custom_title").text("Define Visuals");

    @Override
    public void onEnable() {
        updateTitle();
    }

    @Override
    public void onDisable() {
        resetTitle();
    }

    private void updateTitle() {
        if (mc.getWindow() != null) {
            mc.getWindow().setTitle(customTitle.getText());
        }
    }

    private void resetTitle() {
        if (mc.getWindow() != null) {
            mc.getWindow().setTitle("Minecraft");
        }
    }

    @Generated
    public StringSetting getCustomTitle() {
        return this.customTitle;
    }
}
