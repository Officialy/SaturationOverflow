package com.zenny3d.saturationoverflow;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SatExtMod.MODID)
@Mod.EventBusSubscriber(modid = SatExtMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SatExtMod {
	public static final String MODID = "saturationoverflow";
	public static final Logger LOGGER = LogManager.getLogger();
	public static boolean canGainPastSat = true;

	public SatExtMod() {
		IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		MOD_BUS.addListener(this::commonSetup);
		MOD_BUS.addListener(new ExtSatProvider()::registerCaps);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec, "SaturationOverflow.toml");
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		canGainPastSat = Config.CONFIG.canGainPastSaturation.get();
	}

	public static class Config {

		private static final ForgeConfigSpec commonSpec;
		public static final Config CONFIG;

		static {
			final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);

			commonSpec = specPair.getRight();
			CONFIG = specPair.getLeft();
		}

		public final ForgeConfigSpec.BooleanValue canGainPastSaturation;

		Config(final ForgeConfigSpec.Builder builder) {
			canGainPastSaturation = builder
					.comment("Can Gain Past Saturation")
					.define("canGainPastSaturation", true);
		}
	}
}
