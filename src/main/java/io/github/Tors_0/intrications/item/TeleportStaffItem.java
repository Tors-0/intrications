package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.util.ItemModified;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class TeleportStaffItem extends Item {

	public TeleportStaffItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			double maxDistance = 20d;
			float tickDelta = 1f;
			Vec3d vec3d = user.getCameraPosVec(tickDelta);
			Vec3d vec3d5 = user.getRotationVec(tickDelta);
			Vec3d vec3d6 = vec3d.add(vec3d5.x * maxDistance, vec3d5.y * maxDistance, vec3d5.z * maxDistance);
			HitResult blockResult = world
				.raycast(new RaycastContext(
					vec3d, vec3d6, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user
				));
			Vec3d startPoint = user.getPos().add(0.0, 1.6F, 0.0);
			BlockHitResult hitResult = ItemModified.raycast(
				world,
				user,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				maxDistance
			);
			// coordinate handling to avoid teleporting into blocks

			BlockPos pos = new BlockPos(hitResult.getPos());
			//pos = pos.offset(hitResult.getSide(), 1);

			user.teleport(pos.getX() + .5, pos.getY(), pos.getZ() + .5, true);

			Vec3d finalPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

			Intrications.LOGGER.info("offset: " + finalPos.subtract(user.getPos()));
			// particles along path & arrival sound
			Vec3d vec3d2 = finalPos.subtract(startPoint);
			Vec3d vec3d3 = vec3d2.normalize();
			for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 1; ++i) {
				Vec3d vec3d4 = startPoint.add(vec3d3.multiply(i));
				((ServerWorld) world).spawnParticles(ParticleTypes.FIREWORK, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
			}
			user.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 3.0F, 1.0F);
			return TypedActionResult.success(user.getStackInHand(hand));
		} else {
			return TypedActionResult.fail(user.getStackInHand(hand));
		}
	}
}
