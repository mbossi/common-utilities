package com.ia.common.utilities.helpher.collection;

import com.ia.common.utilities.helper.collection.GroupingArrayList;
import lombok.Builder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupingArrayListTest {


    @Test
    void groupingATest() {
        final Function<Security, String> keyProvider = Security::country;
        final var data = GroupingArrayList.create(sampleData(), keyProvider);
        assertThat(data.hasDuplicates()).isTrue();
        assertThat(data.hasUniques()).isTrue();
        assertThat(data.uniquesToList()).hasSize(6);
        assertThat(data.duplicateToMap()).hasSize(1).containsKey("USA");
        assertThat(data.itemByKey()).hasSize(7);
        data.add(new Security(9L, "DEU"));
        assertThat(data.hasDuplicates()).isTrue();
        assertThat(data.duplicateToMap()).hasSize(2).containsKey("USA").containsKey("DEU");
        assertThat(data.singleItemByKey((n, p) -> n.securityId() > p.securityId() ? n : p).get("USA").securityId()).isEqualTo(3L);
    }


    private List<Security> sampleData() {
        return List.of(
                new Security(1L, "USA"),
                new Security(2L, "JPN"),
                new Security(3L, "USA"),
                new Security(4L, "ITA"),
                new Security(5L, "DEU"),
                new Security(6L, "FRA"),
                new Security(7L, "CHN"),
                new Security(8L, "IRL"));
    }

    @Builder
    record Security(Long securityId, String country) {
    }
}
