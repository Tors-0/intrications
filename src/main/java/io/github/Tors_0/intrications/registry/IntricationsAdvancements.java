package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.criterion.InflationCriterion;
import io.github.Tors_0.intrications.criterion.MagicMissileCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionConditions;

public class IntricationsAdvancements {
	public static InflationCriterion ARTIFICIAL_INFLATION = Criteria.register(new InflationCriterion());
	public static MagicMissileCriterion MAGIC_MISSILE = Criteria.register(new MagicMissileCriterion());

	public static void register() {
		// init static
	}
}
