package redrosr.jcaddons;

import redrosr.jcaddons.Config.Config;
import redrosr.jcaddons.features.Pots.PotActionBar;
import redrosr.jcaddons.features.Pots.PotEsp;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class JCAddons implements ModInitializer {
	public static final String MOD_ID = "jcaddons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient minecraftClient;

	public static PotEsp potEsp;
	public static PotActionBar potActionBar;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing JCAddons");
		minecraftClient = MinecraftClient.getInstance();

		Config.HANDLER.load();

		potEsp = new PotEsp(minecraftClient);
		potActionBar = new PotActionBar(minecraftClient);

		registerCommands();
	}

	public static void onTick() {
		if (minecraftClient.player == null || minecraftClient.world == null) return;

		potEsp.onUpdate();
		potActionBar.onUpdate();
	}

	public static void onRender(MatrixStack matrixStack, float renderTickCounter) {
		potEsp.onRender(matrixStack, renderTickCounter);
	}

	public static void registerCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("jcaddons").executes(context -> {
				MinecraftClient.getInstance().send(() -> {
					minecraftClient.setScreen(Config.createScreen(context.getSource().getClient().currentScreen));
				});
				return 1;
			}));
		});
	}
}