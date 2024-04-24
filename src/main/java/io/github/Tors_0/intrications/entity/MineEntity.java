package io.github.Tors_0.intrications.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;

public class MineEntity extends PersistentProjectileEntity {
	/**
	 * if this is true, explode next game tick
	 * this to prevent mines from trying to detonate outside loaded chunks
 	 */
	private boolean shouldExplode = false;
	private int life = 0;
	public static final Map<UUID, List<MineEntity>> playerMines = new HashMap<>();
	protected MineEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected MineEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world) {
		super(type, x, y, z, world);
	}

	protected MineEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
		super(type, owner, world);
		if (playerMines.containsKey(owner.getUuid())) {
			playerMines.get(owner.getUuid()).add(this);
		} else {
			playerMines.put(owner.getUuid(), new ArrayList<MineEntity>());
			playerMines.get(owner.getUuid()).add(this);
		}
	}

	@Override
	protected ItemStack asItemStack() {
		return null;
	}

	@Override
	protected void age() {
		++this.life;
		if (this.life >= 1200) {
			this.shouldExplode = true;
		}
	}
}
