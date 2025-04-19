package redrosr.jcaddons.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import redrosr.jcaddons.JCAddons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;


public class Config {
    public static final ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
        .id(Identifier.of(JCAddons.MOD_ID, "config")) // unique ID for your config
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve(JCAddons.MOD_ID + ".json5"))
            .setJson5(true) // json5 support, with GSON!
            .build())
        .build();

    @AutoGen(category = "General", group = "Pots")
    @Boolean(formatter = Boolean.Formatter.ON_OFF, colored = true)
    @SerialEntry public boolean PotESP = true;

    @AutoGen(category = "General", group = "Pots")
    @Boolean(formatter = Boolean.Formatter.ON_OFF, colored = true)
    @SerialEntry public boolean PotsWarningActionBar = true;

    @AutoGen(category = "General")
    @Boolean(formatter = Boolean.Formatter.ON_OFF, colored = true)
    @SerialEntry public boolean RemoveAds = true;

    @AutoGen(category = "Dev")
    @Boolean(formatter = Boolean.Formatter.ON_OFF, colored = true)
    @SerialEntry public boolean Logging = false;

    @AutoGen(category = "Dev")
    @Boolean(formatter = Boolean.Formatter.ON_OFF, colored = true)
    @SerialEntry public boolean testText = false;

    public static Screen createScreen(Screen parent) {
        return Config.HANDLER.generateGui().generateScreen(parent);
    }

    public static Config get() {
        return HANDLER.instance();
    }

}