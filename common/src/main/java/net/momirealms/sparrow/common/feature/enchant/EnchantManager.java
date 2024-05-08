package net.momirealms.sparrow.common.feature.enchant;

public interface EnchantManager<T> {

    String SHELVES_META_KEY = "sparrow_shelves";
    String SHELVES_FLAG_PERMISSION = "sparrow.command.player.enchantmenttable.flag.shelves";

    void openEnchantmentTable(T player, int shelves);
}
