package com.windanesz.lostloot.entity;

import com.google.common.base.Predicate;
import com.windanesz.lostloot.LostLoot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class EntitySpecter extends EntityMob {

    protected static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntitySpecter.class, DataSerializers.BOOLEAN);
    public static final ResourceLocation LOOT_TABLE = new ResourceLocation(LostLoot.MOD_ID, "entities/specter");

    public EntitySpecter(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.8F);
        this.isImmuneToFire = true;
        this.moveHelper = new SpecterMoveHelper(this);
        this.setNoGravity(true);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(4, new AIAttack(this));
        this.tasks.addTask(5, new AIRandomFly(this));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new AIFindPlayerToAttack(this));
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
    }

    public void setAttacking(boolean attacking) {
        this.dataManager.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.dataManager.get(ATTACKING);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() instanceof EntityPlayer) {
            return super.attackEntityFrom(source, amount);
        }
        if (source.isProjectile() || source.isMagicDamage()) {
            return super.attackEntityFrom(source, amount);
        }
        if (source == DamageSource.IN_WALL) {
            return false;
        }
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue());
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.noClip = true;
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LOOT_TABLE;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.world.checkNoEntityCollision(this.getEntityBoundingBox())
                && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()
                && !this.world.containsAnyLiquid(this.getEntityBoundingBox());
    }

    @Override
    public int getTalkInterval() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Override
    public boolean isEntityUndead() {
        return true;
    }

    @Override
    public float getEyeHeight() {
        return 1.6F;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState landedState, BlockPos pos) {
    }

    static class AIAttack extends EntityAIBase {
        private final EntitySpecter parentEntity;
        
        private enum State { CHARGING, DASHING, CIRCLING }
        private State currentState = State.CHARGING;

        private int chargeCooldown;
        private int dashTimer;
        private int circlingTimer;

        public AIAttack(EntitySpecter specter) {
            this.parentEntity = specter;
            this.setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return this.parentEntity.getAttackTarget() != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.parentEntity.getAttackTarget() != null && this.parentEntity.getAttackTarget().isEntityAlive();
        }

        @Override
        public void startExecuting() {
            this.parentEntity.setAttacking(true);
            this.currentState = State.CHARGING;
            this.chargeCooldown = 0;
            this.dashTimer = 0;
            this.circlingTimer = 0;
        }

        @Override
        public void resetTask() {
            this.parentEntity.setAttacking(false);
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = this.parentEntity.getAttackTarget();
            if (target == null) {
                return;
            }

            if (this.chargeCooldown > 0) {
                --this.chargeCooldown;
            }

            switch (this.currentState) {
                case DASHING:
                    --this.dashTimer;
                    if (this.dashTimer <= 0) {
                        this.currentState = State.CIRCLING;
                        this.circlingTimer = 40 + this.parentEntity.getRNG().nextInt(40); // 2-4 seconds
                    }
                    break;
                case CIRCLING:
                    --this.circlingTimer;
                    if (this.circlingTimer <= 0) {
                        this.currentState = State.CHARGING;
                    } else {
                        if (!this.parentEntity.getMoveHelper().isUpdating()) {
                            double circleRadius = 5.0;
                            double angleOffset = this.parentEntity.getRNG().nextBoolean() ? 90 : -90;
                            
                            double vecX = this.parentEntity.posX - target.posX;
                            double vecZ = this.parentEntity.posZ - target.posZ;
                            double currentAngle = Math.toDegrees(Math.atan2(vecZ, vecX));
                            double newAngle = Math.toRadians(currentAngle + angleOffset);

                            double destX = target.posX + Math.cos(newAngle) * circleRadius;
                            double destZ = target.posZ + Math.sin(newAngle) * circleRadius;
                            this.parentEntity.getMoveHelper().setMoveTo(destX, target.posY, destZ, 0.8D);
                        }
                    }
                    break;
                case CHARGING:
                    this.parentEntity.getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
                    this.parentEntity.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);

                    double distanceSq = this.parentEntity.getDistanceSq(target);
                    if (distanceSq < 4.0D && this.chargeCooldown <= 0) {
                        this.parentEntity.attackEntityAsMob(target);
                        this.chargeCooldown = 40;
                        
                        this.currentState = State.DASHING;
                        this.dashTimer = 20;

                        Random random = this.parentEntity.getRNG();
                        double dashVecX = this.parentEntity.posX - target.posX;
                        double dashVecZ = this.parentEntity.posZ - target.posZ;
                        double len = MathHelper.sqrt(dashVecX * dashVecX + dashVecZ * dashVecZ);

                        if (len > 0) {
                            double perpX = -dashVecZ / len;
                            double perpZ = dashVecX / len;

                            if (random.nextBoolean()) {
                                perpX = -perpX;
                                perpZ = -perpZ;
                            }

                            double strafeDist = 5.0 + random.nextDouble() * 2.0;
                            
                            double destX = this.parentEntity.posX + perpX * strafeDist;
                            double destY = this.parentEntity.posY + (random.nextDouble() - 0.5D) * 2.0D;
                            double destZ = this.parentEntity.posZ + perpZ * strafeDist;
                            
                            double moveVecX = destX - this.parentEntity.posX;
                            double moveVecY = destY - this.parentEntity.posY;
                            double moveVecZ = destZ - this.parentEntity.posZ;
                            double moveLen = MathHelper.sqrt(moveVecX*moveVecX + moveVecY*moveVecY + moveVecZ*moveVecZ);

                            double dashSpeed = 1.8;
                            this.parentEntity.motionX = (moveVecX / moveLen) * dashSpeed;
                            this.parentEntity.motionY = (moveVecY / moveLen) * dashSpeed;
                            this.parentEntity.motionZ = (moveVecZ / moveLen) * dashSpeed;

                            if (this.parentEntity.getMoveHelper() instanceof SpecterMoveHelper) {
                                ((SpecterMoveHelper) this.parentEntity.getMoveHelper()).startCooldown(10);
                            }
                        }
                    }
                    break;
            }
        }
    }

    static class AIFindPlayerToAttack extends EntityAINearestAttackableTarget<EntityPlayer> {
        public AIFindPlayerToAttack(EntityMob mob) {
            super(mob, EntityPlayer.class, true);
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && this.target != null;
        }
    }

    static class AIRandomFly extends EntityAIBase {
        private final EntitySpecter parentEntity;

        public AIRandomFly(EntitySpecter specter) {
            this.parentEntity = specter;
            this.setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();
            if (!entitymovehelper.isUpdating()) {
                return true;
            } else {
                double d0 = entitymovehelper.getX() - this.parentEntity.posX;
                double d1 = entitymovehelper.getY() - this.parentEntity.posY;
                double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return false;
        }

        @Override
        public void startExecuting() {
            Random random = this.parentEntity.getRNG();
            double d0 = this.parentEntity.posX + (double) ((random.nextFloat() * 2.0F - 1.0F) * 4.0F);
            double d1 = this.parentEntity.posY + (double) ((random.nextFloat() * 2.0F - 1.0F) * 4.0F);
            double d2 = this.parentEntity.posZ + (double) ((random.nextFloat() * 2.0F - 1.0F) * 4.0F);
            this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
        }
    }

    static class SpecterMoveHelper extends EntityMoveHelper {
        private final EntitySpecter parentEntity;
        private int cooldown;

        public SpecterMoveHelper(EntitySpecter specter) {
            super(specter);
            this.parentEntity = specter;
        }

        public void startCooldown(int ticks) {
            this.cooldown = ticks;
        }

        @Override
        public void onUpdateMoveHelper() {
            if (this.cooldown > 0) {
                --this.cooldown;
                return;
            }

            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - this.parentEntity.posX;
                double d1 = this.posY - this.parentEntity.posY;
                double d2 = this.posZ - this.parentEntity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 < 0.1D) {
                    this.action = Action.WAIT;
                    this.parentEntity.motionX = 0.0D;
                    this.parentEntity.motionY = 0.0D;
                    this.parentEntity.motionZ = 0.0D;
                    return;
                }

                d3 = (double) MathHelper.sqrt(d3);

                d0 /= d3;
                d1 /= d3;
                d2 /= d3;

                float speed = (float) (this.speed * this.parentEntity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());

                this.parentEntity.motionX = d0 * speed;
                this.parentEntity.motionY = d1 * speed;
                this.parentEntity.motionZ = d2 * speed;
                
                float f = (float) (MathHelper.atan2(this.parentEntity.motionZ, this.parentEntity.motionX) * (180D / Math.PI)) - 90.0F;
                this.parentEntity.rotationYaw = this.limitAngle(this.parentEntity.rotationYaw, f, 90.0F);
                this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;

            } else {
                this.parentEntity.motionX *= 0.5D;
                this.parentEntity.motionY *= 0.5D;
                this.parentEntity.motionZ *= 0.5D;
            }
        }
    }
}
