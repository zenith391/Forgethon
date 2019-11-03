package io.zenith391.forgethon;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.python.core.PyCode;
import org.python.core.PyFrame;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import io.zenith391.forgethon.loader.ModLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Mod(Forgethon.MODID)
public class Forgethon {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "forgethon";
	private static List<PythonMod> mods;

	public Forgethon() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Scanning Python mods..");
		try {
			mods = ModLoader.search(new File("mods"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		for (PythonMod mod : mods) {
			LOGGER.info("Found python mod " + mod.getDisplayName() + " (" + mod.getId() + " " + mod.getVersion() + ")");
		}
		
		LOGGER.info("Constructing mods..");
		for (PythonMod mod : mods) {
			mod.construct();
		}
		
		for (PythonMod mod : mods) {
			PythonInterpreter intr = mod.getInterpreter();
			PyObject setup = intr.get("setup");
			if (setup != null && setup instanceof PyFunction) {
				PyFunction func = (PyFunction) setup;
				func._jcall(new Object[0]);
			}
		}
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		for (PythonMod mod : mods) {
			PythonInterpreter intr = mod.getInterpreter();
			PyObject f = intr.get("on_server_starting");
			if (f != null && f instanceof PyFunction) {
				PyFunction func = (PyFunction) f;
				func._jcall(new Object[0]);
			}
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {

		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			LOGGER.info("HELLO from Register Block");
		}
	}
}
