package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.IntricationsConfig;
import io.github.Tors_0.intrications.entity.SlimeballEntity;
import io.github.Tors_0.intrications.entity.PlayerFireballEntity;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;

public class IntricationsItemCallbacks {
	public static void register() {
		if (IntricationsConfig.INSTANCE.throwableFireChargesEnabled.value()) {
			UseItemCallback.EVENT.register(((player, world, hand) -> {
				ItemStack itemStack = player.getStackInHand(hand);
				if (itemStack.isOf(Items.FIRE_CHARGE)) {
					world.playSound(
						null,
						player.getX(),
						player.getY(),
						player.getZ(),
						SoundEvents.ITEM_FIRECHARGE_USE,
						SoundCategory.PLAYERS,
						0.5F,
						0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
					);
					if (!world.isClient) {
						Vec3d lookdir = player.getRotationVec(1f).multiply(.5);
						AbstractFireballEntity fireball = IntricationsConfig.INSTANCE.explosiveFireBalls.value() ?
							new PlayerFireballEntity(world, player, lookdir, IntricationsConfig.INSTANCE.fireballExplosionPower.value())
							: new SmallFireballEntity(world, player, lookdir.x, lookdir.y, lookdir.z);
						fireball.setItem(itemStack);
						fireball.move(MovementType.SELF, player.getRotationVector().add(0, 1.6, 0));
						fireball.setProperties(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
						world.spawnEntity(fireball);
					}

					player.incrementStat(Stats.USED.getOrCreateStat(Items.FIRE_CHARGE));
					if (!player.getAbilities().creativeMode) {
						itemStack.decrement(1);
					}

					return TypedActionResult.success(itemStack, world.isClient());
				} else {
					return TypedActionResult.pass(itemStack);
				}
			}));
		}
		if (IntricationsConfig.INSTANCE.throwableSlimeBallsEnabled.value()) {
			UseItemCallback.EVENT.register(((player, world, hand) -> {
				ItemStack itemStack = player.getStackInHand(hand);
				if (itemStack.isOf(Items.SLIME_BALL)) {
					world.playSound(
						null,
						player.getX(),
						player.getY(),
						player.getZ(),
						SoundEvents.ENTITY_SLIME_SQUISH,
						SoundCategory.PLAYERS,
						0.5F,
						0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
					);
					if (!world.isClient) {
						SlimeballEntity slimeballEntity = new SlimeballEntity(world, player);
						slimeballEntity.setItem(itemStack);
						slimeballEntity.setProperties(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
						world.spawnEntity(slimeballEntity);
					}

					player.incrementStat(Stats.USED.getOrCreateStat(Items.SLIME_BALL));
					if (!player.getAbilities().creativeMode) {
						itemStack.decrement(1);
					}

					return TypedActionResult.success(itemStack, world.isClient());
				} else {
					return TypedActionResult.pass(itemStack);
				}
			}));
		}
	}
}
