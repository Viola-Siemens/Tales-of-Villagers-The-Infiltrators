package com.hexagram2021.infiltrators.common.blocks.entities;

import com.hexagram2021.infiltrators.common.blocks.AnalystTableBlock;
import com.hexagram2021.infiltrators.common.crafting.AnalystTableMenu;
import com.hexagram2021.infiltrators.common.register.InfBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AnalystTableBlockEntity extends BaseContainerBlockEntity {
	public static final int CONTAINER_SIZE = 18;
	
	private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	
	public AnalystTableBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(InfBlockEntities.ANALYST_TABLE.get(), blockPos, blockState);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override @NotNull
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}
	
	@Override @NotNull
	protected Component getDefaultName() {
		return Component.translatable("block.infiltrators.analyst_table");
	}
	
	@Override @NotNull
	public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
		return new AnalystTableMenu(id, inventory, this);
	}
	
	@Override
	public int getContainerSize() {
		return this.items.size();
	}
	
	@Override
	public boolean isEmpty() {
		for(ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isWritten() {
		return !this.isEmpty();
	}
	
	@Override @NotNull
	public ItemStack getItem(int index) {
		return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
	}
	
	@Override @NotNull
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.items, index, count);
	}
	
	@Override @NotNull
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.items, index);
	}
	
	@Override
	public void setItem(int index, @NotNull ItemStack itemStack) {
		if (index >= 0 && index < this.items.size()) {
			this.items.set(index, itemStack);
		}
	}
	
	@Override
	public boolean stillValid(@NotNull Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
	}
	
	@Override
	public void clearContent() {
		this.items.clear();
	}
	
	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, AnalystTableBlockEntity blockEntity) {
		if(blockEntity.isWritten() != blockState.getValue(AnalystTableBlock.WRITTEN)) {
			level.setBlock(blockPos, blockState.setValue(AnalystTableBlock.WRITTEN, blockEntity.isWritten()), Block.UPDATE_ALL);
		}
	}
}
