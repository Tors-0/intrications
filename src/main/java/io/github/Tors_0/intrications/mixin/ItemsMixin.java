package io.github.Tors_0.intrications.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.Tors_0.intrications.IntricationsConfig;
import io.github.Tors_0.intrications.item.ThrowableFireChargeItem;
import io.github.Tors_0.intrications.item.ThrowableSlimeballItem;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class ItemsMixin {
	@WrapOperation(
		method = "<clinit>",
		slice = @Slice(
			from = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;BOOK:Lnet/minecraft/item/Item;")
		),
		at = @At(value = "NEW", target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0)
	)
	private static @NotNull Item intrications$throw_slimeballs(Item.Settings settings, Operation<Item> original) {
		if (IntricationsConfig.INSTANCE.throwableSlimeBallsEnabled.value()) {
			return new ThrowableSlimeballItem(settings);
		} else {
			return original.call(settings);
		}
	}
	@WrapOperation(
		method = "<clinit>",
		slice = @Slice(
			from = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;EXPERIENCE_BOTTLE:Lnet/minecraft/item/Item;")
		),
		at = @At(value = "NEW", target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/FireChargeItem;", ordinal = 0)
	)
	private static FireChargeItem intrications$throw_fireballs(Item.Settings settings, Operation<FireChargeItem> original) {
		if (IntricationsConfig.INSTANCE.throwableFireChargesEnabled.value()) {
			return new ThrowableFireChargeItem(settings);
		} else {
			return original.call(settings);
		}
	}
}
