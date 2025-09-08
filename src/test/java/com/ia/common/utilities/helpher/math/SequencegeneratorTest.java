package com.ia.common.utilities.helpher.math;

import com.ia.common.utilities.helper.math.SequenceGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SequencegeneratorTest {

    @Test
    void testSequenceEventlyDistributed() {
        final var seq = SequenceGenerator.arithmeticSequence(0,5,1);
        assertThat(seq).hasSize(6).contains(0L,1L,2L,3L,4L,5L);
    }

    @Test
    void testSequenceNotEventlyDistributed() {
        final var seq = SequenceGenerator.arithmeticSequence(0,5,2);
        assertThat(seq).hasSize(4).contains(0L,2L,4L, 5L);
    }
}
