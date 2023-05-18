package gg.hipposgrumm.armor_trims.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class EntityLootModifier extends LootModifier {
    public static final Supplier<Codec<EntityLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).and(inst.group(
            ForgeRegistries.ITEMS.getCodec().fieldOf("addition").forGetter(m -> m.addition),
            Codec.INT.fieldOf("count").forGetter(m -> m.count),
            Codec.INT.fieldOf("chance").forGetter(m -> m.chance)
    )).apply(inst, EntityLootModifier::new)));

    private final Item addition;
    private final int count;
    private final int chance;

    protected EntityLootModifier(LootItemCondition[] conditionsIn, Item addition, int count, int chance) {
        super(conditionsIn);
        this.addition = addition;
        this.count = count;
        this.chance = chance;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (ThreadLocalRandom.current().nextInt(0,100) <= chance) {
            generatedLoot.add(new ItemStack(addition, count));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}