package com.windanesz.lostloot.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGoblin extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer ear_left;
	private final ModelRenderer ear_left_r1;
	private final ModelRenderer ear_right;
	private final ModelRenderer ear_right_r1;
	private final ModelRenderer leg_left;
	private final ModelRenderer leg_right;
	private final ModelRenderer arm_left;
	private final ModelRenderer arm_right;

	public ModelGoblin() {
		textureWidth = 48;
		textureHeight = 48;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 24.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 27, 12, -3.0F, -11.0F, -2.0F, 6, 6, 3, 0.01F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -11.0F, -0.5F);
		body.addChild(head);
		head.cubeList.add(new ModelBox(head, 0, 0, -2.9503F, -6.0F, -2.9929F, 6, 6, 6, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 22, 26, -0.9503F, -3.0F, -5.9929F, 2, 3, 3, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 24, 0, -2.9503F, -6.0F, -2.9929F, 6, 6, 6, 0.1F, false));

		ear_left = new ModelRenderer(this);
		ear_left.setRotationPoint(3.0497F, -3.0F, -1.9929F);
		head.addChild(ear_left);


		ear_left_r1 = new ModelRenderer(this);
		ear_left_r1.setRotationPoint(0.0F, -1.0F, 0.0F);
		ear_left.addChild(ear_left_r1);
		setRotationAngle(ear_left_r1, 0.0F, 0.48F, 0.0F);
		ear_left_r1.cubeList.add(new ModelBox(ear_left_r1, 18, -6, 0.0F, -1.0F, 0.0F, 0, 4, 6, 0.0F, false));

		ear_right = new ModelRenderer(this);
		ear_right.setRotationPoint(-2.9503F, -3.0F, -1.9929F);
		head.addChild(ear_right);


		ear_right_r1 = new ModelRenderer(this);
		ear_right_r1.setRotationPoint(0.0F, -1.0F, 0.0F);
		ear_right.addChild(ear_right_r1);
		setRotationAngle(ear_right_r1, 0.0F, -0.48F, 0.0F);
		ear_right_r1.cubeList.add(new ModelBox(ear_right_r1, 18, -6, 0.0F, -1.0F, 0.0F, 0, 4, 6, 0.0F, true));

		leg_left = new ModelRenderer(this);
		leg_left.setRotationPoint(1.5497F, -5.0F, 0.0071F);
		body.addChild(leg_left);
		leg_left.cubeList.add(new ModelBox(leg_left, 0, 21, -1.5F, 0.0F, -2.0F, 3, 5, 3, 0.0F, false));

		leg_right = new ModelRenderer(this);
		leg_right.setRotationPoint(-1.4503F, -5.0F, 0.0071F);
		body.addChild(leg_right);
		leg_right.cubeList.add(new ModelBox(leg_right, 0, 21, -1.5F, 0.0F, -2.0F, 3, 5, 3, 0.0F, true));

		arm_left = new ModelRenderer(this);
		arm_left.setRotationPoint(3.0497F, -10.0F, 0.0071F);
		body.addChild(arm_left);
		arm_left.cubeList.add(new ModelBox(arm_left, 0, 12, 0.0F, -1.0F, -2.0F, 3, 6, 3, 0.0F, false));

		arm_right = new ModelRenderer(this);
		arm_right.setRotationPoint(-2.9503F, -10.0F, 0.0071F);
		body.addChild(arm_right);
		arm_right.cubeList.add(new ModelBox(arm_right, 0, 12, -3.0F, -1.0F, -2.0F, 3, 6, 3, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
