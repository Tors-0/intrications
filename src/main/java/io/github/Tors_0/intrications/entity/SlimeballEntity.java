package io.github.Tors_0.intrications.entity;

import io.github.Tors_0.intrications.IntricationsConfig;
import io.github.Tors_0.intrications.registry.IntricationsAdvancements;
import io.github.Tors_0.intrications.registry.IntricationsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
	public static final int SLIME_MAX_SIZE = IntricationsConfig.INSTANCE.maximumSlimeSize.value();

	public SlimeballEntity(EntityType<? extends SlimeballEntity> entityType, World world) {
		super(entityType, world);
		this.setItem(ITEM);
	}

	public SlimeballEntity(World world, LivingEntity owner) {
		super(world, owner);
		this.setItem(ITEM);
	}

	public SlimeballEntity(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.setItem(ITEM);
	}

	@Override
	public ItemStack getStack() {
		return ITEM;
	}

	@Override
	public void tick() {
		super.tick();
	}

	private ParticleEffect getParticleParameters() {
		ItemStack itemStack = this.getItem();
		return itemStack.isEmpty() ? ParticleTypes.ITEM_SLIME : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack);
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
		if (entity instanceof SlimeEntity slime && !(entity instanceof MagmaCubeEntity)) {
			int slimeSize = slime.getSize();
			if (slimeSize >= SLIME_MAX_SIZE) {
				return;
			}
			int newSize = Math.min(slimeSize + 1, SLIME_MAX_SIZE);
			if (newSize != slimeSize) {
				// give the slime a chance of growing inversely proportional to its size
				float sizeChance = 1f / (slimeSize < 1 ? 1 : (float) slimeSize / 2f);
				if (Math.random() <= sizeChance) {
					// increase the slime size
					slime.setSize(newSize, true);
					// particles & sound
					for (int i = 1; i < 15; ++i) {
						((ServerWorld) world).spawnParticles(newSize >= SLIME_MAX_SIZE ? ParticleTypes.END_ROD : ParticleTypes.POOF, slime.getX(), slime.getY(), slime.getZ(), 1, 0, 0, 0, Math.random());
					}
					slime.playSound(SoundEvents.ENTITY_SLIME_SQUISH);
					// if we maxed out the slime size
					if (this.getOwner() instanceof ServerPlayerEntity player && newSize == SLIME_MAX_SIZE) {
						// give the player the advancement
						IntricationsAdvancements.ARTIFICIAL_INFLATION.trigger(player);
					}
				} else if (slime.getHealth() < slime.getMaxHealth()) {
					float diff = slime.getMaxHealth() - slime.getHealth();
					slime.heal((float) (diff * Math.random()));
					// particles
					for (int i = 1; i < 15; ++i) {
						((ServerWorld) world).spawnParticles(ParticleTypes.HEART, slime.getX(), slime.getY(), slime.getZ(), 1, 0, 0, 0, Math.random());
					}
				}
			}

		} else {
			// give player credit if this accidentally kills smth
			entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 1f);

			// launch the hit entity
			Vec3d velocity = this.getVelocity();
			velocity.add(0, .2, 0);
			entity.setVelocity(velocity);

			if (entity instanceof LivingEntity && ((LivingEntity) entity).isDead() && this.getOwner() instanceof ServerPlayerEntity) {
				IntricationsAdvancements.USELESS_PROJECTILE.trigger((ServerPlayerEntity) this.getOwner());
			}
			dropItem(Items.SLIME_BALL);
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		BlockPos blockPos = blockHitResult.getBlockPos();
		blockPos = blockPos.offset(blockHitResult.getSide());
		BlockState state = IntricationsBlocks.AIRY_SLIME.getDefaultState();
		if (world.canPlace(state, blockPos, ShapeContext.absent())) {
			world.setBlockState(blockPos, state);
			world.playSound(null, blockPos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1f, 1f);
		} else {
			dropItem(Items.SLIME_BALL);
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
