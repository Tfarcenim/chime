package dev.emi.chime.mixin;

import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelsProperties.class)
public abstract class ModelPredicateProviderRegistryMixin {

	@Shadow
	private static IItemPropertyGetter registerGlobalProperty(ResourceLocation id, IItemPropertyGetter provider) {
		throw new RuntimeException("unimplemented");
	}
	
	@Inject(at = @At("TAIL"), method = "<clinit>")
	private static void clinit(CallbackInfo info) {
		registerGlobalProperty(new ResourceLocation("count"), (stack, world, entity) -> stack.getCount());
	}
}
