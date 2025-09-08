package com.ia.common.utilities.helper.collection;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An ArrayList with grouping and filtering capabilities based on a key provider function.
 *
 * @author Martin Blaise Signe
 * @param <K> the type of keys used for grouping
 * @param <E> the type of elements in the list
 */
public class GroupingArrayList<K, E> extends ArrayList<E> {

    private final Predicate<List<E>> multiplePredicate = list -> list.size() > 1;
    private final Predicate<List<E>> singlePredicate = list -> list.size() == 1;

    @Getter
    @Setter
    private Function<E, K> keyProvider;

    private GroupingArrayList(List<E> items, Function<E, K> keyProvider) {
        super(items);
        this.keyProvider = keyProvider;
    }

    /***
     * Creates a new GroupingArrayList with the specified items and key provider function.
     *
     * @param items       the initial items to be added to the list
     * @param keyProvider a function that provides the key for each item
     * @param <U>         the type of keys used for grouping
     * @param <V>         the type of elements in the list
     * @return a new GroupingArrayList instance
     */
    public static <U, V> GroupingArrayList<U, V> create(List<V> items, Function<V, U> keyProvider) {
        return new GroupingArrayList<>(List.copyOf(items), keyProvider);
    }

    /***
     * Creates an empty GroupingArrayList with the specified key provider function.
     *
     * @param keyProvider a function that provides the key for each item
     * @param <U>         the type of keys used for grouping
     * @param <V>         the type of elements in the list
     * @return a new empty GroupingArrayList instance
     */
    public static <U, V> GroupingArrayList<U, V> create(Function<V, U> keyProvider) {
        return new GroupingArrayList<>(new ArrayList<>(), keyProvider);
    }

    /***
     * Groups duplicate items in the list based on the key provider function.
     *
     * @return a map where each key maps to a list of duplicate items
     */
    public Map<K, List<E>> duplicateToMap() {
        return getBranch(multiplePredicate).stream().collect(Collectors.groupingBy(keyProvider));
    }

    /***
     * Retrieves a list of all duplicate items in the list based on the key provider function.
     *
     * @return a list of duplicate items
     */
    public List<E> duplicatesToList() {
        return duplicateToMap().values().stream().flatMap(List::stream).toList();
    }

    /***
     * Retrieves a list of all unique items in the list based on the key provider function.
     *
     * @return a list of unique items
     */
    public List<E> uniquesToList() {
        return getBranch(singlePredicate);
    }

    /***
     * Groups all items in the list based on the key provider function.
     *
     * @return an immutable map where each key maps to a list of items
     */
    public Map<K, List<E>> itemByKey() {
        return ImmutableMap.copyOf(this.stream().collect(Collectors.groupingBy(keyProvider)));
    }

    /***
     * Creates a map where each key maps to a single item, merging duplicates using the provided merge function.
     *
     * @param mergeFunction a function to merge duplicate items
     * @return a map where each key maps to a single item
     */
    public Map<K, E> singleItemByKey(BinaryOperator<E> mergeFunction) {
        return this.stream().collect(Collectors.toMap(keyProvider, Function.identity(), mergeFunction, HashMap::new));
    }

    /***
     * Retrieves a list of single items, merging duplicates using the provided merge function.
     *
     * @param mergeFunction a function to merge duplicate items
     * @return a list of single items
     */
    public List<E> singleItemToList(BinaryOperator<E> mergeFunction) {
        return new ArrayList<>(singleItemByKey(mergeFunction).values());
    }

    /***
     * Checks if the list contains any duplicate items based on the key provider function.
     *
     * @return true if there are duplicate items, false otherwise
     */
    public boolean hasDuplicates() {
        return match(multiplePredicate);
    }

    /***
     * Checks if the list contains any unique items based on the key provider function.
     *
     * @return true if there are unique items, false otherwise
     */
    public boolean hasUniques() {
        return match(singlePredicate);
    }

    private List<E> getBranch(Predicate<List<E>> predicate) {
        return getStreamFromMap().filter(predicate).flatMap(List::stream).toList();
    }

    private boolean match(Predicate<List<E>> predicate) {
        return getStreamFromMap().anyMatch(predicate);
    }


    private Stream<List<E>> getStreamFromMap() {
        return itemByKey().values().stream();
    }
}
