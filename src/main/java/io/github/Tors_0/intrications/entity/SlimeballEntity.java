package io.github.Tors_0.intrications.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SlimeballEntity extends SnowballEntity {
	public static final ItemStack ITEM = Items.SLIME_BALL.getDefaultStack();
	public SlimeballEntity(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.setItem(ITEM);
	}

	@Override
	public void tick() {
		super.tick();
	}

	private ParticleEffect getParticleParameters() {
		ItemStack itemStack = this.getItem();
		return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SLIME : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			ParticleEffect particleEffect = this.getParticleParameters();

			for(int i = 0; i < 8; ++i) {
				this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity(); // get the entity we hit
		int i = entity instanceof SlimeEntity ? 0 : 1; // if slime, deal no damage, otherwise: 1 damage
		// give player credit if this accidentally kills smth
		entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)i);

		// launch the hit entity
		Vec3d velocity = this.getVelocity();
		velocity.add(0,.2,0);
		entity.setVelocity(velocity);
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		BlockPos blockPos = blockHitResult.getBlockPos();
		blockPos = blockPos.offset(blockHitResult.getSide());
		BlockState state = Blocks.SLIME_BLOCK.getDefaultState();
		if (world.canPlace(state, blockPos, ShapeContext.absent())) {
			world.setBlockState(blockPos, state);
			world.playSound(null, blockPos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1f, 1f);
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		// override default projectile landing actions
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			this.onEntityHit((EntityHitResult)hitResult);
		} else if (type == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			this.onBlockHit(blockHitResult);
			BlockPos blockPos = blockHitResult.getBlockPos();
			this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.create(this, this.world.getBlockState(blockPos)));
		}

		// slimeball land behavior
		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)3);
			this.discard();
		}
	}
}
