package io.github.Tors_0.intrications.entity;

import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class MagicMissileEntity extends ArrowEntity {

	public MagicMissileEntity(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.setSound(SoundEvents.BLOCK_SOUL_SAND_HIT);
		this.setGlowing(true);
	}

	public MagicMissileEntity(World world, LivingEntity owner) {
		super(world, owner);
		this.setSound(SoundEvents.BLOCK_SOUL_SAND_HIT);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public int getColor() {
		return 8900331;
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		return false;
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 0) {
			int i = this.getColor();
			if (i != -1) {
				double d = (double)(i >> 16 & 255) / 255.0;
				double e = (double)(i >> 8 & 255) / 255.0;
				double f = (double)(i >> 0 & 255) / 255.0;

				for(int j = 0; j < 20; ++j) {
					this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
				}
			}
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		this.discard();
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		super.onPlayerCollision(player);
	}

	@Override
	public int getTeamColorValue() {
		return getColor();
	}
}
