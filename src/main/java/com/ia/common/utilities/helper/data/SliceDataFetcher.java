package com.ia.common.utilities.helper.data;

import com.ia.common.utilities.helper.data.model.pagination.JPAOffsetPageRequest;
import com.ia.common.utilities.helper.data.model.pagination.OffsetPage;
import com.ia.common.utilities.helper.data.model.pagination.OffsetPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/***
 * Utility interface for fetching data in slices using pagination.
 * This interface provides a method to fetch data in chunks (slices) based on a specified slice size.
 * It uses a functional approach to allow custom data fetching logic to be passed in.
 */
public interface SliceDataFetcher {

    Logger log = LoggerFactory.getLogger(SliceDataFetcher.class);

    /***
     * Fetches data in slices of the specified size using the provided data fetcher function.
     *
     * @param sliceSize   the size of each slice to fetch
     * @param dataFetcher a function that takes a supplier of OffsetPageRequest and returns an OffsetPage of data
     * @param <T>         the type of data being fetched
     * @return a ProcessingResult containing the fetched data
     */
    <T> ProcessingResult<T> fetch(int sliceSize, Function<Supplier<OffsetPageRequest>, OffsetPage<T>> dataFetcher);

    /***
     * Creates a data fetcher function that uses the provided fetcher function to retrieve data.
     *
     * @param fetcher a function that takes a Pageable and returns a Page of data
     * @param <T>     the type of data being fetched
     * @return a function that takes a supplier of OffsetPageRequest and returns an OffsetPage of data
     */
    default <T> Function<Supplier<OffsetPageRequest>, OffsetPage<T>> fetch(Function<Pageable, Page<T>> fetcher) {
        return requestSupplier -> {
            OffsetPageRequest request = requestSupplier.get();
            final Pageable pageable = new JPAOffsetPageRequest(request.offset().intValue(), request.limit());
            final Page<T> data = fetcher.apply(pageable);
            log.info("{} items fetched with offset {} and limit {}", data.getNumberOfElements(), request.offset(), request.limit());
            return OffsetPage.of(data.getContent(), request.offset(), data.getSize(), data.getTotalElements());
        };
    }

    /***
     * A record to hold the result of the data processing, including the fetched data.
     *
     * @param fetchedData the list of fetched data
     * @param <T>         the type of data being held
     */
    record ProcessingResult<T>(List<T> fetchedData) {}
}
