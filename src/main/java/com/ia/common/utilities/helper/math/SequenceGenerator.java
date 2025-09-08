package com.ia.common.utilities.helper.math;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.LongStream;

/***
 * Utility class for generating sequences of numbers.
 * Currently, supports arithmetic sequences.
 * @see #arithmeticSequence(long, long, int)
 * @author Martin Blaise Signe
 */
@UtilityClass
public class SequenceGenerator {

    public List<Long> arithmeticSequence(long startOffset, long length, int diff) {
        if (diff <= 0) {
            throw new IllegalArgumentException("Diff must be greater than 0");
        }
        if (startOffset > length) {
            throw new IllegalArgumentException("Start must be less than or equal to end");
        }
        final long partitionSize = Math.ceilDiv(length - startOffset, diff);
        return LongStream.rangeClosed(0, partitionSize).mapToObj(i -> nextOffset(i, startOffset, length, diff)).toList();
    }

    private long nextOffset(long index, long startOffset, long length, int diff) {
        final long nextOffset = startOffset + index * diff;
        return Math.min(nextOffset, length);
    }
}
