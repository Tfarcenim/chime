package dev.emi.chime.mixin;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import dev.emi.chime.ChimeMain;
import dev.emi.chime.ModelOverrideWrapper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemOverride.Deserializer.class)
public class ModelOverrideDeserializerMixin {
	private Map<String, Object> customPredicates;
	
	@Inject(at = @At("RETURN"), method = "deserialize")
	public void deserialize(JsonElement element, Type type, JsonDeserializationContext context, CallbackInfoReturnable<ItemOverride> info) throws JsonParseException {
		((ModelOverrideWrapper) info.getReturnValue()).setCustomPredicates(customPredicates);
	}

	@Inject(at = @At("HEAD"), method = "makeMapResourceValues")
	private void deserializeMinPropertyValues(JsonObject object, CallbackInfoReturnable<Map<ResourceLocation, Float>> info) {
		customPredicates = Maps.newHashMap();
		JsonObject pred = object.getAsJsonObject("predicate");
		parseCustomPredicates(pred, "");
	}

	private void parseCustomPredicates(JsonObject pred, String path) {
		List<String> toRemove = Lists.newArrayList();
		for (Map.Entry<String, JsonElement> entry : pred.entrySet()) {
			String newPath = entry.getKey();
			if (path.length() > 0) {
				newPath = path + "/" + newPath;
			}
			if (entry.getValue().isJsonObject() && !entry.getKey().equals("nbt")) {
				parseCustomPredicates(entry.getValue().getAsJsonObject(), newPath);
				if (path.length() == 0) {
					toRemove.add(entry.getKey());
				}
			} else {
				if (ChimeMain.CUSTOM_MODEL_PREDICATES.containsKey(newPath)) {
					customPredicates.put(newPath, ChimeMain.CUSTOM_MODEL_PREDICATES.get(newPath).parseType(entry.getValue()));
					if (path.length() == 0) {
						toRemove.add(entry.getKey());
					}
				}
			}
		}
		for (String s : toRemove) {
			pred.remove(s);
		}
	}
}
