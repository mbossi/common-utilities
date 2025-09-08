package com.ia.common.utilities.helper.data.model.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * A custom implementation of Spring Data's Pageable interface that uses offset and limit for pagination.
 * This is useful for scenarios where you want to control the starting point (offset) and the number of records (limit)
 * to be fetched from a data source, such as a database.
 */
public record JPAOffsetPageRequest(int offset, int limit, Sort sort) implements Pageable, Serializable {

    public JPAOffsetPageRequest {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
    }

    /**
     * Creates a new {@link JPAOffsetPageRequest} with the given offset, limit, direction and properties.
     *
     * @param offset     zero-based offset.
     * @param limit      the size of the page to be returned.
     * @param direction  the direction of the sort.
     * @param properties the properties to sort by.
     */
    public JPAOffsetPageRequest(int offset, int limit, Sort.Direction direction, String... properties) {
        this(offset, limit, Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link JPAOffsetPageRequest} with the given offset and limit.
     * The sort will be unsorted.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the page to be returned.
     */
    public JPAOffsetPageRequest(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new JPAOffsetPageRequest(offset() + getPageSize(), getPageSize(), getSort());
    }

    public Pageable previous() {
        return hasPrevious() ? new JPAOffsetPageRequest(offset() - getPageSize(), getPageSize(), getSort()) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new JPAOffsetPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new JPAOffsetPageRequest((pageNumber - 1) * limit, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
