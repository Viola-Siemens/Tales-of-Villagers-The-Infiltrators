package com.hexagram2021.infiltrators.mixin;

import com.hexagram2021.infiltrators.common.entity.ai.behaviors.InfiltratorDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractIllager.class)
public class AbstractIllagerEntityMixin {
	@Inject(method = "canAttack", at = @At(value = "HEAD"), cancellable = true)
	public void cannotAttackInfiltrators(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if(livingEntity instanceof Villager && ((InfiltratorDataHolder)livingEntity).isInfiltrator()) {
			cir.setReturnValue(Boolean.FALSE);
			cir.cancel();
		}
	}
}
