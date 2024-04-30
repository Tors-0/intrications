package io.github.Tors_0.intrications.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerFireballEntity extends FireballEntity {
	public PlayerFireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
		super(world, owner, velocity.x, velocity.y, velocity.z, explosionPower);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (entityHitResult.getEntity() instanceof GhastEntity) { // if the fireball is about to kill a ghast
			this.setOwner(this); // don't give the player credit, that's not how you're meant to get that advancement
		}
		super.onEntityHit(entityHitResult);
	}

	@Override
	public void tick() {
		super.tick();
		this.age();
	}
	public void age() {
		if (this.age > 1200) {
			this.discard();
		}
	}
}
