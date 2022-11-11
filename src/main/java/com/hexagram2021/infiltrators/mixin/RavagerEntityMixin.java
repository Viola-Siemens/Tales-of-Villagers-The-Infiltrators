package com.hexagram2021.infiltrators.mixin;

import com.hexagram2021.infiltrators.common.entities.InfiltratorDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ravager.class)
public class RavagerEntityMixin {
	@Inject(method = "lambda$registerGoals$1", at = @At(value = "HEAD"), cancellable = true)
	private static void checkIsInfiltrator(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if(livingEntity instanceof Villager && ((InfiltratorDataHolder)livingEntity).isInfiltrator()) {
			cir.setReturnValue(Boolean.FALSE);
			cir.cancel();
		}
	}
}
