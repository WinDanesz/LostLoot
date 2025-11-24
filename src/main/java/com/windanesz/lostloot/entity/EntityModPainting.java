package com.windanesz.lostloot.entity;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

public class EntityModPainting extends Entity {

	protected static final DataParameter<Float> ROTATION = EntityDataManager.createKey(EntityModPainting.class, DataSerializers.FLOAT);
	protected static final DataParameter<String> PAINTING = EntityDataManager.createKey(EntityModPainting.class, DataSerializers.STRING);
	protected static final DataParameter<Integer> SIZE_X = EntityDataManager.createKey(EntityModPainting.class, DataSerializers.VARINT);
	protected static final DataParameter<Integer> SIZE_Y = EntityDataManager.createKey(EntityModPainting.class, DataSerializers.VARINT);

	private static final Predicate<Entity> IS_HANGING_ENTITY = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity p_apply_1_) {
			return p_apply_1_ instanceof EntityModPainting;
		}
	};
	private int tickCounter1;
	protected BlockPos hangingPosition;
	/**
	 * The direction the entity is facing
	 */
	@Nullable
	public EnumFacing facingDirection;

	public EntityModPainting(World worldIn) {
		super(worldIn);
		this.setSize(0.5F, 0.5F);
		this.facingDirection = EnumFacing.WEST;
	}

	public EntityModPainting(World worldIn, BlockPos hangingPositionIn, EnumFacing facing) {
		this(worldIn);
		this.hangingPosition = hangingPositionIn;
		this.facingDirection = EnumFacing.WEST;
		this.updateFacingWithBoundingBox(facing);
	}

	protected void entityInit() {
		this.dataManager.register(ROTATION, 0f);
		this.dataManager.register(PAINTING, "forest");
		this.dataManager.register(SIZE_X, 32);
		this.dataManager.register(SIZE_Y, 32);
	}

	/**
	 * Updates facing and bounding box based on it
	 */
	protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
		Validate.notNull(facingDirectionIn);
		Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
		this.facingDirection = facingDirectionIn;
		this.rotationYaw = (float) (this.facingDirection.getHorizontalIndex() * 90);
		this.prevRotationYaw = this.rotationYaw;
		this.updateBoundingBox();
	}

	/**
	 * Updates the entity bounding box based on current facing
	 */
	protected void updateBoundingBox() {
		if (this.facingDirection != null) {
			double d0 = (double) this.hangingPosition.getX() + 0.5D;
			double d1 = (double) this.hangingPosition.getY() + 0.5D;
			double d2 = (double) this.hangingPosition.getZ() + 0.5D;
			double d3 = 0.46875D;
			double d4 = this.offs(this.getWidthPixels());
			double d5 = this.offs(this.getHeightPixels());
			d0 = d0 - (double) this.facingDirection.getXOffset() * 0.46875D;
			d2 = d2 - (double) this.facingDirection.getZOffset() * 0.46875D;
			d1 = d1 + d5;
			EnumFacing enumfacing = this.facingDirection.rotateYCCW();
			d0 = d0 + d4 * (double) enumfacing.getXOffset();
			d2 = d2 + d4 * (double) enumfacing.getZOffset();
			this.posX = d0;
			this.posY = d1;
			this.posZ = d2;
			double d6 = (double) this.getWidthPixels();
			double d7 = (double) this.getHeightPixels();
			double d8 = (double) this.getWidthPixels();

			if (this.facingDirection.getAxis() == EnumFacing.Axis.Z) {
				d8 = 1.0D;
			} else {
				d6 = 1.0D;
			}

			d6 = d6 / 32.0D;
			d7 = d7 / 32.0D;
			d8 = d8 / 32.0D;
			this.setEntityBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
		}
	}

	private double offs(int p_190202_1_) {
		return p_190202_1_ % 32 == 0 ? 0.5D : 0.0D;
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		return super.getEntityBoundingBox();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.tickCounter1++ == 100 && !this.world.isRemote) {
			this.tickCounter1 = 0;

			if (!this.isDead && !this.onValidSurface()) {
				this.setDead();
				this.onBroken((Entity) null);
			}
		}
	}

	/**
	 * checks to make sure painting can be placed there
	 */
	public boolean onValidSurface() {
		//if (true) return true;
		if (!this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
			return false;
		} else {
			int i = Math.max(1, this.getWidthPixels() / 16);
			int j = Math.max(1, this.getHeightPixels() / 16);
			BlockPos blockpos = this.hangingPosition.offset(this.facingDirection.getOpposite());
			EnumFacing enumfacing = this.facingDirection.rotateYCCW();
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

			for (int k = 0; k < i; ++k) {
				for (int l = 0; l < j; ++l) {
					int i1 = (i - 1) / -2;
					int j1 = (j - 1) / -2;
					blockpos$mutableblockpos.setPos(blockpos).move(enumfacing, k + i1).move(EnumFacing.UP, l + j1);
					IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos);

					if (iblockstate.isSideSolid(this.world, blockpos$mutableblockpos, this.facingDirection))
						continue;

					if (!iblockstate.getMaterial().isSolid() && !BlockRedstoneDiode.isDiode(iblockstate)) {
						return false;
					}
				}
			}

			return this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), IS_HANGING_ENTITY).isEmpty();
		}
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith() {
		return true;
	}

	/**
	 * Called when a player attacks an entity. If this returns true the attack will not happen.
	 */
	public boolean hitByEntity(Entity entityIn) {
		return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) entityIn), 0.0F) : false;
	}

	/**
	 * Gets the horizontal facing direction of this Entity.
	 */
	public EnumFacing getHorizontalFacing() {
		return this.facingDirection;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else {
			if (!this.isDead && !this.world.isRemote) {
				this.setDead();
				this.markVelocityChanged();
				this.onBroken(source.getTrueSource());
			}

			return true;
		}
	}

	/**
	 * Tries to move the entity towards the specified location.
	 */
	public void move(MoverType type, double x, double y, double z) {
		if (!this.world.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D) {
			this.setDead();
			this.onBroken((Entity) null);
		}
	}

	/**
	 * Adds to the current velocity of the entity, and sets {@link #isAirBorne} to true.
	 */
	public void addVelocity(double x, double y, double z) {
		if (!this.world.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D) {
			this.setDead();
			this.onBroken((Entity) null);
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setByte("Facing", (byte) this.facingDirection.getHorizontalIndex());
		BlockPos blockpos = this.getHangingPosition();
		compound.setInteger("TileX", blockpos.getX());
		compound.setInteger("TileY", blockpos.getY());
		compound.setInteger("TileZ", blockpos.getZ());
		compound.setFloat("Rotation", this.getRotation());
		compound.setInteger("SizeX", this.getXSize());
		compound.setInteger("SizeY", this.getYSize());
		compound.setString("Painting", this.getPainting());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound compound) {
		this.hangingPosition = new BlockPos(compound.getInteger("TileX"), compound.getInteger("TileY"), compound.getInteger("TileZ"));
		this.updateFacingWithBoundingBox(EnumFacing.byHorizontalIndex(compound.getByte("Facing")));
		this.setProperties(compound.getFloat("Rotation"), compound.getInteger("SizeX"), compound.getInteger("SizeY"), compound.getString("Painting"));
	}

	public int getWidthPixels() {
		return 64;
	}

	public int getHeightPixels() {
		return 64;
	}

	public void onBroken(@Nullable Entity brokenEntity) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);

			if (brokenEntity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) brokenEntity;

				if (entityplayer.capabilities.isCreativeMode) {
					return;
				}
			}

			this.entityDropItem(new ItemStack(Items.PAINTING), 0.0F);
		}
	}

	public void playPlaceSound() {
		this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		BlockPos blockpos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
		this.setPosition((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ());
	}

	/**
	 * Drops an item at the position of the entity.
	 */
	public EntityItem entityDropItem(ItemStack stack, float offsetY) {
		EntityItem entityitem = new EntityItem(this.world, this.posX + (double) ((float) this.facingDirection.getXOffset() * 0.15F), this.posY + (double) offsetY, this.posZ + (double) ((float) this.facingDirection.getZOffset() * 0.15F), stack);
		entityitem.setDefaultPickupDelay();
		this.world.spawnEntity(entityitem);
		return entityitem;
	}

	protected boolean shouldSetPosAfterLoading() {
		return false;
	}

	/**
	 * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
	 */
	public void setPosition(double x, double y, double z) {
		this.hangingPosition = new BlockPos(x, y, z);
		this.updateBoundingBox();
		this.isAirBorne = true;
	}

	public BlockPos getHangingPosition() {
		return this.hangingPosition;
	}

	/**
	 * Transforms the entity's current yaw with the given Rotation and returns it. This does not have a side-effect.
	 */
	@SuppressWarnings("incomplete-switch")
	public float getRotatedYaw(Rotation transformRotation) {
		if (this.facingDirection != null && this.facingDirection.getAxis() != EnumFacing.Axis.Y) {
			switch (transformRotation) {
				case CLOCKWISE_180:
					this.facingDirection = this.facingDirection.getOpposite();
					break;
				case COUNTERCLOCKWISE_90:
					this.facingDirection = this.facingDirection.rotateYCCW();
					break;
				case CLOCKWISE_90:
					this.facingDirection = this.facingDirection.rotateY();
			}
		}

		float f = MathHelper.wrapDegrees(this.rotationYaw);

		switch (transformRotation) {
			case CLOCKWISE_180:
				return f + 180.0F;
			case COUNTERCLOCKWISE_90:
				return f + 90.0F;
			case CLOCKWISE_90:
				return f + 270.0F;
			default:
				return f;
		}
	}

	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.setPosition(x, y, z);
	}

	/**
	 * Transforms the entity's current yaw with the given Mirror and returns it. This does not have a side-effect.
	 */
	public float getMirroredYaw(Mirror transformMirror) {
		return this.getRotatedYaw(transformMirror.toRotation(this.facingDirection));
	}

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
	}

	public void setProperties(float rotation, int xSize, int ySize, String painting) {
		this.dataManager.set(ROTATION, rotation);
		this.dataManager.set(SIZE_X, xSize);
		this.dataManager.set(SIZE_Y, ySize);
		this.dataManager.set(PAINTING, painting);
	}

   public float getRotation() {
		return this.dataManager.get(ROTATION);
   }

   public int getXSize() {
		return this.dataManager.get(SIZE_X);
   }

   public int getYSize() {
		return this.dataManager.get(SIZE_Y);
   }

   public String getPainting() {
		return this.dataManager.get(PAINTING);
   }

}