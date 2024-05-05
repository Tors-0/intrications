package io.github.Tors_0.intrications.entity;

import io.github.Tors_0.intrications.registry.IntricationsAdvancements;
import io.github.Tors_0.intrications.registry.IntricationsEntities;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MagicMissileEntity extends PersistentProjectileEntity {
	private LivingEntity target;
	private float speed;
	public static final float MAX_SPEED = 4f;

	public MagicMissileEntity(EntityType<? extends MagicMissileEntity> entityType, World world) {
		super(entityType, world);
		this.setSound(SoundEvents.BLOCK_SOUL_SAND_HIT);
		this.setNoGravity(true);
		this.setDamage(0.9f);
	}

	public MagicMissileEntity(World world, double x, double y, double z, LivingEntity target, float speed) {
		super(IntricationsEntities.MAGIC_MISSILE, world);
		this.setPos(x, y, z);
		this.setSound(SoundEvents.BLOCK_SOUL_SAND_HIT);
		this.setNoGravity(true);
		this.target = target;
		this.speed = speed;
		this.setDamage(0.9f);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putFloat("int$speed", this.speed);
		nbt.putUuid("int$target", this.target.getUuid());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.speed = nbt.getFloat("int$speed");
		if (this.getWorld() instanceof ServerWorld server) {
			this.target = (LivingEntity) server.getEntity(nbt.getUuid("int$target"));
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld() instanceof ServerWorld) {
			if ((target == null || target.isDead())) {
				this.discard();
				return;
			}
			if (!(target instanceof EnderDragonEntity)) {
				this.setVelocity(
					target.getEyePos().subtract(this.getPos())
						.normalize()
						.multiply(this.speed)
				);
			} else if (target instanceof EnderDragonEntity dragon) {
				this.setVelocity(
					dragon.head.getPos().add(0, dragon.head.getHeight(), 0)
						.subtract(this.getPos())
						.normalize()
						.multiply(this.speed)
				);
			}
			if (this.distanceTo(target) < 2) {
				this.setVelocity(
					this.getVelocity()
						.multiply(this.distanceTo(target) / 2));
			}
			if (this.getWorld().getBlockState(this.getBlockPos()).getBlock().equals(Blocks.AIR)) {
				this.setNoClip(false);
			}
		}
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		return false;
	}

	@Override
	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity target = entityHitResult.getEntity();
		if (!target.equals(this.target)) {
			return;
		}
		float velLength = (float)this.getVelocity().length();
		int damage = MathHelper.ceil(MathHelper.clamp((double)velLength * this.getDamage(), 0.0, 2.147483647E9));

		if (this.isCritical()) {
			long critDmgIncrease = this.random.nextInt(damage / 2 + 2);
			damage = (int) Math.min(critDmgIncrease + (long) damage, 2147483647L);
		}

		Entity owner = this.getOwner();
		DamageSource damageSource;
		if (owner == null) {
			damageSource = this.getDamageSources().create(DamageTypes.MAGIC, this);
		} else {
			damageSource = this.getDamageSources().create(DamageTypes.MAGIC, this, owner);
			if (owner instanceof LivingEntity) {
				((LivingEntity)owner).onAttacking(target);
			}
		}

		boolean isEnderman = target.getType() == EntityType.ENDERMAN;
		int fireTicks = target.getFireTicks();
		if (this.isOnFire() && !isEnderman) {
			target.setOnFireFor(5);
		}

		target.timeUntilRegen = 0;
		if (target.damage(damageSource, (float)damage)) {
			if (target instanceof LivingEntity livingEntity) {
				if (!this.getWorld().isClient && owner instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity, owner);
					EnchantmentHelper.onTargetDamaged((LivingEntity)owner, livingEntity);
				}
				livingEntity.addStatusEffect(
					new StatusEffectInstance(StatusEffects.GLOWING, 20, 1, false, false, false), owner);

				this.onHit(livingEntity);
				if (owner != null && livingEntity != owner && livingEntity instanceof PlayerEntity && owner instanceof ServerPlayerEntity && !this.isSilent()) {
					((ServerPlayerEntity)owner).networkHandler.sendPacket(new GameStateUpdateS2CPacket(GameStateUpdateS2CPacket.PROJECTILE_HIT_PLAYER, 0.0F));
				}
			}

			this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (this.getPierceLevel() <= 0) {
				this.discard();
			}
		} else {
			target.setFireTicks(fireTicks);
			this.setVelocity(this.getVelocity().multiply(-0.1));
			this.setYaw(this.getYaw() + 180.0F);
			this.prevYaw += 180.0F;
			if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
				if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.discard();
			}
		}

		if (owner instanceof ServerPlayerEntity player && this.target.isDead()) {
			IntricationsAdvancements.MAGIC_MISSILE.trigger(player);
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		if (this.target == null || this.target.isDead()) {
			this.discard();
			super.onBlockHit(blockHitResult);
		} else if (target != null && target.isAlive()) {
			this.setNoClip(true);
		}
	}
}
