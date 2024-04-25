package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.entity.MineEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class MineItem extends Item {
	public MineItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos pos = context.getBlockPos().offset(context.getSide());
		MineEntity mine = new MineEntity(pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, context.getWorld());
		mine.setOwner(context.getPlayer());
		mine.setRotation(context.getSide());
		BlockPos velocity = context.getBlockPos().subtract(mine.getBlockPos());
		mine.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
		context.getWorld().spawnEntity(mine);
		context.getStack().decrement(1);

		return ActionResult.success(context.getWorld().isClient);
	}
}
