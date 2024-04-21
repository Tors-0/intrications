package io.github.Tors_0.intrications;

import com.google.gson.annotations.SerializedName;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.IntegerRange;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class IntricationsConfig extends ReflectiveConfig {
	public static final IntricationsConfig INSTANCE = QuiltConfig.create(Intrications.ID, Intrications.ID, IntricationsConfig.class);

	@SerializedName("fire_staff_enabled")
	public final TrackedValue<Boolean> fireStaffEnabled = this.value(true);

	@IntegerRange(min = 3, max = 25)
	@Comment("Anything over 5 is like, very OP >:3")
	@SerializedName("fire_staff_max_explosion_power")
	public final TrackedValue<Integer> fireStaffMaxExplosionPower = this.value(4);

	@SerializedName("slime_staff_enabled")
	public final TrackedValue<Boolean> slimeStaffEnabled = this.value(true);

	@IntegerRange(min = 4, max = 12)
	@SerializedName("max_slime_size")
	public final TrackedValue<Integer> maximumSlimeSize = this.value(10);

	@SerializedName("teleport_staff_enabled")
	public final TrackedValue<Boolean> teleportStaffEnabled = this.value(true);

	@SerializedName("spellcasting_staff_enabled")
	public final TrackedValue<Boolean> spellcastingStaffEnabled = this.value(true);
}
