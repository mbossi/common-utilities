package com.ia.common.utilities.helper.data.model.pagination;
/***
 * OffsetPageRequest
 *
 * A simple record to represent a page request using offset and limit.
 *
 * @param offset the starting point of the page (zero-based)
 * @param limit  the maximum number of items to return
 */
public record OffsetPageRequest(Long offset, int limit) {
}
