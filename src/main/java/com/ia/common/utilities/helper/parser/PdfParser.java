package com.ia.common.utilities.helper.parser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Portal document file parsing specification.
 *
 * @param <O> data output type
 * @author Martin Blaise Signe
 */
public interface PdfParser<O> extends Parser<File, O> {

    Logger log = LoggerFactory.getLogger(PdfParser.class);


    /**
     * Default parsing operation that extract the content of pdf file into a stream of lines.
     *
     * @return BiFunction that takes a File and a line delimiter, returning a Stream of strings representing the lines extracted from the file.
     */
    default BiFunction<File, String, Stream<String>> contentExtractor() {
        return (source, lineDelimiter) -> {
            try (final PDDocument document = PDDocument.load(source)) {
                if (!document.isEncrypted()) {
                    final PDFTextStripper tStripper = new PDFTextStripper();
                    final String pdfFileInText = tStripper.getText(document);
                    return Stream.of(pdfFileInText.split(lineDelimiter));
                } else {
                    log.error("[{}] is actually encrypted and we were unable to extract the content.", source.getAbsolutePath());
                    throw new IllegalArgumentException("The file " + source.getAbsolutePath() + "is encrypted");
                }
            } catch (Exception e) {
                log.error("Error occurred during the file processing. message=[{}]", e.getLocalizedMessage(), e);
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };
    }
}
