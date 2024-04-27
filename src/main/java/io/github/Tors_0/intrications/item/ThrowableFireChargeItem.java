package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.IntricationsConfig;
import io.github.Tors_0.intrications.entity.StaffFireballEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ThrowableFireChargeItem extends FireChargeItem {
	public static final int FIREBALL_POWER = IntricationsConfig.INSTANCE.fireStaffMaxExplosionPower.value();
	public ThrowableFireChargeItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound(
			null,
			user.getX(),
			user.getY(),
			user.getZ(),
			SoundEvents.ITEM_FIRECHARGE_USE,
			SoundCategory.PLAYERS,
			0.5F,
			0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			StaffFireballEntity fireball = new StaffFireballEntity(world, user, FIREBALL_POWER);
			fireball.setItem(itemStack);
			fireball.move(MovementType.SELF, user.getRotationVector().normalize());
//			fireball.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
			world.spawnEntity(fireball);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			itemStack.decrement(1);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (!entity.isFireImmune()) { // if the entity isnt immune to fire
			entity.setOnFireFor(5); // set it on fire >:3
			entity.damage(DamageSource.player(user),7f); // then hurt it too >:3

			stack.damage(1, user, (p) -> { // make the fire staff use 1 durability
				p.sendToolBreakStatus(hand); // break it if there's no durability left
			});

			user.incrementStat(Stats.USED.getOrCreateStat(this)); // wahoo statistics

			stack.damage(1, user, (p) -> {
				p.sendToolBreakStatus(user.getActiveHand());
			});
			return ActionResult.SUCCESS; // and yippee we did it
		} else {
			return ActionResult.FAIL; // if the entity is immune to fire, we fail :(
		}
	}
}
