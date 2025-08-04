package dev.hipposgrumm.armor_trims.loot;

//? if forge {
import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
//? if <1.19
/*import net.minecraftforge.common.loot.GlobalLootModifierSerializer;*/
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

/// Tutorial by Kaupenjoe
public class ChestLootModifier extends LootModifier {
    private final Item addition;
    private final int count;
    private final float chance;

    protected ChestLootModifier(LootItemCondition[] conditionsIn, Item addition, int count, float chance) {
        super(conditionsIn);
        this.addition = addition;
        this.count = count;
        this.chance = chance/100f;
    }

    @Nonnull
    @Override
    protected /*? if >=1.19 {*/ObjectArrayList<ItemStack> doApply(ObjectArrayList/*?} else {*//*List<ItemStack> doApply(List*//*?}*/<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() <= chance) {
            generatedLoot.add(new ItemStack(addition, count));
        }
        return generatedLoot;
    }

    //? if >=1.19 {
    public static final Supplier<Codec<ChestLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).and(inst.group(
            ForgeRegistries.ITEMS.getCodec().fieldOf("addition").forGetter(m -> m.addition),
            Codec.INT.fieldOf("count").forGetter(m -> m.count),
            Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance)
    )).apply(inst, ChestLootModifier::new)));
    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
    //?} else {
    /*public static class Serializer extends GlobalLootModifierSerializer<ChestLootModifier> {
         @Override
         public ChestLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
             Item addition = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(object, "addition")));
             int count = GsonHelper.getAsInt(object, "count");
             float chance = GsonHelper.getAsFloat(object, "chance");
             return new ChestLootModifier(conditionsIn, addition, count, chance);
         }

         @Override
         public JsonObject write(ChestLootModifier instance) {
             JsonObject json = makeConditions(instance.conditions);
             json.addProperty("addition", ForgeRegistries.ITEMS.getKey(instance.addition).toString());
             json.addProperty("count", instance.count);
             json.addProperty("chance", instance.chance);
             return json;
         }
    }
    *///?}
}
//?}