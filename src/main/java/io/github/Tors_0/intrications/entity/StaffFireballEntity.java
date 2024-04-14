package io.github.Tors_0.intrications.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class StaffFireballEntity extends FireballEntity {
	public StaffFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower) {
		super(world, owner, velocityX, velocityY, velocityZ, explosionPower);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (entityHitResult.getEntity() instanceof GhastEntity) {
			this.setOwner(this);
		}
		super.onEntityHit(entityHitResult);
	}
}
