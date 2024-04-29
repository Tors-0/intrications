package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.entity.MineEntity;
import io.github.Tors_0.intrications.entity.SlimeballEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class IntricationsDispenserBehavior {
	static {
		DispenserBlock.registerBehavior(IntricationsItems.MINE, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				return new MineEntity(position.getX(), position.getY(), position.getZ(), world, stack, Explosion.DestructionType.NONE);
			}
		});
		DispenserBlock.registerBehavior(Items.SLIME_BALL, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				return new SlimeballEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});
	}
	public static void register() {}
}
