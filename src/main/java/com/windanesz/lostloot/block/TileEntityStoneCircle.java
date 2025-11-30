package com.windanesz.lostloot.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityStoneCircle extends TileEntity implements ITickable {

	// For channeling
	private UUID channelingPlayer = null;
	private BlockPos pair = null;
	private int channelProgress = 0;

	@Override
	public void update() {
	}

	public BlockPos getPair() {
		return pair;
	}

	public void setPair(BlockPos pair) {
		this.pair = pair;
		markDirty();
	}

	public UUID getChannelingPlayer() {
		return channelingPlayer;
	}

	public void setChannelingPlayer(UUID channelingPlayer) {
		this.channelingPlayer = channelingPlayer;
		markDirty();
	}

	public int getChannelProgress() {
		return channelProgress;
	}

	public void setChannelProgress(int channelProgress) {
		this.channelProgress = channelProgress;
		markDirty();
	}

	public void incrementChannelProgress() {
		incrementChannelProgress(1);
	}

	public void incrementChannelProgress(int amount) {
		this.channelProgress += amount;
		markDirty();
	}

	public void resetChanneling() {
		this.channelingPlayer = null;
		this.channelProgress = 0;
		markDirty();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		if (this.pair != null) {
			compound.setTag("Pair", NBTUtil.createPosTag(this.pair));
		}

		if (this.channelingPlayer != null) {
			compound.setString("ChannelingPlayer", this.channelingPlayer.toString());
		}
		compound.setInteger("ChannelProgress", this.channelProgress);

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("Pair")) {
			this.pair = NBTUtil.getPosFromTag(compound.getCompoundTag("Pair"));
		}

		if (compound.hasKey("ChannelingPlayer")) {
			this.channelingPlayer = UUID.fromString(compound.getString("ChannelingPlayer"));
		}
		this.channelProgress = compound.getInteger("ChannelProgress");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}
