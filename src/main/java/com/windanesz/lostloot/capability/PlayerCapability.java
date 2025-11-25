package com.windanesz.lostloot.capability;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.network.PacketHandler;
import com.windanesz.lostloot.packet.PacketPlayerSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@Mod.EventBusSubscriber
public class PlayerCapability implements INBTSerializable<NBTTagCompound> {

	// This annotation does some crazy Forge magic behind the scenes and assigns this field a value.
	@CapabilityInject(PlayerCapability.class)
	private static final Capability<PlayerCapability> PLAYER_CAPABILITY = null;

	private final EntityPlayer player;

	public int getHauntingProgress() {
		return hauntingProgress;
	}

	public void setHauntingProgress(int hauntingProgress) {
		this.hauntingProgress = hauntingProgress;
	}

	public int hauntingProgress = 0;

	public PlayerCapability() {
		this(null); // Nullary constructor for the registration method factory parameter
	}

	public PlayerCapability(EntityPlayer player) {
		this.player = player;
	}

	/**
	 * Called from preInit
	 */
	public static void register() {

		CapabilityManager.INSTANCE.register(PlayerCapability.class, new IStorage<PlayerCapability>() {
			// Unused but necessary...
			@Override
			public NBTBase writeNBT(Capability<PlayerCapability> capability, PlayerCapability instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<PlayerCapability> capability, PlayerCapability instance, EnumFacing side, NBTBase nbt) {
			}

		}, PlayerCapability::new);
	}

	/**
	 * Returns the WizardData instance for the specified player.
	 */
	public static PlayerCapability get(EntityPlayer player) {
		return player.getCapability(PLAYER_CAPABILITY, null);
	}

	/**
	 * Called each time the associated player is updated.
	 */
	@SuppressWarnings("unchecked")
	private void update() {
	}

	/**
	 * Called from the event handler each time the associated player entity is cloned, i.e. on respawn or when
	 * travelling to a different dimension. Used to copy over any data that should persist over player death. This
	 * is the inverse of the old onPlayerDeath method, which reset the data that shouldn't persist.
	 *
	 * @param data    The old WizardData whose data is to be copied over.
	 * @param respawn True if the player died and is respawning, false if they are just travelling between dimensions.
	 */
	public void copyFrom(PlayerCapability data, boolean respawn) {
		this.hauntingProgress = data.hauntingProgress;
	}

	/**
	 * Sends a packet to this player's client to synchronise necessary information. Only called server side.
	 */
	public void sync() {
		if (this.player instanceof EntityPlayerMP) {
			IMessage msg = new PacketPlayerSync.Message(this.hauntingProgress);
			PacketHandler.net.sendTo(msg, (EntityPlayerMP) this.player);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public NBTTagCompound serializeNBT() {

		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger("hauntingProgress", hauntingProgress);
		return properties;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

		if (nbt != null) {
			this.hauntingProgress = nbt.getInteger("hauntingProgress");
		}
	}

	// ============================================== Event Handlers ==============================================

	@SubscribeEvent
	// The type parameter here has to be Entity, not EntityPlayer, or the event won't get fired.
	public static void onCapabilityLoad(AttachCapabilitiesEvent<Entity> event) {

		if (event.getObject() instanceof EntityPlayer)
			event.addCapability(new ResourceLocation(LostLoot.MODID, LostLoot.MODNAME + "Data"),
					new PlayerCapability.Provider((EntityPlayer) event.getObject()));
	}

	@SubscribeEvent
	public static void onPlayerCloneEvent(PlayerEvent.Clone event) {

		PlayerCapability newData = PlayerCapability.get(event.getEntityPlayer());
		PlayerCapability oldData = PlayerCapability.get(event.getOriginal());

		newData.copyFrom(oldData, event.isWasDeath());

		newData.sync();
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayerMP) {
			PlayerCapability data = PlayerCapability.get((EntityPlayer) event.getEntity());
			if (data != null) data.sync();
		}
	}

	@SubscribeEvent
	public static void onLivingUpdateEvent(LivingUpdateEvent event) {

		if (event.getEntityLiving() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntityLiving();

			if (PlayerCapability.get(player) != null) {
				PlayerCapability.get(player).update();
			}
		}
	}

	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private final PlayerCapability data;

		public Provider(EntityPlayer player) {
			data = new PlayerCapability(player);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == PLAYER_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

			if (capability == PLAYER_CAPABILITY) {
				return PLAYER_CAPABILITY.cast(data);
			}

			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return data.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			data.deserializeNBT(nbt);
		}

	}

}
