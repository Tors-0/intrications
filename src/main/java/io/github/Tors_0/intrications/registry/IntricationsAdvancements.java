package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.criterion.InflationCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionConditions;

public class IntricationsAdvancements {
	public static InflationCriterion ARTIFICIAL_INFLATION = Criteria.register(new InflationCriterion());

	public static void register() {
		// init static
	}
}
