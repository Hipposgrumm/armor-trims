package dev.hipposgrumm.armor_trims.loot;

//? if fabric {
/*import dev.hipposgrumm.armor_trims.api.base.SmithingTemplateItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
//? if >=1.17 {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//?} else {
/^import net.minecraft.world.level.storage.loot.ConstantIntValue;
^///?}

import java.util.function.Supplier;

public class LootModifiers {
    @SuppressWarnings("unchecked")
    public static void register(ResourceLocation id, LootTable.Builder tableBuilder) {
        String bastion_common = id.toString().replace("minecraft:chests/bastion_","");
        String shipwreck_common = id.toString().replace("minecraft:chests/shipwreck_","");
        if (id.toString().equals("minecraft:entities/elder_guardian")) {
            add(tableBuilder, SmithingTemplateItems.TIDE_SMITHING_TEMPLATE.get(), 0.20f, 1);
        } else if (id.toString().equals("minecraft:chests/ancient_city")) {
            add(tableBuilder, SmithingTemplateItems.WARD_SMITHING_TEMPLATE.get(), 0.05f, 1);
            add(tableBuilder, SmithingTemplateItems.SILENCE_SMITHING_TEMPLATE.get(), 0.012f, 1);
        } else if (bastion_common.equals("bridge") || bastion_common.equals("hoglin_stable") || bastion_common.equals("treasure") || bastion_common.equals("other")) {
            add(tableBuilder, SmithingTemplateItems.NETHERITE_UPGRADE_SMITHING_TEMPLATE.get(), bastion_common.equals("treasure")?1f:0.10f, 1);
            add(tableBuilder, SmithingTemplateItems.SNOUT_SMITHING_TEMPLATE.get(), 0.083f, 1);
        } else if (id.toString().equals("minecraft:chests/desert_pyramid")) {
            add(tableBuilder, SmithingTemplateItems.DUNE_SMITHING_TEMPLATE.get(), 0.143f, 2);
        } else if (id.toString().equals("minecraft:chests/end_city_treasure")) {
            add(tableBuilder, SmithingTemplateItems.SPIRE_SMITHING_TEMPLATE.get(), 0.067f, 1);
        } else if (id.toString().equals("minecraft:chests/jungle_temple")) {
            add(tableBuilder, SmithingTemplateItems.WILD_SMITHING_TEMPLATE.get(), 0.333f, 1);
        } else if (id.toString().equals("minecraft:chests/nether_bridge")) {
            add(tableBuilder, SmithingTemplateItems.RIB_SMITHING_TEMPLATE.get(), 0.067f, 2);
        } else if (id.toString().equals("minecraft:chests/pillager_outpost")) {
            add(tableBuilder, SmithingTemplateItems.SENTRY_SMITHING_TEMPLATE.get(), 0.25f, 2);
        } else if (shipwreck_common.equals("map") || shipwreck_common.equals("supply") || shipwreck_common.equals("treasure")) {
            add(tableBuilder, SmithingTemplateItems.COAST_SMITHING_TEMPLATE.get(), 0.167f, 2);
        } else if (id.toString().equals("minecraft:chests/stronghold_corridor")) {
            add(tableBuilder, SmithingTemplateItems.EYE_SMITHING_TEMPLATE.get(), 0.10f, 1);
        } else if (id.toString().equals("minecraft:chests/stronghold_library")) {
            add(tableBuilder, SmithingTemplateItems.EYE_SMITHING_TEMPLATE.get(), 1f, 1);
        } else if (id.toString().equals("minecraft:chests/woodland_mansion")) {
            add(tableBuilder, SmithingTemplateItems.VEX_SMITHING_TEMPLATE.get(), 0.50f, 1);

        // These probably won't be seen but adding them anyway.
        } else if (id.toString().equals("minecraft:archeology/trail_ruins_rare")) {
            for (Supplier<Item> item:new Supplier[]{
                    SmithingTemplateItems.HOST_SMITHING_TEMPLATE,
                    SmithingTemplateItems.RAISER_SMITHING_TEMPLATE,
                    SmithingTemplateItems.SHAPER_SMITHING_TEMPLATE,
                    SmithingTemplateItems.WAYFINDER_SMITHING_TEMPLATE
            }) add(tableBuilder, item.get(), 0.083f, 1);
        } else if (id.toString().equals("minecraft:chests/trial_chambers/reward_unique")) {
            add(tableBuilder, SmithingTemplateItems.BOLT_SMITHING_TEMPLATE.get(), 0.054f, 1);
        } else if (id.toString().equals("minecraft:chests/trial_chambers/reward_ominous_unique")) {
            add(tableBuilder, SmithingTemplateItems.FLOW_SMITHING_TEMPLATE.get(), 0.225f, 1);
        }
    }

    private static void add(LootTable.Builder tableBuilder, Item item, float chance, int count) {
        //? if >=1.17 {
        tableBuilder.pool(new LootPool.Builder()
                .setRolls(ConstantValue.exactly(1))
                .conditionally(LootItemRandomChanceCondition.randomChance(chance).build())
                .with(LootItem.lootTableItem(item).build())
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)).build())
                .build()
        );
        //?} else {
        /^tableBuilder.withPool(new LootPool.Builder()
                .setRolls(ConstantIntValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(item))
                .apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(count)))
        );
        ^///?}
    }
}
*///?}