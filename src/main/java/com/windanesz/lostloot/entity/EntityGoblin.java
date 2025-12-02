package com.windanesz.lostloot.entity;

import com.windanesz.lostloot.LostLoot;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityGoblin extends EntityTameable implements IEntityOwnable {

	// TODO:
	// Goblins have 3 states:
	// 1. hostile - default, attacks player on sight (not owned)
	// 2. neutral - won't initiate combat (not owned)
	// 3. friendly - owned, follows player and fights for player (needs the player to keep holding a minecraft:stick)
	protected static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityGoblin.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<String> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityGoblin.class, DataSerializers.STRING);
	protected static final DataParameter<String> OWNER_NAME = EntityDataManager.createKey(EntityGoblin.class, DataSerializers.STRING);
	public static final ResourceLocation LOOT_TABLE = new ResourceLocation(LostLoot.MODID, "entities/goblin");

	public EntityGoblin(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 1.8F);
	}

	@Nullable
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIPanic(this, 2.0D));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 0.5D, false));
		this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 5.0F, 3.0F));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ATTACKING, false);
		this.dataManager.register(OWNER_UNIQUE_ID, "");
		this.dataManager.register(OWNER_NAME, "");
	}

	@Override
	public UUID getOwnerId() {
		try {
			return UUID.fromString(this.dataManager.get(OWNER_UNIQUE_ID));
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	public void setOwnerId(@Nullable UUID ownerId) {
		this.dataManager.set(OWNER_UNIQUE_ID, ownerId == null ? "" : ownerId.toString());
	}

	@Nullable
	@Override
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	public void setAttacking(boolean attacking) {
		this.dataManager.set(ATTACKING, attacking);
	}

	public boolean isAttacking() {
		return this.dataManager.get(ATTACKING);
	}

	public boolean isOwner(Entity entityIn) {
		return entityIn == this.getOwner();
	}

	@Nullable
	public String getOwnerName() {
		return this.dataManager.get(OWNER_NAME);
	}

	public void setOwner(@Nullable EntityPlayer player) {
		this.setOwnerId(player == null ? null : player.getUniqueID());
		this.dataManager.set(OWNER_NAME, player == null ? "" : player.getName());
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
		if (this.getOwner() != null && entitylivingbaseIn != null && this.getOwner().equals(entitylivingbaseIn)) {
			super.setAttackTarget(null);
			return;
		}
		super.setAttackTarget(entitylivingbaseIn);
	}


	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isOwner(source.getTrueSource())) {
			return false;
		}
		if (source == DamageSource.IN_WALL) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue());
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		// TODO: Ownership depends on whether the player holds a minecraft:stick
		// if no stick is held, ownership is lost after 60 ticks - and turns into neutral and loses ownership
		// after 60 more ticks, the goblin becomes hostile

	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}


	@Override
	public int getTalkInterval() {
		return 160;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_VILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_VILLAGER_HURT;
	}

	@Override
	public float getEyeHeight() {
		return 0.5F;
	}
}
