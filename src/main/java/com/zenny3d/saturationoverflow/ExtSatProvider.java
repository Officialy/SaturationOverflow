package com.zenny3d.saturationoverflow;


import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExtSatProvider {
    public static final Capability<IExtSat> EXT_SAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final ResourceLocation ID = new ResourceLocation(SatExtMod.MODID, "ext_sat");


    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IExtSat.class);
    }

    public static LazyOptional<IExtSat> getMaxHealth(final LivingEntity entity) {
        return entity.getCapability(EXT_SAT_CAPABILITY, null);
    }

    public static ICapabilityProvider createProvider(final IExtSat saturation) {
        return new Provider<>(EXT_SAT_CAPABILITY, saturation);
    }

    public static class Provider<H> implements ICapabilityProvider, INBTSerializable<Tag> {
        private final INBTSerializable<Tag> instance;


        public Provider(final Capability<H> capability, final H instance) {
            this.instance = (INBTSerializable<Tag>) instance;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return capability.orEmpty(capability, (LazyOptional<T>) LazyOptional.of(() -> this.instance));
        }

        @Override
        public Tag serializeNBT() {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(Tag arg) {
            instance.deserializeNBT(arg);
        }
    }
}
