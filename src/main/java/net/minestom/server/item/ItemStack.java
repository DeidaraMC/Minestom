package net.minestom.server.item;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.minestom.server.inventory.ContainerInventory;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

/**
 * Represents an immutable item to be placed inside {@link net.minestom.server.inventory.PlayerInventory},
 * {@link ContainerInventory} or even on the ground {@link net.minestom.server.entity.ItemEntity}.
 * <p>
 * An item stack cannot be null, {@link ItemStack#AIR} should be used instead.
 */
public sealed interface ItemStack extends TagReadable, ItemComponentMap, HoverEventSource<HoverEvent.ShowItem>
        permits ItemStackImpl {

    /**
     * Constant AIR item. Should be used instead of 'null'.
     */
    @NotNull ItemStack AIR = ItemStack.of(Material.AIR);

    @NotNull NetworkBuffer.Type<ItemStack> NETWORK_TYPE = ItemStackImpl.NETWORK_TYPE;

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Builder builder(@NotNull Material material) {
        return new ItemStackImpl.Builder(material, 1);
    }

    @Contract(value = "_ ,_ -> new", pure = true)
    static @NotNull ItemStack of(@NotNull Material material, int amount) {
        return ItemStackImpl.create(material, amount);
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull ItemStack of(@NotNull Material material) {
        return of(material, 1);
    }

    /**
     * Converts this item to an NBT tag containing the id (material), count (amount), and tag (meta).
     *
     * @param nbtCompound The nbt representation of the item
     */
    static @NotNull ItemStack fromItemNBT(@NotNull CompoundBinaryTag nbtCompound) {
        return ItemStackImpl.NBT_TYPE.read(nbtCompound);
    }

    @Contract(pure = true)
    @NotNull Material material();

    @Contract(pure = true)
    int amount();

    @Contract(value = "_, -> new", pure = true)
    @NotNull ItemStack with(@NotNull Consumer<@NotNull Builder> consumer);

    @Contract(value = "_, -> new", pure = true)
    @NotNull ItemStack withMaterial(@NotNull Material material);

    @Contract(value = "_, -> new", pure = true)
    @NotNull ItemStack withAmount(int amount);

    @Contract(value = "_, -> new", pure = true)
    default @NotNull ItemStack withAmount(@NotNull IntUnaryOperator intUnaryOperator) {
        return withAmount(intUnaryOperator.applyAsInt(amount()));
    }

    @Contract(value = "_, _ -> new", pure = true)
    <T> @NotNull ItemStack with(@NotNull ItemComponentType<T> component, T value);

    default <T> @NotNull ItemStack with(@NotNull ItemComponentType<T> component, @NotNull UnaryOperator<T> operator) {
        T value = get(component);
        if (value == null) return this;
        return with(component, operator.apply(value));
    }

    @Contract(value = "_, -> new", pure = true)
    @NotNull ItemStack without(@NotNull ItemComponentType<?> component);

    @Contract(value = "_, _ -> new", pure = true)
    default <T> @NotNull ItemStack withTag(@NotNull Tag<T> tag, @Nullable T value) {
        return with(ItemComponent.CUSTOM_DATA, get(ItemComponent.CUSTOM_DATA, CustomData.EMPTY).withTag(tag, value));
    }

    @Override
    @Contract(pure = true)
    default <T> @UnknownNullability T getTag(@NotNull Tag<T> tag) {
        return get(ItemComponent.CUSTOM_DATA, CustomData.EMPTY).getTag(tag);
    }

    @Contract(pure = true)
    default int maxStackSize() {
        return get(ItemComponent.MAX_STACK_SIZE, 64);
    }

    @Contract(value = "_, -> new", pure = true)
    @NotNull ItemStack consume(int amount);

    @Contract(pure = true)
    default boolean isAir() {
        return material() == Material.AIR;
    }

    @Contract(pure = true)
    boolean isSimilar(@NotNull ItemStack itemStack);

    /**
     * Converts this item to an NBT tag containing the id (material), count (amount), and components (diff)
     *
     * @return The nbt representation of the item
     */
    @NotNull CompoundBinaryTag toItemNBT();

    @Override
    default @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent(@NotNull UnaryOperator<HoverEvent.ShowItem> op) {
        //todo
//        try {
//            final BinaryTagHolder tagHolder = BinaryTagHolder.encode(meta().toNBT(), MinestomAdventure.NBT_CODEC);
//            return HoverEvent.showItem(op.apply(HoverEvent.ShowItem.showItem(material(), amount(), tagHolder)));
//        } catch (IOException e) {
//            //todo(matt): revisit,
//            throw new RuntimeException(e);
//        }
        throw new UnsupportedOperationException("todo");
    }

    sealed interface Builder extends TagWritable
            permits ItemStackImpl.Builder {

        @Contract(value = "_ -> this")
        @NotNull Builder amount(int amount);

        @Contract(value = "_, _ -> this")
        <T> @NotNull Builder set(@NotNull ItemComponentType<T> component, T value);

        @Contract(value = "_ -> this")
        @NotNull Builder remove(@NotNull ItemComponentType<?> component);

        @Contract(value = "_, _ -> this")
        default <T> @NotNull Builder set(@NotNull Tag<T> tag, @Nullable T value) {
            setTag(tag, value);
            return this;
        }

        @Contract(value = "-> new", pure = true)
        @NotNull ItemStack build();

    }
}
