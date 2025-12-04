package com.windanesz.lostloot.entity;

import com.google.common.base.Optional;
import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.entity.ai.GoblinAIOwnerHurtByTarget;
import com.windanesz.lostloot.entity.ai.GoblinAIOwnerHurtTarget;
import com.windanesz.lostloot.entity.ai.GoblinAIFollowOwner;
import com.windanesz.lostloot.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityGoblin extends EntityMob implements IEntityOwnable {

	// Goblins have 3 states:
	// 1. hostile - default, attacks player on sight (not owned)
	// 2. neutral - won't initiate combat (not owned)
	// 3. friendly - owned, follows player and fights for player (needs the player to keep holding a minecraft:stick)
	protected static final DataParameter<Boolean> NEUTRAL = EntityDataManager.createKey(EntityGoblin.class, DataSerializers.BOOLEAN);
	public static final ResourceLocation LOOT_TABLE = new ResourceLocation(LostLoot.MODID, "entities/goblin");
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityGoblin.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private int ownershipLossTimer = 0;
	private int hostilityTimer = 0;

	public EntityGoblin(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 1F);
	}


	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIPanic(this, 2.0D));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.3D, false));
		this.tasks.addTask(5, new GoblinAIFollowOwner(this, 1.3D, 5.0F, 3.0F));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new GoblinAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new GoblinAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true) {
			@Override
			public boolean shouldExecute() {
				return !EntityGoblin.this.hasOwner() && !EntityGoblin.this.isNeutral() && super.shouldExecute();
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
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
	}

	public boolean isNeutral() {
		return this.dataManager.get(NEUTRAL);
	}

	public void setNeutral(boolean neutral) {
		this.dataManager.set(NEUTRAL, neutral);
	}

	public boolean isOwner(Entity entityIn) {
		return entityIn != null && entityIn.equals(this.getOwner());
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
		if (this.isOwner(source.getTrueSource())) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue());

		if (flag) {
			this.swingArm(EnumHand.MAIN_HAND);
		}
		return flag;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		// Interaction with stick no longer needed - goblins auto-ally based on active idol
		return super.processInteract(player, hand);
	}

	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if (this.getOwnerId() == null) {
			compound.setString("OwnerUUID", "");
		} else {
			compound.setString("OwnerUUID", this.getOwnerId().toString());
		}
	}

	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		String s;

		if (compound.hasKey("OwnerUUID", 8)) {
			s = compound.getString("OwnerUUID");
		} else {
			String s1 = compound.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
		}
		if (!s.isEmpty()) {
			this.setOwnerId(UUID.fromString(s));
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (hasOwner()) {
			EntityLivingBase owner = getOwner();
			boolean ownerMissingOrNotHoldingActiveIdol = true;
			if (owner instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) owner;
				if (player.isEntityAlive()) {
					// Check both hands for active goblin idol
					ItemStack mainHand = player.getHeldItemMainhand();
					ItemStack offHand = player.getHeldItemOffhand();
					
					if (isActiveGoblinIdol(mainHand) || isActiveGoblinIdol(offHand)) {
						ownerMissingOrNotHoldingActiveIdol = false;
					}
				}
			}

			if (ownerMissingOrNotHoldingActiveIdol) {
				ownershipLossTimer++;
				if (ownershipLossTimer > 20) { // Instant transition to neutral
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
			if (hostilityTimer > 60) { // 3 seconds (60 ticks) before becoming hostile
				setNeutral(false);
				hostilityTimer = 0;
				// Spawn angry particles
				if (this.world.isRemote) {
					for (int i = 0; i < 5; i++) {
						double d0 = this.rand.nextGaussian() * 0.02D;
						double d1 = this.rand.nextGaussian() * 0.02D;
						double d2 = this.rand.nextGaussian() * 0.02D;
						this.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY,
								this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width,
								this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height),
								this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width,
								d0, d1, d2);
					}
				}
			}
		} else {
			// Check if any nearby player is holding an active goblin idol
			EntityPlayer nearestPlayer = this.world.getClosestPlayerToEntity(this, 16.0D);
			if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
				ItemStack mainHand = nearestPlayer.getHeldItemMainhand();
				ItemStack offHand = nearestPlayer.getHeldItemOffhand();
				
				if (isActiveGoblinIdol(mainHand) || isActiveGoblinIdol(offHand)) {
					// Ally with this player
					this.setTamedBy(nearestPlayer);
					this.setNeutral(false);
					this.hostilityTimer = 0;
					this.setAttackTarget(null);
				}
			}
		}
	}

	private boolean isActiveGoblinIdol(ItemStack stack) {
		if (stack.getItem() == ModItems.goblin_idol) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("active")) {
				return stack.getTagCompound().getBoolean("active");
			}
		}
		return false;
	}

	public boolean hasOwner() {
		return this.getOwnerId() != null;
	}

	private void setTamedBy(EntityPlayer player) {
		this.setOwnerId(player.getUniqueID());
	}

	public void setOwnerId(@Nullable UUID p_184754_1_) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
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
		return 0.9F;
	}

	@Nullable
	public UUID getOwnerId() {
		return (UUID) ((Optional) this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}

	@Nullable
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
		if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast)) {
			if (target instanceof EntityWolf) {
				EntityWolf entitywolf = (EntityWolf) target;

				if (entitywolf.isTamed() && entitywolf.getOwner() == owner) {
					return false;
				}
			}

			if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target)) {
				return false;
			} else {
				return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
			}
		} else {
			return false;
		}
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(net.minecraft.world.DifficultyInstance difficulty) {
		super.setEquipmentBasedOnDifficulty(difficulty);
		
		if (this.rand.nextFloat() < 0.95F) {
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
		}
	}
}
