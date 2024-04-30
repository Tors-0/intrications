package io.github.Tors_0.intrications;

import com.google.gson.annotations.SerializedName;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.IntegerRange;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class IntricationsConfig extends ReflectiveConfig {
	public static final IntricationsConfig INSTANCE = QuiltConfig.create(Intrications.ID, Intrications.ID, IntricationsConfig.class);

	@SerializedName("throwable_fire_charges")
	public final TrackedValue<Boolean> throwableFireChargesEnabled = this.value(true);

	@SerializedName("explosive_fire_charges")
	@Comment("if false, thrown fireballs act like blaze fireballs, if true, they act like ghast fireballs")
	public final TrackedValue<Boolean> explosiveFireBalls = this.value(false);

	@IntegerRange(min = 3, max = 25)
	@Comment("Anything over 5 is like, very OP >:3")
	@Comment("only applies if 'explosive_fire_charges' = true")
	@SerializedName("fireball_explosion_power")
	public final TrackedValue<Integer> fireballExplosionPower = this.value(4);

	@SerializedName("throwable_slime_balls")
	public final TrackedValue<Boolean> throwableSlimeBallsEnabled = this.value(true);

	@IntegerRange(min = 4, max = 12)
	@SerializedName("max_slime_size")
	public final TrackedValue<Integer> maximumSlimeSize = this.value(10);

	@SerializedName("teleport_staff_enabled")
	public final TrackedValue<Boolean> teleportStaffEnabled = this.value(true);

	@SerializedName("spellcasting_staff_enabled")
	public final TrackedValue<Boolean> spellcastingStaffEnabled = this.value(true);

	@SerializedName("mines_enabled")
	public final TrackedValue<Boolean> minesEnabled = this.value(true);
}
