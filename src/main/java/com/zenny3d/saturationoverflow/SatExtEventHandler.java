package com.zenny3d.saturationoverflow;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SatExtMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SatExtEventHandler {

	private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();

	@SubscribeEvent
	public static void addCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof LivingEntity) {
			final ExtSat sat = new ExtSat((LivingEntity) event.getObject());
			event.addCapability(ExtSatProvider.ID, ExtSatProvider.createProvider(sat));
		}
	}

	@SubscribeEvent
	public static void Start(LivingEntityUseItemEvent.Start evt) {
		if (evt.isCanceled() || !(evt.getEntity() instanceof Player)) return;
		Player player = (Player) evt.getEntity();
		if (!player.level().isClientSide) {
			if (lastSaturationLevels.containsKey(player.getUUID()))
				lastSaturationLevels.replace(player.getUUID(), player.getFoodData().getSaturationLevel());
			else
				lastSaturationLevels.put(player.getUUID(), player.getFoodData().getSaturationLevel());
		}
	}

	@SubscribeEvent
	public static void Stop(LivingEntityUseItemEvent.Stop evt) {
		if (evt.isCanceled() || !(evt.getEntity() instanceof Player)) return;

		Player player = (Player) evt.getEntity();
		if (!player.level().isClientSide) {
			lastSaturationLevels.remove(player.getUUID());
		}
	}

	@SubscribeEvent
	public static void Finish(LivingEntityUseItemEvent.Finish evt) {
		if (evt.isCanceled() || !evt.getItem().getItem().isEdible() || !(evt.getEntity() instanceof Player)) return;

		FoodProperties food = evt.getItem().getItem().getFoodProperties(evt.getItem(), evt.getEntity());
		Player player = (Player) evt.getEntity();
		if (!player.level().isClientSide) {
			player.getCapability(ExtSatProvider.EXT_SAT_CAPABILITY, null).ifPresent((sat) -> {
				if ((sat.getExtSat() <= 0.0001 || SatExtMod.canGainPastSat) && lastSaturationLevels.containsKey(player.getUUID())) {
					float foodSat = food.getNutrition() * food.getSaturationModifier();
					float addedSat = Math.min(20 - lastSaturationLevels.get(player.getUUID()), foodSat);
					if (sat.getExtSat() < 0) sat.setExtSat(0);
					if (SatExtMod.canGainPastSat)
						sat.setExtSat(sat.getExtSat() + (foodSat - addedSat));
					else
						sat.setExtSat(foodSat - addedSat);
				}
				lastSaturationLevels.remove(player.getUUID());
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent evt) {
		if (evt.isCanceled()) return;

		Player player = evt.player;
		if (!player.level().isClientSide) {
			FoodData pf = player.getFoodData();
			float curSat = pf.getSaturationLevel();
			float needed = 20.0f - curSat;
			if (needed > 0.0) {
				player.getCapability(ExtSatProvider.EXT_SAT_CAPABILITY, null).ifPresent((sat) -> {
					if (sat.getExtSat() > 0.0f) {
						float toAdd = Math.min(needed, sat.getExtSat());
						pf.setSaturation(curSat + toAdd);
						sat.setExtSat(sat.getExtSat() - toAdd);
					}
				});
			}
		}
	}
}
