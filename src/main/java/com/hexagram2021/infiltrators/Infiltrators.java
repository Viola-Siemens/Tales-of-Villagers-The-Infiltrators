package com.hexagram2021.infiltrators;

import com.hexagram2021.infiltrators.client.ClientProxy;
import com.hexagram2021.infiltrators.common.InfContent;
import com.hexagram2021.infiltrators.common.config.InfCommonConfig;
import com.hexagram2021.infiltrators.common.register.InfItems;
import com.hexagram2021.infiltrators.common.register.InfTriggers;
import com.hexagram2021.infiltrators.common.world.village.Village;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(Infiltrators.MODID)
public class Infiltrators {
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static final String MODID = "infiltrators";
    
    public static <T>
    Supplier<T> bootstrapErrorToXCPInDev(Supplier<T> in) {
        if(FMLLoader.isProduction()) {
            return in;
        }
        return () -> {
            try {
                return in.get();
            } catch(BootstrapMethodError e) {
                throw new RuntimeException(e);
            }
        };
    }

    public Infiltrators() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    
        DeferredWorkQueue queue = DeferredWorkQueue.lookup(Optional.of(ModLoadingStage.CONSTRUCT)).orElseThrow();
        Consumer<Runnable> runLater = job -> queue.enqueueWork(
                ModLoadingContext.get().getActiveContainer(), job
        );
        InfContent.modConstruction(bus, runLater);

        bus.addListener(this::creativeTabEvent);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, bootstrapErrorToXCPInDev(() -> ClientProxy::modConstruction));
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, InfCommonConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // preinit
        InfTriggers.init();
        
        event.enqueueWork(InfContent::init);
        event.enqueueWork(Village::init);
    }
    
    public static CreativeModeTab ITEM_GROUP;

    public void creativeTabEvent(CreativeModeTabEvent.Register event) {
        ITEM_GROUP = event.registerCreativeModeTab(new ResourceLocation(MODID, "item_group"), builder -> builder
                .icon(() -> new ItemStack(InfItems.SEER_BOOK::get))
                .title(Component.translatable("itemGroup.infiltrators")).displayItems(
                        (flags, output) -> InfItems.ItemEntry.REGISTERED_ITEMS.forEach(output::accept)
                ));
    }
}
