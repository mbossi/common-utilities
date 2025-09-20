package com.ia.common.utilities.helper.parser;

import com.ia.common.utilities.helper.function.TriFunction;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Stream;

/***
 * csv file parsing specification
 * @author Martin Blaise Signe
 * @param <O> data output type
 */
public interface CsvParser<O> extends Parser<File, List<O>> {

    Logger log = org.slf4j.LoggerFactory.getLogger(CsvParser.class);

    /***
     * Default csv parsing operation that convert the content of a csv file into a stream of beans of type O
     * @param file csv file to parse
     * @param beanType type of the bean to map the csv content
     * @param skipLines number of lines to skip before starting the parsing
     * @return stream of beans of type O
     */
    default TriFunction<File, Class<O>, Integer, Stream<O>> defaultCsvparser() {
        return (file, beanType, skipLines) -> {
            try (Reader reader = new FileReader(file.getAbsolutePath())) {
                return new CsvToBeanBuilder<O>(reader)
                        .withSkipLines(skipLines)
                        .withType(beanType)
                        .build().stream();
            } catch (Exception e) {
                log.error("Error occurred during the csv parsing. message =[{}]", e.getLocalizedMessage(), e);
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };
    }
}
