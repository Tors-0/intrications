package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.entity.MagicMissileEntity;
import io.github.Tors_0.intrications.entity.SlimeballEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
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
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public class SpellcastingStaffItem extends Item {
	public static final Predicate<Entity> VALID_ENTITY = entity -> entity instanceof LivingEntity;
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
		boolean hasXP = user.totalExperience >= 3; // check if user has any exp
		if (!user.getAbilities().creativeMode && !hasXP) { // if they arent in creative and dont have exp
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
					int xpCost = -3;
					if (!world.isClient) {
						double range = 36.6d;
						float tickDelta = 1f;
						Vec3d playerCamPos = playerEntity.getCameraPosVec(tickDelta);
						Vec3d lookDir = playerEntity.getRotationVec(tickDelta);
						Vec3d endPos = playerCamPos.add(lookDir.x * range, lookDir.y * range, lookDir.z * range);
						HitResult blockResult = world
							.raycast(new RaycastContext(
								playerCamPos, endPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, playerEntity
							));
						Vec3d startPoint = playerEntity.getPos().add(0.0, 1.6F, 0.0);
						double distanceToBlockSq = blockResult != null ? blockResult.getPos().squaredDistanceTo(startPoint) : Double.POSITIVE_INFINITY;
						Vec3d rotationVec = playerEntity.getRotationVec(1.0F);
                        double effectiveRangeSq = Math.min(range * range, distanceToBlockSq);
						Vec3d endPoint = startPoint.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
						Box box = playerEntity.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0D,1.0D,1.0D);
						EntityHitResult entityHitResult = ProjectileUtil.raycast(
							playerEntity,
							startPoint,
							endPoint,
							box,
							VALID_ENTITY,
							effectiveRangeSq
						);
						if (entityHitResult == null) {
							xpCost = 0;
							return;
						}
						ArrayList<MagicMissileEntity> missiles = new ArrayList<>();
						for (byte b = 0; b < 3; b++) {
							missiles.add(new MagicMissileEntity(world, user.getX(), user.getY(), user.getZ(),
								(LivingEntity) entityHitResult.getEntity(), f * MagicMissileEntity.MAX_SPEED));
						}
						lookDir = lookDir.rotateY((float) (Math.PI / 4));
                        for (MagicMissileEntity missile : missiles) {
							// set the player as the owner of it
							missile.setOwner(user);
                            // move the missile to a position around the player
                            missile.move(MovementType.SELF, lookDir.normalize().multiply(3).add(0, 1.6f, 0));
							// change the angle of the next missile
							lookDir = lookDir.rotateY((float) -(Math.PI / 4));
							// spawn the missile
							world.spawnEntity(missile);
                        }


						stack.damage(1, playerEntity, (p) -> {
							p.sendToolBreakStatus(playerEntity.getActiveHand()); // use durability
						});

						if (hasXP && !playerEntity.getAbilities().creativeMode) {
							playerEntity.addExperience(xpCost); // use xp
						}

						// give the item a cooldown so it cant be spammed
						if (!playerEntity.isCreative()) {
							playerEntity.getItemCooldownManager().set(this, 50);
						}
					}

					world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BLOCK_SOUL_SAND_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);

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
