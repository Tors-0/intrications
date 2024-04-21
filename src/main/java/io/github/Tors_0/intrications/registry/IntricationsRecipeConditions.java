package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.IntricationsConfig;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.util.Identifier;

public class IntricationsRecipeConditions {
	public static void register() {
		ResourceConditions.register(new Identifier(Intrications.ID, "fire_staff_enabled"),
			jsonObject -> IntricationsConfig.INSTANCE.fireStaffEnabled.value());

		ResourceConditions.register(new Identifier(Intrications.ID, "slime_staff_enabled"),
			jsonObject -> IntricationsConfig.INSTANCE.slimeStaffEnabled.value());

		ResourceConditions.register(new Identifier(Intrications.ID, "teleport_staff_enabled"),
			jsonObject -> IntricationsConfig.INSTANCE.teleportStaffEnabled.value());

		ResourceConditions.register(Intrications.getIdentifier("spellcasting_staff_enabled"),
			jsonObject -> IntricationsConfig.INSTANCE.spellcastingStaffEnabled.value());
	}
}
