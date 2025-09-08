package com.ia.common.utilities.helpher.data;

import com.ia.common.utilities.helper.data.DefaultSliceDataFetcher;
import com.ia.common.utilities.helper.data.model.pagination.OffsetPage;
import com.ia.common.utilities.helper.data.model.pagination.OffsetPageRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultSliceDataFetcherTest {

    private final DefaultSliceDataFetcher fetcher = new DefaultSliceDataFetcher();

    @Test
    void testFetchData() {
        final Function<Supplier<OffsetPageRequest>, OffsetPage<Long>> pageFetcher = request->OffsetPage.of(List.of(1L,2L,3L), 1, 1,3);
        final var result = fetcher.fetch(1,pageFetcher);
        assertThat(result).isNotNull();
        assertThat(result.fetchedData()).hasSize(12);
        assertThat(result.fetchedData()).contains(1L,2L,3L);
    }
}
