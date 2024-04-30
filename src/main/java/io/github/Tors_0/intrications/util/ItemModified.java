package io.github.Tors_0.intrications.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ItemModified {
	public static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, double distance) {
		float f = player.getPitch();
		float g = player.getYaw();
		Vec3d vec3d = player.getEyePos();
		float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
		float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
		float j = -MathHelper.cos(-f * 0.017453292F);
		float k = MathHelper.sin(-f * 0.017453292F);
		float l = i * j;
		float n = h * j;
		Vec3d vec3d2 = vec3d.add((double)l * distance, (double)k * distance, (double)n * distance);
		return world.raycast(new RaycastContext(vec3d, vec3d2, shapeType, fluidHandling, player));
	}
}
