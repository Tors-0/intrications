package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.entity.SlimeballEntity;
import io.github.Tors_0.intrications.entity.StaffFireballEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SlimeStaffItem extends RangedWeaponItem {
	public static final Predicate<ItemStack> SLIME_STAFF_PROJECTILES = itemStack -> itemStack.isOf(Items.SLIME_BLOCK);
	public SlimeStaffItem(Settings settings) {
		super(settings);
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return SLIME_STAFF_PROJECTILES;
	}

	@Override
	public int getRange() {
		return 15;
	}

	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		boolean bl = user.getArrowType(itemStack).isEmpty(); // check if user has any slime blocks
		if (!user.getAbilities().creativeMode && bl) { // if they arent in creative and doesnt have slime blocks
			return TypedActionResult.fail(itemStack); // dont let them charge the staff
		} else {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack); // otherwise they all good :)
		}
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntity playerEntity) {
			boolean userHasCreative = playerEntity.getAbilities().creativeMode;
			ItemStack itemStack = playerEntity.getArrowType(stack);
			if (!itemStack.isEmpty() || userHasCreative) { // only continue if the player has slime blocks or access to creative mode
				if (itemStack.isEmpty()) {
					itemStack = new ItemStack(Items.SLIME_BLOCK);
				}

				int i = this.getMaxUseTime(stack) - remainingUseTicks;
				float f = getPullProgress(i);
				if (!((double)f < 0.1)) {
					boolean userCreativeAndHasAmmo = userHasCreative && itemStack.isOf(Items.SLIME_BLOCK);
					if (!world.isClient) {
						// get the player's looking direction
						Vec3d lookDir = user.getRotationVec(1f);
						// register a slimeball in the world
						SlimeballEntity slimeballEntity = new SlimeballEntity(world, user.getX(), user.getY(), user.getZ());
						// move it one block forward and 1.6 blocks up, to allow player to hit it and prevent it from hitting the player
						slimeballEntity.move(MovementType.SELF, lookDir.normalize().add(0,1.6f,0));
						// set the player as the owner of it
						slimeballEntity.setOwner(user);

						// set proper velocity and trajectory for slimeball
						slimeballEntity.setProperties(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, .5F);

						stack.damage(1, playerEntity, (p) -> {
							p.sendToolBreakStatus(playerEntity.getActiveHand()); // use durability
						});

						world.spawnEntity(slimeballEntity); // spawn in da slimeball :3
					}

					world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
					if (!userCreativeAndHasAmmo && !playerEntity.getAbilities().creativeMode) {
						itemStack.decrement(1); // remove one slime block
						if (itemStack.isEmpty()) { // if item stack has no slime blocks left
							playerEntity.getInventory().removeOne(itemStack); // remove the empty stack from the inventory
						}
					}

					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
				}
			}
		}
	}

	public static float getPullProgress(int useTicks) { // tell us if the staff is fully charged
		// code copied from BowItem.class
		float f = (float)useTicks / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

}
