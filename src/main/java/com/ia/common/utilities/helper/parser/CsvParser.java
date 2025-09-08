package com.ia.common.utilities.helper.parser;

import com.ia.common.utilities.helper.function.TriFunction;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

/***
 * csv file parsing specification
 * @author Martin Blaise Signe
 * @param <O> data output type
 */
public interface CsvParser<O> extends Parser<File, List<O>> {

    Logger log = org.slf4j.LoggerFactory.getLogger(CsvParser.class);

    /**
     * Default parsing operation for csv files.
     *
     * @return TriFunction that accept the file to parse, the output object type and the number of lines to skip on top of the file.
     */
    default TriFunction<File, Class<O>, Integer, List<O>> defaultCsvparser() {
        return (file, beanType, skipLines) -> {
            try (Reader reader = new FileReader(file.getAbsolutePath())) {
                return new CsvToBeanBuilder<O>(reader)
                        .withSkipLines(skipLines)
                        .withType(beanType)
                        .build().parse();
            } catch (Exception e) {
                log.error("Error occurred during the csv parsing. message =[{}]", e.getLocalizedMessage(), e);
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };
    }
}
