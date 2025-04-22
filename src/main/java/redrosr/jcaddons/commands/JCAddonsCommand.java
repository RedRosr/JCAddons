package redrosr.jcaddons.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import redrosr.jcaddons.config.Config;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static redrosr.jcaddons.JCAddons.minecraftClient;

public class JCAddonsCommand {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(JCAddonsCommand::register);
    }

    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess RegistryAccess) {
        dispatcher.register(literal("jcaddons").executes(context -> {
            MinecraftClient.getInstance().send(() -> {
                minecraftClient.setScreen(Config.createScreen(context.getSource().getClient().currentScreen));
            });
            return 1;
        }));
    }


}
