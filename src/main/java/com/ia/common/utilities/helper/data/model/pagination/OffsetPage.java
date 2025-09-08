package com.ia.common.utilities.helper.data.model.pagination;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
/***
 * Generic class for paginated responses using offset-based pagination.
 *
 * @param <T> the type of items in the page
 */
@Getter
@NoArgsConstructor
public class OffsetPage<T> {
    private List<T> items;
    private OffsetInformation pageInformation;

    private OffsetPage(List<T> items, long offset, int size, long totalSize) {
        this.items = items;
        this.pageInformation = new OffsetInformation(offset, size, totalSize, offset + size < totalSize);
    }

    /***
     * Creates an OffsetPage instance with the provided items and pagination details.
     *
     * @param items     the list of items in the current page
     * @param offset    the starting point of the current page
     * @param size      the number of items in the current page
     * @param totalSize the total number of items available
     * @param <T>       the type of items in the page
     * @return an OffsetPage instance containing the items and pagination information
     */
    public static <T> OffsetPage<T> of(List<T> items, long offset, int size, long totalSize) {
        return new OffsetPage<>(items, offset, size, totalSize);
    }

    /***
     * Creates an empty OffsetPage instance with the specified offset.
     *
     * @param offset the starting point of the empty page
     * @param <T>    the type of items in the page
     * @return an empty OffsetPage instance with pagination information
     */
    public static <T> OffsetPage<T> emptyOffset(long offset) {
        return new OffsetPage<>(List.of(), offset, 0, 0);
    }
}
