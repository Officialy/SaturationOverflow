package com.zenny3d.saturationoverflow;

import net.minecraft.nbt.FloatTag;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
public class ExtSat implements IExtSat, INBTSerializable<FloatTag> {

    private float bonusSaturation;
    private final LivingEntity entity;

    protected static final UUID ID = UUID.fromString("0321ef29-3d3e-4df5-809f-5fb79e9bb88e");

    public ExtSat(@Nullable final LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public FloatTag serializeNBT() {
        return FloatTag.valueOf(bonusSaturation);
    }

    @Override
    public void deserializeNBT(FloatTag nbt) {
        bonusSaturation = nbt.getAsFloat();
    }

    protected AttributeModifier createModifier() {
        return new AttributeModifier(ID, "Bonus Saturation", getExtSat(), AttributeModifier.Operation.ADDITION);
    }

    @Override
    public float getExtSat() {
        return bonusSaturation;
    }

    @Override
    public void setExtSat(float i) {
        this.bonusSaturation = i;
    }
}
