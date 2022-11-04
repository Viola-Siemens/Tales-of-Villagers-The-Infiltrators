package com.hexagram2021.infiltrators.mixin;

import com.hexagram2021.infiltrators.common.entity.InfiltratorDataHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.YieldJobSite;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(YieldJobSite.class)
public class YieldJobSiteMixin {
	@Inject(method = "nearbyWantsJobsite", at = @At(value = "HEAD"), cancellable = true)
	private void ignoreInfiltrator(PoiType poiType, Villager other, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		if(((InfiltratorDataHolder)other).isInfiltrator()) {
			cir.setReturnValue(Boolean.FALSE);
			cir.cancel();
		}
	}
}
