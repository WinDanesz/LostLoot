package com.windanesz.lostloot.entity;

import com.windanesz.lostloot.LostLoot;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityGoblin extends EntityTameable {

	// Goblins have 3 states:
	// 1. hostile - default, attacks player on sight (not owned)
	// 2. neutral - won't initiate combat (not owned)
	// 3. friendly - owned, follows player and fights for player (needs the player to keep holding a minecraft:stick)
	protected static final DataParameter<Boolean> NEUTRAL = EntityDataManager.createKey(EntityGoblin.class, DataSerializers.BOOLEAN);
	public static final ResourceLocation LOOT_TABLE = new ResourceLocation(LostLoot.MODID, "entities/goblin");

	private int ownershipLossTimer = 0;
	private int hostilityTimer = 0;

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
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true) {
			@Override
			public boolean shouldExecute() {
				return !EntityGoblin.this.isTamed() && !EntityGoblin.this.isNeutral() && super.shouldExecute();
			}

			@Override
			protected boolean isSuitableTarget(EntityLivingBase target, boolean ignoreDisabled) {
				if (!super.isSuitableTarget(target, ignoreDisabled)) {
					return false;
				}
				if (target instanceof EntityPlayer) {
					if (((EntityPlayer) target).isCreative() || ((EntityPlayer) target).isSpectator()) {
						return false;
					}
				}
				return !EntityGoblin.this.isOwner(target);
			}
		});
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
		this.dataManager.register(NEUTRAL, false);
	}

	public boolean isNeutral() {
		return this.dataManager.get(NEUTRAL);
	}

	public void setNeutral(boolean neutral) {
		this.dataManager.set(NEUTRAL, neutral);
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
		if (this.isOwner(entitylivingbaseIn)) {
			super.setAttackTarget(null);
			return;
		}
		super.setAttackTarget(entitylivingbaseIn);
	}


	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		}
		if (this.isOwner((EntityLivingBase) source.getTrueSource())) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue());
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		if (!this.isTamed() && itemstack.getItem() == Items.STICK) {
			if (!player.capabilities.isCreativeMode) {
				itemstack.shrink(1);
			}

			if (!this.world.isRemote) {
				this.setTamedBy(player);
				this.setNeutral(false);
				this.hostilityTimer = 0;
			}
			return true;
		}

		return super.processInteract(player, hand);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.world.isRemote) {
			return;
		}

		if (isTamed()) {
			EntityLivingBase owner = getOwner();
			boolean ownerMissingOrNotHoldingStick = true;
			if (owner instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) owner;
				if (player.isEntityAlive() && (player.getHeldItemMainhand().getItem() == Items.STICK || player.getHeldItemOffhand().getItem() == Items.STICK)) {
					ownerMissingOrNotHoldingStick = false;
				}
			}
			
			if (ownerMissingOrNotHoldingStick) {
				ownershipLossTimer++;
				if (ownershipLossTimer > 60) {
					setTamed(false);
					setOwnerId(null);
					setNeutral(true);
					ownershipLossTimer = 0;
					hostilityTimer = 0;
				}
			} else {
				ownershipLossTimer = 0;
			}
		} else if (isNeutral()) {
			hostilityTimer++;
			if (hostilityTimer > 60) {
				setNeutral(false);
				hostilityTimer = 0;
			}
		}
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
