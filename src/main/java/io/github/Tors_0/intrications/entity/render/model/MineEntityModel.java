package io.github.Tors_0.intrications.entity.render.model;// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.entity.MineEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class MineEntityModel<T extends MineEntity> extends EntityModel<T> {
	public static final Identifier IDENTIFIER = Intrications.getIdentifier("textures/entity/mine/mine.png");
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(IDENTIFIER, "main");
	private final ModelPart bone;

	public MineEntityModel(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create()
			.uv(0,0).cuboid(-4f, -2f, -4f, 8f, 3f, 8f)
			.uv(5,11).cuboid(-2.5f,-3,-2.5f,5,1,5),
			ModelTransform.of(0f,24f,0f,0,0,0));

		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.bone.yaw = (float) ((Math.PI / 180.0) * entity.getPich());
		this.bone.pitch = (float) ((Math.PI / 180.0) * entity.getYa());
		this.bone.roll = (float) ((Math.PI / 180.0) * entity.getRoll());
	}

	@Override
	public void render(MatrixStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
