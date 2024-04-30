package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.entity.MagicMissileEntity;
import io.github.Tors_0.intrications.entity.MineEntity;
import io.github.Tors_0.intrications.entity.SlimeballEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IntricationsEntities {
	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

	//	public static EntityType<ModEntity> MOD_ENTITY = createEntity("mod_entity", QuiltEntityTypeBuilder.<ModEntity>create(SpawnGroup.MISC, ModEntity::new).setDimensions(EntityDimensions.fixed(0f, 0f)).maxChunkTrackingRange(128).build());
	EntityType<MagicMissileEntity> MAGIC_MISSILE = createEntity("magic_missile",
		QuiltEntityTypeBuilder.<MagicMissileEntity>create(SpawnGroup.MISC, MagicMissileEntity::new)
			.setDimensions(EntityDimensions.fixed(.5f,.5f)).maxChunkTrackingRange(128).build());
	EntityType<MineEntity> MINE = createEntity("mine",
		QuiltEntityTypeBuilder.<MineEntity>create(SpawnGroup.MISC, MineEntity::new)
			.setDimensions(EntityDimensions.fixed(.5f,.25f)).maxChunkTrackingRange(128).build());
	EntityType<SlimeballEntity> SLIMEBALL = createEntity("slimeball",
		QuiltEntityTypeBuilder.<SlimeballEntity>create(SpawnGroup.MISC, SlimeballEntity::new)
			.setDimensions(EntityDimensions.fixed(.5f,.5f)).maxChunkTrackingRange(128).build());

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, new Identifier(Intrications.ID, name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}
}
