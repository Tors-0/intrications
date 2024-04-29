package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.criterion.InflationCriterion;
import io.github.Tors_0.intrications.criterion.MagicMissileCriterion;
import io.github.Tors_0.intrications.criterion.TchaikovskyCriterion;
import io.github.Tors_0.intrications.criterion.UselessProjectileCriterion;
import net.minecraft.advancement.criterion.Criteria;

public class IntricationsAdvancements {
	public static InflationCriterion ARTIFICIAL_INFLATION = Criteria.register(new InflationCriterion());
	public static MagicMissileCriterion MAGIC_MISSILE = Criteria.register(new MagicMissileCriterion());
	public static TchaikovskyCriterion TCHAIKOVSKY_NO = Criteria.register(new TchaikovskyCriterion());
	public static UselessProjectileCriterion USELESS_PROJECTILE = Criteria.register(new UselessProjectileCriterion());

	public static void register() {
		// init static
	}
}
