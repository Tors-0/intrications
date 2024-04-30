package io.github.Tors_0.intrications.criterion;

import com.google.gson.JsonObject;
import io.github.Tors_0.intrications.Intrications;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.unmapped.C_ctsfmifk;
import net.minecraft.util.Identifier;

public class UselessProjectileCriterion extends AbstractCriterion<UselessProjectileCriterion.Conditions> {
	public UselessProjectileCriterion() {}

	static final Identifier ID = new Identifier(Intrications.ID, "useless_projectile");
	@Override
	protected Conditions conditionsFromJson(JsonObject obj, C_ctsfmifk playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new Conditions(playerPredicate);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public void trigger(ServerPlayerEntity player) {
		super.trigger(player, condition -> {
			return true;
		});
	}
	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(C_ctsfmifk playerPredicate) {
			super(ID, playerPredicate);
		}
	}
}
