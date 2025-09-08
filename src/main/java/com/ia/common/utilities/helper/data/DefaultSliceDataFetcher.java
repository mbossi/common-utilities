package com.ia.common.utilities.helper.data;

import com.ia.common.utilities.helper.data.model.pagination.OffsetPage;
import com.ia.common.utilities.helper.data.model.pagination.OffsetPageRequest;
import com.ia.common.utilities.helper.math.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/***
 * Default implementation of {@link SliceDataFetcher} that fetches data in slices using offset-based pagination.
 * It retrieves the first slice to determine the total size and next offsets, then fetches remaining slices in parallel.
 * The fetched data from all slices is merged into a single list and returned as a {@link ProcessingResult}.
 * @see SliceDataFetcher
 * @see OffsetPage
 * @see OffsetPageRequest
 * @see ProcessingResult
 * @see SequenceGenerator
 * @author Martin Blaise Signe
 */
@Component
@Slf4j
public class DefaultSliceDataFetcher implements SliceDataFetcher {

    @Override
    public <T> ProcessingResult<T> fetch(int sliceSize, Function<Supplier<OffsetPageRequest>, OffsetPage<T>> dataFetcher) {

        // Offset request creator definition
        final Function<Long, Supplier<OffsetPageRequest>> offsetRequestCreator = offset -> () -> new OffsetPageRequest(offset, sliceSize);

        // Fetch the first slice to get total size and next offsets
        final OffsetSliceState<List<T>> initialState = firstSliceFetcher(dataFetcher).apply(sliceSize);

        log.info("Start processing . data size: {}", sliceSize);
        final Stream<T> remainingData = initialState.nextOffsets().parallelStream()
                .map(offsetRequestCreator)
                .map(dataFetcher)
                .map(OffsetPage::getItems)
                .flatMap(Collection::stream);
        final var mergedData = Stream.concat(initialState.fetchedData().stream(), remainingData).toList();
        log.info("Completed processing . total size: {}", mergedData.size());
        return new ProcessingResult<>(mergedData);
    }

    /***
     * Fetches the first slice of data using the provided dataFetcher function.
     * It retrieves the first page of data, extracts the total size and next offsets,
     * and returns an OffsetSliceState containing the fetched data and pagination information.
     * @param dataFetcher Function that takes a Supplier of OffsetPageRequest and returns an OffsetPage of data.
     * @param <T> The type of data being fetched.
     * @return A function that takes the slice size and returns an OffsetSliceState with pagination details and fetched data.
     */
    private <T> Function<Integer, OffsetSliceState<List<T>>> firstSliceFetcher(Function<Supplier<OffsetPageRequest>, OffsetPage<T>> dataFetcher) {
        return sliceSize -> {
            final OffsetPage<T> firstPage = dataFetcher.apply(() -> new OffsetPageRequest(0L, sliceSize));
            final var pageInfo = firstPage.getPageInformation();
            final long totalSize = pageInfo.getTotalSize();
            final List<Long> nextOffsets = pageInfo.isHasNext() ? SequenceGenerator.arithmeticSequence(sliceSize, totalSize, sliceSize) : List.of();
            final var fetchedData = Optional.ofNullable(firstPage.getItems()).orElseGet(List::of);
            return new OffsetSliceState<>(nextOffsets, fetchedData, totalSize);
        };
    }


    record OffsetSliceState<T>(List<Long> nextOffsets, T fetchedData, long totalSize) {
    }
}
