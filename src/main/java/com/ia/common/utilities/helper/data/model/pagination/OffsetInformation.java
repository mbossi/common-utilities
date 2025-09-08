package com.ia.common.utilities.helper.data.model.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing pagination information using offset-based pagination.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OffsetInformation {
    private long offset;
    private int pageSize;
    private long totalSize;
    private boolean hasNext;
}

