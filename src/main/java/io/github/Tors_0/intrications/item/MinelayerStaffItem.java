package io.github.Tors_0.intrications.item;

import io.github.Tors_0.intrications.entity.MineEntity;
import io.github.Tors_0.intrications.registry.IntricationsAdvancements;
import io.github.Tors_0.intrications.registry.IntricationsItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
			detonateAttachedMines(stack, server, null);
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (world instanceof ServerWorld server) {
			if (user.isSneaking()) { // sneak rclick to detonate mines
				detonateAttachedMines(stack, server, (ServerPlayerEntity) user);

				stack.damage(1, user, (p) -> { // use durability
					p.sendToolBreakStatus(user.getActiveHand()); // break tool if no dura
				});

				return TypedActionResult.success(stack);
			}
			ItemStack ammo;
			if (hand == Hand.MAIN_HAND && ((ammo = user.getStackInHand(Hand.OFF_HAND)).isOf(IntricationsItems.MINE) || user.getAbilities().creativeMode)) {
				// get the player's looking direction
				Vec3d lookDir = user.getRotationVec(1f);
				// register a mine in the world
				MineEntity mineEntity = new MineEntity(user.getX(), user.getY(), user.getZ(), world, ammo);
				// move it one block forward and 1.6 blocks up, to allow player to hit it and prevent it from hitting the player
				mineEntity.move(MovementType.SELF, lookDir.normalize().add(0, 1.6f, 0));
				// set the player as the owner of it
				mineEntity.setOwner(user);
				// the mine is linked to a staff
				mineEntity.link();

				// store the mine's uuid in the staff
				NbtString nbtString = NbtString.of(mineEntity.getUuidAsString());
				if (!stack.getOrCreateNbt().contains("mines")) {
					stack.getOrCreateNbt().put("mines", new NbtList());
				}
				stack.getOrCreateNbt().getList("mines", NbtElement.STRING_TYPE).add(nbtString);

				// set proper velocity and trajectory for mine
				mineEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 3.0F, .5F);

				if (!user.getAbilities().creativeMode) {
					ammo.decrement(1);
					if (ammo.isEmpty()) {
						user.getInventory().removeOne(ammo);
					}
				}

				world.spawnEntity(mineEntity); // spawn in da mine >:3
				return TypedActionResult.consume(stack);
			}
		}
		return TypedActionResult.pass(stack);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		List<Entity> mines = context.getWorld().getOtherEntities(
			context.getPlayer(),
			new Box(context.getHitPos().subtract(1,1,1), context.getHitPos().add(1,1,1)),
			entity -> entity instanceof MineEntity && !((MineEntity) entity).isLinked());
		if (!mines.isEmpty()) {
			ItemStack stack = context.getStack();
			if (!stack.getOrCreateNbt().contains("mines")) {
				stack.getOrCreateNbt().put("mines", new NbtList());
			}
			NbtList list = stack.getOrCreateNbt().getList("mines", NbtElement.STRING_TYPE);
			mines.forEach(mine -> {
				if (!list.contains(NbtString.of(mine.getUuidAsString()))) {
					list.add(NbtString.of(mine.getUuidAsString()));
				} else {
					return;
				}
				// don't let the player link mines if an owner already exists
				// (would be too easy if you could just link and then detonate them)
				if (!(((MineEntity) mine).getOwner() instanceof PlayerEntity)) {
					((MineEntity) mine).setOwner(context.getPlayer());
					((MineEntity) mine).link();
					if (context.getWorld().isClient) {
						context.getWorld().addParticle(ParticleTypes.GLOW, true,
							mine.getParticleX(.5), mine.getEyeY(), mine.getParticleZ(.5),
							0,0,0);
					}
					context.getWorld().playSoundFromEntity(null, context.getPlayer(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.PLAYERS, 1, 1);
				}
			});
			return ActionResult.success(context.getWorld().isClient);
		}
		return ActionResult.FAIL;
	}

	private void detonateAttachedMines(ItemStack stack, ServerWorld server, @Nullable ServerPlayerEntity player) {
		NbtList nbtList = stack.getOrCreateNbt().getList("mines", NbtElement.STRING_TYPE);
		byte b = 0;
		for (int i = nbtList.size() - 1; i >= 0; i--) {
			Entity mine = server.getEntity(UUID.fromString(nbtList.getString(i)));
			if (mine instanceof MineEntity) {
				((MineEntity) mine).detonate();
				b++;
			}
			nbtList.remove(i);
			if (b >= 21 && player != null) {
				b = 0;
				IntricationsAdvancements.TCHAIKOVSKY_NO.trigger(player);
			}
		}
	}
}
