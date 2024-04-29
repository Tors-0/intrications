package io.github.Tors_0.intrications.entity;

import io.github.Tors_0.intrications.registry.IntricationsEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class MineEntity extends PersistentProjectileEntity {
	/**
	 * if this is true, explode next game tick.
	 * this is to prevent mines from trying to detonate outside loaded chunks
 	 */
	private boolean shouldExplode = false;
	private float pich = 0;
	private float ya = 0;
	private float roll = 0;
	private ItemStack stack;
	private Explosion.DestructionType destructionType = Explosion.DestructionType.BREAK;
	private boolean linked = false;
	public MineEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
		this.setSound(SoundEvents.BLOCK_METAL_FALL);
	}

	public MineEntity(double x, double y, double z, World world, ItemStack stack, Explosion.DestructionType type) {
		super(IntricationsEntities.MINE, x, y, z, world);
		this.setSound(SoundEvents.BLOCK_METAL_FALL);
		this.stack = stack.copy();
		this.stack.setCount(1);
		this.destructionType = type;
	}

	public MineEntity(LivingEntity owner, World world) {
		super(IntricationsEntities.MINE, owner, world);
		this.setSound(SoundEvents.BLOCK_METAL_FALL);
	}
	public void setRotation(Direction dir) {
		switch (dir) {
            case DOWN -> {
				this.ya = 180;
				this.pich = (float) (Math.random() * 90);
				this.roll = 0;
            }
            case UP -> {
				this.ya = 0;
				this.pich = (float) (Math.random() * 90);
				this.roll = 0;
			}
            case NORTH -> {
				this.ya = 90;
				this.pich = 0;
				this.roll = (float) (Math.random() * 90);
            }
            case SOUTH -> {
				this.ya = 90;
				this.pich = 180;
				this.roll = (float) (Math.random() * 90);
            }
            case WEST -> {
				this.ya = 180;
				this.pich = (float) (Math.random() * 90);
				this.roll = -90;
            }
            case EAST -> {
				this.ya = 0;
				this.pich = (float) (Math.random() * 90);
				this.roll = -90;
            }
        }
	}

	/**
	 * check if the {@link MineEntity} is already linked to a mine controller
	 */
	public void link() {
		this.linked = true;
	}

	/**
	 * check if the {@link MineEntity} is already linked
	 * @return {@code true} if mine is already linked to a controller
	 */
	public boolean isLinked() {
		return this.linked;
	}
	public float getPich() {
		return pich;
	}
	public float getYa() {
		return ya;
	}
	public float getRoll() {
		return roll;
	}
	@Override
	public void tick() {
		super.tick();
		if (this.world instanceof ServerWorld server) {
			if (this.shouldExplode) {
				server.createExplosion(this, DamageSource.explosion(getOwner() != null ? (LivingEntity) getOwner() : null), null,
					this.getX(), this.getY(), this.getZ(), 3, false, this.destructionType);
				this.discard();
			}
			if (!server.getOtherEntities(this.getOwner(),
				new Box(this.getPos().subtract(1,1,1), this.getPos().add(1,.5,1)).expand(1),
				entity -> entity instanceof LivingEntity && entity.distanceTo(this) < 2.5f).isEmpty()
			) {
				this.detonate();
			}
			if (this.isOnFire()) {
				this.detonate();
			}
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		this.setRotation(blockHitResult.getSide());
	}

	/**
	 * set this mine to explode next game tick
	 */
	public void detonate() {
		this.shouldExplode = true;
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		if (player.equals(this.getOwner())) {
			if (player.isSneaking()) {
				player.getInventory().insertStack(this.asItemStack());
				return true;
			}
			return false;
		}
		if (player.getAbilities().creativeMode) {
			return player.isSneaking();
		}
		this.detonate();
		return false;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (!entityHitResult.getEntity().equals(this.getOwner())) {
			this.detonate();
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("int$shouldExplode", this.shouldExplode);
		NbtCompound stackNbt = new NbtCompound();
		this.stack.writeNbt(stackNbt);
		nbt.put("stack", stackNbt);
		nbt.putInt("type", this.destructionType.ordinal());
		nbt.putBoolean("linked", this.linked);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.shouldExplode = nbt.getBoolean("int$shouldExplode");
		this.stack = ItemStack.fromNbt(nbt.getCompound("stack"));
		this.destructionType = Explosion.DestructionType.values()[nbt.getInt("type")];
		this.linked = nbt.getBoolean("linked");
	}

	@Override
	protected ItemStack asItemStack() {
		return this.stack;
	}

	@Override
	protected void age() {}
}
