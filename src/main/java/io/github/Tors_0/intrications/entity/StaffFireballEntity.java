package io.github.Tors_0.intrications.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class StaffFireballEntity extends FireballEntity {

	public StaffFireballEntity(World world, LivingEntity owner, int explosionPower) {
		super(world, owner, 0, 0, 0, explosionPower);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (entityHitResult.getEntity() instanceof GhastEntity) { // if the fireball is about to kill a ghast
			this.setOwner(this); // don't give the player credit, that's not how you're meant to get that advancement
		}
		super.onEntityHit(entityHitResult);
	}
}
