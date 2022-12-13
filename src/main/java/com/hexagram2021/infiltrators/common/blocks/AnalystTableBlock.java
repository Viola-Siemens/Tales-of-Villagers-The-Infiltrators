package com.hexagram2021.infiltrators.common.blocks;

import com.hexagram2021.infiltrators.common.blocks.entities.AnalystTableBlockEntity;
import com.hexagram2021.infiltrators.common.register.InfProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class AnalystTableBlock extends BaseEntityBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WRITTEN = InfProperties.WRITTEN;
	
	private static final VoxelShape SHAPE = Shapes.or(Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D), Block.box(0.0D, 4.0D, 0.0D, 16.0D, 6.0D, 16.0D));
	
	public AnalystTableBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(WRITTEN, Boolean.FALSE).setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos) {
		return blockState.getValue(WRITTEN) ? 1 : 0;
	}
	
	@Override @NotNull
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
	}
	@Override @NotNull
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
	}
	
	@Override @NotNull
	public RenderShape getRenderShape(@NotNull BlockState blockState) {
		return RenderShape.MODEL;
	}
	
	@Override @NotNull
	public VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
		return SHAPE;
	}
	
	@Override @Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		return new AnalystTableBlockEntity(blockPos, blockState);
	}
	
	@Override
	public void onRemove(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, BlockState newBlockState, boolean v) {
		if (!blockState.is(newBlockState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(blockPos);
			if (blockentity instanceof Container container) {
				Containers.dropContents(level, blockPos, container);
				level.updateNeighbourForOutputSignal(blockPos, this);
			}
			
			super.onRemove(blockState, level, blockPos, newBlockState, v);
		}
	}
	
	@Override
	public boolean isPathfindable(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos, @NotNull PathComputationType type) {
		return false;
	}
	
	@Override @NotNull
	public InteractionResult use(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player,
								 @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockEntity blockentity = level.getBlockEntity(blockPos);
		if(blockentity instanceof AnalystTableBlockEntity analystTableBlockEntity) {
			player.openMenu(analystTableBlockEntity);
		}
		
		return InteractionResult.CONSUME;
	}
}
