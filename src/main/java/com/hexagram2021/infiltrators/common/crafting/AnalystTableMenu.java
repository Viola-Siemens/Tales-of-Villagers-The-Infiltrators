package com.hexagram2021.infiltrators.common.crafting;

import com.hexagram2021.infiltrators.common.blocks.entities.AnalystTableBlockEntity;
import com.hexagram2021.infiltrators.common.register.InfMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AnalystTableMenu extends AbstractContainerMenu {
	private final Container analystTable;
	private final Level level;
	
	public final Container inputSlots = new SimpleContainer(2) {
		public void setChanged() {
			super.setChanged();
			AnalystTableMenu.this.slotsChanged(this);
		}
	};
	private final ResultContainer resultSlots = new ResultContainer();
	
	final Slot[] inputSlot = new Slot[2];
	final Slot resultSlot;
	long lastSoundTime;
	
	public AnalystTableMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(AnalystTableBlockEntity.CONTAINER_SIZE));
	}
	
	public AnalystTableMenu(int id, Inventory inventory, Container container) {
		super(InfMenuTypes.ANALYST_TABLE_MENU.get(), id);
		this.analystTable = container;
		this.level = inventory.player.level;
		
		this.inputSlot[0] = this.addSlot(new Slot(this.inputSlots, 0, , ) {
			@Override
			public boolean mayPlace(@NotNull ItemStack itemStack) {
				return itemStack.is(Items.NAME_TAG);
			}
		});
		this.inputSlot[1] = this.addSlot(new Slot(this.inputSlots, 1, , ));
		this.resultSlot = this.addSlot(new Slot(this.resultSlots, 2, , ) {
			@Override
			public boolean mayPlace(@NotNull ItemStack itemStack) {
				return false;
			}
			
			@Override
			public void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
				ItemStack input0 = AnalystTableMenu.this.inputSlot[0].remove(1);
				ItemStack input1 = AnalystTableMenu.this.inputSlot[1].remove(1);
				if (!input0.isEmpty() && !input1.isEmpty()) {
					AnalystTableMenu.this.setupResultSlot();
				}
				
				long l = player.level.getGameTime();
				if (AnalystTableMenu.this.lastSoundTime != l) {
					player.level.playSound(null, new BlockPos(player.position()), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
					AnalystTableMenu.this.lastSoundTime = l;
				}
				super.onTake(player, itemStack);
			}
		});
		
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(container, i * 3 + j, , ));
			}
		}
		
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(container, i * 3 + j + 9, , ));
			}
		}
		
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
		}
	}
	
	@Override
	public boolean stillValid(@NotNull Player player) {
		return this.analystTable.stillValid(player);
	}
	
	@Override @NotNull
	public ItemStack quickMoveStack(@NotNull Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < AnalystTableBlockEntity.CONTAINER_SIZE) {
				if (!this.moveItemStackTo(itemstack1, AnalystTableBlockEntity.CONTAINER_SIZE, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, AnalystTableBlockEntity.CONTAINER_SIZE, false)) {
				return ItemStack.EMPTY;
			}
			
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		
		return itemstack;
	}
	
	public void removed(@NotNull Player player) {
		super.removed(player);
		this.analystTable.stopOpen(player);
	}
}
