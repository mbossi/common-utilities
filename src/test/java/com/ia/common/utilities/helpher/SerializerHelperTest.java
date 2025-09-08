package com.ia.common.utilities.helpher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ia.common.utilities.helper.SerializerHelper;
import lombok.Builder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SerializerHelperTest {


    @Test
    void testSerialization() {
        final String json = SerializerHelper.serialize(sampleData());
        final String expected = "[{\"name\":\"John Doe\",\"address\":{\"street\":\"123 Main St\",\"town\":\"Springfield\",\"country\":\"USA\"}},{\"name\":\"Jane Smith\",\"address\":{\"street\":\"456 Elm St\",\"town\":\"Shelbyville\",\"country\":\"USA\"}}]";
        assertThat(json).isEqualTo(expected);
    }

    @Test
    void testDeserialization() {
        final String json = "[{\"name\":\"John Doe\",\"address\":{\"street\":\"123 Main St\",\"town\":\"Springfield\",\"country\":\"USA\"}},{\"name\":\"Jane Smith\",\"address\":{\"street\":\"456 Elm St\",\"town\":\"Shelbyville\",\"country\":\"USA\"}}]";
        final List<Pojo> pojos = SerializerHelper.deserialize(json, () -> new TypeReference<List<Pojo>>() {
        });
        assertThat(pojos).hasSize(2);
        assertThat(pojos.get(0).name()).isEqualTo("John Doe");
        assertThat(pojos.get(0).address().street()).isEqualTo("123 Main St");
        assertThat(pojos.get(1).name()).isEqualTo("Jane Smith");
        assertThat(pojos.get(1).address().town()).isEqualTo("Shelbyville");
    }

    @Test
    void testConversion() {
        final TestClass original = TestClass.builder()
                .name("Parent")
                .test(TestClass.builder()
                        .name("Child")
                        .test(null)
                        .build())
                .build();
        final TestClass converted = SerializerHelper.convert(original, new TypeReference<TestClass>() {
        });
        assertThat(converted).isNotNull();
        assertThat(converted.name()).isEqualTo("Parent");
        assertThat(converted.test()).isNotNull();
        assertThat(converted.test().name()).isEqualTo("Child");
        assertThat(converted.test().test()).isNull();
    }

    @Test
    void testFileSerialization() throws Exception {
        final List<Pojo> data = sampleData();
        final File file = SerializerHelper.createFile("target/test.json", data);
        assertThat(file).exists();
        assertThat(Files.lines(file.toPath()).count()).isEqualTo(15);
        file.deleteOnExit();
    }


    @Test
    void convertWithException() {
        final String invalidJson = "invalid json";
        try {
            SerializerHelper.convert(invalidJson, new TypeReference<List<Pojo>>() {
            });
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Error during conversion");
        }
    }

    @Test
    void deserializeWithException() {
        final String invalidJson = "invalid json";
        try {
            SerializerHelper.deserialize(invalidJson, () -> new TypeReference<List<Pojo>>() {
            });
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Error during deserialization");
        }
    }

    @Test
    void testSerializeWithException() {
        Object obj = new Object() {
            // Jackson cannot serialize this object because it has no properties
        };
        try {
            SerializerHelper.serialize(obj);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Error during serialization");
        }
    }

    private List<Pojo> sampleData() {
        return List.of(
                Pojo.builder()
                        .name("John Doe")
                        .address(Address.builder()
                                .street("123 Main St")
                                .town("Springfield")
                                .country("USA")
                                .build())
                        .build(),
                Pojo.builder()
                        .name("Jane Smith")
                        .address(Address.builder()
                                .street("456 Elm St")
                                .town("Shelbyville")
                                .country("USA")
                                .build())
                        .build()
        );
    }

    @Builder
    record Pojo(String name, Address address) {
    }

    @Builder
    record Address(String street, String town, String country) {
    }

    @Builder
    record TestClass(String name, TestClass test) {
    }
}
