package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.entity.MineEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.UUID;

public class MinelayerStaffItem extends Item {
	public MinelayerStaffItem(Settings settings) {
		super(settings);
	}

	@Override
	public void onItemEntityDestroyed(ItemEntity entity) {
		super.onItemEntityDestroyed(entity);
		if (entity.getWorld() instanceof ServerWorld server) {
			ItemStack stack = entity.getStack();
			detonateAttachedMines(stack, server);
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (world instanceof ServerWorld server) {
			if (user.isSneaking()) { // sneak rclick to detonate mines
				detonateAttachedMines(stack, server);

				return TypedActionResult.success(stack);
			}

			// get the player's looking direction
			Vec3d lookDir = user.getRotationVec(1f);
			// register a mine in the world
			MineEntity mineEntity = new MineEntity(user.getX(), user.getY(), user.getZ(), world);
			// move it one block forward and 1.6 blocks up, to allow player to hit it and prevent it from hitting the player
			mineEntity.move(MovementType.SELF, lookDir.normalize().add(0,1.6f,0));
			// set the player as the owner of it
			mineEntity.setOwner(user);

			// store the mine's uuid in the staff
			NbtString nbtString = NbtString.of(mineEntity.getUuidAsString());
			if (!stack.getOrCreateNbt().contains("mines")) {
				stack.getOrCreateNbt().put("mines", new NbtList());
			}
			stack.getOrCreateNbt().getList("mines", NbtElement.STRING_TYPE).add(nbtString);

			// set proper velocity and trajectory for mine
			mineEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 3.0F, .5F);

			stack.damage(1, user, (p) -> {
				p.sendToolBreakStatus(user.getActiveHand()); // use durability
			});

			world.spawnEntity(mineEntity); // spawn in da mine >:3
			return TypedActionResult.consume(stack);
		}
		return TypedActionResult.pass(stack);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		List<Entity> mines = context.getWorld().getOtherEntities(context.getPlayer(),
			new Box(context.getHitPos().subtract(1,1,1), context.getHitPos().add(1,1,1)),
			entity -> entity instanceof MineEntity);
		if (!mines.isEmpty()) {
			ItemStack stack = context.getStack();
			if (!stack.getOrCreateNbt().contains("mines")) {
				stack.getOrCreateNbt().put("mines", new NbtList());
			}
			mines.forEach(mine -> {
				stack.getOrCreateNbt().getList("mines", NbtElement.STRING_TYPE).add(NbtString.of(mine.getUuidAsString()));
				((MineEntity)mine).setOwner(context.getPlayer());
			});
			return ActionResult.success(context.getWorld().isClient);
		}
		return ActionResult.FAIL;
	}

	private void detonateAttachedMines(ItemStack stack, ServerWorld server) {
		NbtList nbtList = stack.getOrCreateNbt().getList("mines", NbtElement.STRING_TYPE);
		for (int i = nbtList.size() - 1; i >= 0; i--) {
			Entity mine = server.getEntity(UUID.fromString(nbtList.getString(i)));
			if (mine instanceof MineEntity) {
				((MineEntity) mine).detonate();
			}
			nbtList.remove(i);
		}
	}
}
