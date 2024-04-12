package io.github.Tors_0.intrications.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Predicate;

public class FireStaffItem extends RangedWeaponItem implements Vanishable {
	public static final Predicate<ItemStack> FIRE_STAFF_PROJECTILES = itemStack -> itemStack.isOf(Items.FIRE_CHARGE);
	public FireStaffItem(Settings settings) {
		super(settings);
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return FIRE_STAFF_PROJECTILES;
	}

	@Override
	public int getRange() {
		return 15;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
			BlockPos blockPos2 = blockPos.offset(context.getSide());
			if (AbstractFireBlock.canPlaceAt(world, blockPos2, context.getPlayerFacing())) {
				world.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
				BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
				world.setBlockState(blockPos2, blockState2, 11);
				world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
				ItemStack itemStack = context.getStack();
				if (playerEntity instanceof ServerPlayerEntity) {
					Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					itemStack.damage(1, playerEntity, (p) -> {
						p.sendToolBreakStatus(context.getHand());
					});
				}

				return ActionResult.success(world.isClient());
			} else {
				return ActionResult.FAIL;
			}
		} else {
			world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
			world.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, true), 11);
			world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
			if (playerEntity != null) {
				context.getStack().damage(1, playerEntity, (p) -> {
					p.sendToolBreakStatus(context.getHand());
				});
			}

			return ActionResult.success(world.isClient());
		}
	}

	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack) {
		return true;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			ItemStack itemStack = user.getStackInHand(hand);
			boolean bl = user.getArrowType(itemStack).isEmpty();
			if (!user.getAbilities().creativeMode && bl) {
				return TypedActionResult.fail(itemStack);
			} else {
				user.setCurrentHand(hand);
				return TypedActionResult.consume(itemStack);
			}
		} else {
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntity playerEntity) {
			boolean bl = playerEntity.getAbilities().creativeMode;
			ItemStack itemStack = playerEntity.getArrowType(stack);
			if (!itemStack.isEmpty() || bl) {
				if (itemStack.isEmpty()) {
					itemStack = new ItemStack(Items.FIRE_CHARGE);
				}

				int i = this.getMaxUseTime(stack) - remainingUseTicks;
				float f = getPullProgress(i);
				if (!((double)f < 0.1)) {
					boolean bl2 = bl && itemStack.isOf(Items.ARROW);
					if (!world.isClient) {
						Vec3d lookDir = user.getRotationVec(1f);
						FireballEntity fireballEntity = new FireballEntity(world, user, lookDir.x, lookDir.y, lookDir.z, (int) (3 * f));
						fireballEntity.move(MovementType.SELF, lookDir.normalize().add(0,1,0));
						fireballEntity.setOwner(fireballEntity);

						fireballEntity.setProperties(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, .5F);

						stack.damage(1, playerEntity, (p) -> {
							p.sendToolBreakStatus(playerEntity.getActiveHand());
						});

						world.spawnEntity(fireballEntity);
					}

					world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
					if (!bl2 && !playerEntity.getAbilities().creativeMode) {
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							playerEntity.getInventory().removeOne(itemStack);
						}
					}

					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
				}
			}
		}
	}

	public static float getPullProgress(int useTicks) {
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

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (!entity.isFireImmune()) { // if the entity isnt immune to fire
			entity.setOnFireFor(5); // set it on fire >:3
			entity.damage(DamageSource.player(user),7f); // then hurt it too >:3

			stack.damage(1, user, (p) -> { // make the fire staff use 1 durability
				p.sendToolBreakStatus(hand); // break it if there's no durability left
			});

			user.incrementStat(Stats.USED.getOrCreateStat(this)); // wahoo statistics

			return ActionResult.SUCCESS; // and yippee we did it
		} else {
			return ActionResult.FAIL; // if the entity is immune to fire, we fail :(
		}
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return stack.isDamaged() && ingredient.isOf(Items.BLAZE_ROD);
	}
}
