package com.ia.common.utilities.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.function.Supplier;

/***
 * A utility class that provides methods for serializing and deserializing objects to and from JSON format,
 * as well as converting objects between different types using the Jackson library.
 * It uses a singleton instance of ObjectMapper to perform the operations.
 * The class includes error handling and logging for serialization, deserialization, and conversion processes.
 * @author Martin Blaise Signe
 */
@UtilityClass
@Slf4j
public class SerializerHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * Serialize an object to a JSON string.
     *
     * @param object the object to serialize
     * @return the JSON string representation of the object
     * @throws RuntimeException if an error occurs during serialization
     */
    public String serialize(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Unable to serialize the provided object to JSON. provided object ={}", object.toString(), e);
            throw new RuntimeException("Error during serialization", e);
        }
    }

    /**
     * Deserialize a JSON string to an object of the specified class.
     *
     * @param json  the JSON string to deserialize
     * @param clazz the class of the object to deserialize to
     * @param <T>   the type of the object to deserialize to
     * @return the deserialized object
     * @throws RuntimeException if an error occurs during deserialization
     */
    public <T> T deserialize(String json, Supplier<TypeReference<T>> clazz) {
        try {
            return MAPPER.readValue(json.getBytes(), clazz.get());
        } catch (Exception e) {
            log.error("Unable to deserialize the provided JSON to object. provided json ={}", json, e);
            throw new RuntimeException("Error during deserialization", e);
        }
    }

    /**
     * Convert an object to an object of the specified class.
     *
     * @param obj    the object to convert
     * @param clazz  the class of the object to convert to
     * @param strict whether to enable strict mode (fail on unknown properties)
     * @param <T>    the type of the object to convert to
     * @return the converted object
     * @throws RuntimeException if an error occurs during conversion
     */
    public <T> T convert(Object obj, TypeReference<T> clazz, Boolean strict) {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, strict);
        try {
            return MAPPER.convertValue(obj, clazz);
        } catch (Exception e) {
            log.error("Unable to convert the provided object to the target class. provided object ={}, target class={}", obj.toString(), clazz.getType(), e);
            throw new RuntimeException("Error during conversion", e);
        }
    }

    /**
     * Convert an object to an object of the specified class with strict mode disabled.
     *
     * @param obj   the object to convert
     * @param clazz the class of the object to convert to
     * @param <T>   the type of the object to convert to
     * @return the converted object
     * @throws RuntimeException if an error occurs during conversion
     */
    public <T> T convert(Object obj, TypeReference<T> clazz) {
        return convert(obj, clazz, Boolean.FALSE);
    }

    /**
     * Create a file at the specified path and write the provided data to it in JSON format.
     *
     * @param path the path of the file to create
     * @param data the data to write to the file
     * @return the created file
     * @throws RuntimeException if an error occurs during file creation or writing
     */
    public File createFile(String path, Object data) {
        try {
            final var file = new File(path);
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, data);
            return file;
        } catch (Exception e) {
            log.error("Unable to create file at path ={}", path, e);
            throw new RuntimeException("Error during file creation", e);
        }
    }
}
