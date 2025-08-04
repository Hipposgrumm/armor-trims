package dev.hipposgrumm.armor_trims.config;

//? if fabric {
/*import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuAccess implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth_config")) return ConfigScreen::create;
        return screen -> null;
    }
}
*///?}