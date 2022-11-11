package com.hexagram2021.infiltrators.common.register;

import com.hexagram2021.infiltrators.common.blocks.AnalystTableBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class InfBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	
	public static final BlockEntry<Block> ANALYST_TABLE = new BlockEntry<>(
			"analyst_table", () -> BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD), AnalystTableBlock::new
	);
	
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
	
	public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
		private final RegistryObject<T> regObject;
		private final Supplier<BlockBehaviour.Properties> properties;
		
		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
		}
		
		@Override
		public T get() {
			return regObject.get();
		}
		
		public BlockState defaultBlockState() {
			return get().defaultBlockState();
		}
		
		public ResourceLocation getId() {
			return regObject.getId();
		}
		
		public BlockBehaviour.Properties getProperties() {
			return properties.get();
		}
		
		@Override @NotNull
		public Item asItem() {
			return get().asItem();
		}
	}
}
