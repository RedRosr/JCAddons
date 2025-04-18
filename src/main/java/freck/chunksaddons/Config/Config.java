package freck.chunksaddons.Config;

import com.google.common.collect.Lists;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.Label;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.ValueFormatters;
import freck.chunksaddons.ChunksAddons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;


public class Config {
    public static final ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
        .id(Identifier.of(ChunksAddons.MOD_ID, "config")) // unique ID for your config
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve(ChunksAddons.MOD_ID + ".json5"))
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

    public static Screen createScreen(Screen parent) {
        return Config.HANDLER.generateGui().generateScreen(parent);
    }

    public static Config get() {
        return HANDLER.instance();
    }

}