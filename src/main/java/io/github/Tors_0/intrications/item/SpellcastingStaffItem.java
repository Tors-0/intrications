package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.entity.MagicMissileEntity;
import io.github.Tors_0.intrications.entity.SlimeballEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpellcastingStaffItem extends Item {
	public SpellcastingStaffItem(Settings settings) {
		super(settings);
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
		boolean hasXP = user.totalExperience > 0; // check if user has any exp
		if (!user.getAbilities().creativeMode && hasXP) { // if they arent in creative and dont have exp
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
			boolean hasXP = playerEntity.totalExperience >= 3;
			if (hasXP || userHasCreative) { // only continue if the player has exp or access to creative mode

				int i = this.getMaxUseTime(stack) - remainingUseTicks;
				float f = getPullProgress(i);
				if (!((double)f < 0.1)) {
					boolean userCreativeAndHasExp = userHasCreative && hasXP;
					if (!world.isClient) {
						// get the player's looking direction
						Vec3d lookDir = user.getRotationVec(1f);
						// register a slimeball in the world
						MagicMissileEntity missile = new MagicMissileEntity(world, user.getX(), user.getY(), user.getZ());
						// move it one block forward and 1.6 blocks up, to allow player to hit it and prevent it from hitting the player
						missile.move(MovementType.SELF, lookDir.normalize().add(0,1.6f,0));
						// set the player as the owner of it
						missile.setOwner(user);

						// set proper velocity and trajectory for slimeball
						missile.setProperties(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 8.0F, 0F);

						stack.damage(1, playerEntity, (p) -> {
							p.sendToolBreakStatus(playerEntity.getActiveHand()); // use durability
						});

						world.spawnEntity(missile); // MAGIC MISSILE :D
					}

					world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BLOCK_SOUL_SAND_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
					if (!userCreativeAndHasExp && !playerEntity.getAbilities().creativeMode) {
						playerEntity.addExperience(-1);
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
