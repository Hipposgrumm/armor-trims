package gg.hipposgrumm.armor_trims.loot;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Original Code by Kaupenjoe
 */
public class ChestLootModifier extends LootModifier {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Item addition;
    private final int count;
    private final int chance;

    protected ChestLootModifier(LootItemCondition[] conditionsIn, Item addition, int count, int chance) {
        super(conditionsIn);
        this.addition = addition;
        this.count = count;
        this.chance = chance;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextInt(0,100) <= chance) {
            generatedLoot.add(new ItemStack(addition, count));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ChestLootModifier> {
         @Override
         public ChestLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
             Item addition = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(object, "addition")));
             int count = GsonHelper.getAsInt(object, "count");
             int chance = GsonHelper.getAsInt(object, "chance");
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
}