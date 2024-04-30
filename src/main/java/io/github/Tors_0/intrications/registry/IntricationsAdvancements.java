package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.criterion.BlankCriterion;
import net.minecraft.advancement.criterion.Criteria;

public class IntricationsAdvancements {
	public static BlankCriterion ARTIFICIAL_INFLATION =
		Criteria.register(BlankCriterion.getID("artificial_inflation"), new BlankCriterion());
	public static BlankCriterion MAGIC_MISSILE =
		Criteria.register(BlankCriterion.getID("magic_missile"), new BlankCriterion());
	public static BlankCriterion TCHAIKOVSKY_NO =
		Criteria.register(BlankCriterion.getID("tchaikovsky_no"), new BlankCriterion());
	public static BlankCriterion USELESS_PROJECTILE =
		Criteria.register(BlankCriterion.getID("useless_projectile"), new BlankCriterion());

	public static void register() {
		// init static
	}
}
