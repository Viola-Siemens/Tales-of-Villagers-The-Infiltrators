package com.hexagram2021.infiltrators.common.blocks;

import com.hexagram2021.infiltrators.common.blocks.entities.AnalystTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnalystTableBlock extends BaseEntityBlock {
	public AnalystTableBlock(Properties props) {
		super(props);
	}
	
	@Override @Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		return new AnalystTableBlockEntity(blockPos, blockState);
	}
}
