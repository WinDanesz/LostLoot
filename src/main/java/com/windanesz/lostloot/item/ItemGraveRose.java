package com.windanesz.lostloot.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import com.windanesz.lostloot.entity.EntityFamiliarSpecter;

import net.minecraft.world.WorldServer;
import java.util.UUID;


import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import com.windanesz.lostloot.entity.EntityFamiliarSpecter;

import net.minecraft.world.WorldServer;
import java.util.UUID;

public class ItemGraveRose extends ItemBlock {
	public ItemGraveRose(Block block) {
		super(block);
		this.setMaxStackSize(1);
		this.setMaxDamage(100);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		// Only summon if a specter isn't already linked
		if (itemstack.getSubCompound("SpecterUUID") != null) {
			return new ActionResult<>(EnumActionResult.PASS, itemstack);
		}

		if (!worldIn.isRemote) {
			// Summon an EntityFamiliarSpecter for the player
			EntityFamiliarSpecter specter = new EntityFamiliarSpecter(worldIn);
			specter.setOwner(playerIn); // Set the player as the owner
			specter.setPosition(playerIn.posX, playerIn.posY + 1.0D, playerIn.posZ);
			worldIn.spawnEntity(specter);

			// Store the UUID of the specter in the itemstack
			itemstack.getOrCreateSubCompound("SpecterUUID").setString("UUID", specter.getUniqueID().toString());
		}

        // The item is not consumed, but its state is now linked to the specter
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, net.minecraft.entity.Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (!worldIn.isRemote && stack.getSubCompound("SpecterUUID") != null) {
			String uuidString = stack.getSubCompound("SpecterUUID").getString("UUID");
			if (uuidString.isEmpty()) {
				return;
			}

			UUID specterUUID = UUID.fromString(uuidString);
			
			// Cast worldIn to WorldServer to access getEntityFromUuid
			WorldServer worldServer = (WorldServer) worldIn;
			Entity specter = worldServer.getEntityFromUuid(specterUUID);

			if (specter instanceof EntityFamiliarSpecter) {
				EntityFamiliarSpecter familiar = (EntityFamiliarSpecter) specter;
				float healthPercent = familiar.getHealth() / familiar.getMaxHealth();
				int damage = 100 - (int) (healthPercent * 100);

				// Ensure durability does not fall below 2 (meaning damage does not exceed maxDamage - 2)
				if (damage > stack.getMaxDamage() - 2) {
					damage = stack.getMaxDamage() - 2;
				}
				stack.setItemDamage(damage);
			} else {
				// Specter is gone, don't destroy the item, but clear the UUID so it can summon again
				stack.removeSubCompound("SpecterUUID");
				stack.setItemDamage(stack.getMaxDamage() - 1); // Set to almost broken, but not fully
			}
		}
	}
}
