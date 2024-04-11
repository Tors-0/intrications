package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.util.ItemModified;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Vanishable;
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

public class TeleportStaffItem extends Item implements Vanishable {
	public static final ItemStack FUEL_ITEM = new ItemStack(Items.ENDER_PEARL);
	public static final int MAX_DISTANCE = 50;

	public TeleportStaffItem(Settings settings) {
		super(settings.maxDamage(MAX_DISTANCE + 1));
	}

	public int getMaxUseTime(ItemStack stack) {
		return 120;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			ItemStack itemStack = user.getStackInHand(hand);
			if (!user.isCreative() && !user.getInventory().contains(FUEL_ITEM)) {
				return TypedActionResult.fail(itemStack);
			} else {
				user.setCurrentHand(hand);
				return TypedActionResult.consume(itemStack);
			}
		} else {
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
	}

	public static float getDrawPercentage(ItemStack stack, int remainingUseTicks) {
		return (float) (stack.getMaxUseTime() - remainingUseTicks) / stack.getMaxUseTime();
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return super.isItemBarVisible(stack);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return super.getItemBarColor(stack);
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		//stack.setDamage(stack.getMaxDamage() - Math.max(1,(int) (MAX_DISTANCE * getDrawPercentage(stack, remainingUseTicks))));
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return super.getItemBarStep(stack);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!world.isClient() && user instanceof PlayerEntity player) {
			if (remainingUseTicks > .8f * getMaxUseTime(stack)) {
				stack.setDamage(0);
				return;
			}
			if (player.getInventory().contains(FUEL_ITEM) || player.isCreative()) {
				ItemStack itemStack = player.getInventory().getStack(player.getInventory().getSlotWithStack(FUEL_ITEM));
				double maxDistance = MAX_DISTANCE * getDrawPercentage(stack, remainingUseTicks);
				Vec3d startPoint = player.getPos().add(0.0, 1.6F, 0.0);
				BlockHitResult hitResult = ItemModified.raycast(
					world,
					player,
					RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.NONE,
					maxDistance
				);

				BlockPos pos = new BlockPos(hitResult.getPos());

				// coordinate handling to avoid teleporting into blocks
				player.teleport(pos.getX() + .5, pos.getY(), pos.getZ() + .5, true);

				// particles & arrival sound
				for (int i = 1; i < 15; ++i) {
                    ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, startPoint.x, startPoint.y, startPoint.z, 1, 0, 0, 0, Math.random() * .5);
				}
				player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 3.0F, 1.0F);

				// if player isn't in creative, remove an ender pearl
				if (!player.isCreative() && !itemStack.isEmpty()) {
					itemStack.decrement(1);
					if (itemStack.isEmpty()) {
						player.getInventory().removeOne(itemStack);
					}
				}

				stack.setDamage(0);
				player.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
	}
}
