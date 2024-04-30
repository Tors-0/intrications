package io.github.Tors_0.intrications.criterion;

import com.mojang.serialization.Codec;
import io.github.Tors_0.intrications.Intrications;
import net.minecraft.advancement.criterion.AbstractCriterionTrigger;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BlankCriterion extends AbstractCriterionTrigger<AbstractCriterionTrigger.Conditions> {
	public BlankCriterion() {}

	public static String getID(String name) {
		return Intrications.getIdentifier(name).toString();
	}

	public void trigger(ServerPlayerEntity player) {
		super.trigger(player, condition -> {
			return true;
		});
	}

	@Override
	public Codec<AbstractCriterionTrigger.Conditions> method_54937() {
		return null;
	}
}
