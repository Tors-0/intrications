package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.util.ItemModified;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class TeleportStaffItem extends RangedWeaponItem implements Vanishable {
	public static final Predicate<ItemStack> FUEL_ITEM = itemStack -> itemStack.isOf(Items.ENDER_PEARL);
	public static final int MAX_DISTANCE = 96;

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return stack.isDamaged() && ingredient.isOf(Items.ENDER_EYE);
	}

	public TeleportStaffItem(Settings settings) {
		super(settings);
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return FUEL_ITEM;
	}

	@Override
	public int getRange() {
		return 96;
	}


	public int getMaxUseTime(ItemStack stack) {
		return 100;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			ItemStack itemStack = user.getStackInHand(hand);
			if (!user.getAbilities().creativeMode && user.getArrowType(itemStack).isEmpty()) {
				return TypedActionResult.fail(itemStack);
			} else {
				user.setCurrentHand(hand);
				return TypedActionResult.consume(itemStack);
			}
		} else {
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
	}

	public static float getChargePercentage(ItemStack stack, int remainingUseTicks) {
		return (float) (stack.getMaxUseTime() - remainingUseTicks) / stack.getMaxUseTime();
	}

	@Override
	public boolean isDamageable() {
		return true;
	}
	public double getMaxDistance(ItemStack stack, int remainingUseTicks) {
		return MAX_DISTANCE * Math.pow(getChargePercentage(stack, remainingUseTicks), 2);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!world.isClient() && user instanceof PlayerEntity player) {
			if (remainingUseTicks > .8f * getMaxUseTime(stack)) {
				return;
			}
			if (!player.getArrowType(stack).isEmpty() || player.getAbilities().creativeMode) {
				ItemStack itemStack = player.getArrowType(stack);
				double maxDistance = getMaxDistance(stack, remainingUseTicks);
				Vec3d startPoint = player.getPos().add(0.0, 1.6F, 0.0);
				BlockHitResult hitResult = ItemModified.raycast(
					world,
					player,
					RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.NONE,
					maxDistance
				);

				BlockPos pos = new BlockPos(hitResult.getBlockPos());

				// coordinate handling to avoid teleporting into blocks + teleport
				player.teleport(pos.getX() + .5, pos.getY(), pos.getZ() + .5, true);

				// particles & arrival sound
				for (int i = 1; i < 15; ++i) {
                    ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, startPoint.x, startPoint.y, startPoint.z, 1, 0, 0, 0, Math.random() * .5);
				}
				player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 3.0F, 1.0F);

				// if player isn't in creative, remove an ender pearl
				if (!player.getAbilities().creativeMode && !itemStack.isEmpty()) {
					itemStack.decrement(1);
					if (itemStack.isEmpty()) {
						player.getInventory().removeOne(itemStack);
					}
				}

				stack.damage(1, player, (p) -> {
					p.sendToolBreakStatus(player.getActiveHand());
				});
				player.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
	}
}
