package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.util.EntityFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EntityFieldServiceImpl implements EntityFieldService {

    private static final String METHOD_PREFIX = "get";
    private static final int PREFIX_LENGTH = METHOD_PREFIX.length();

    @Override
    public List<String> getNotNullFields(Object entity) {
        Method[] methods = entity.getClass().getDeclaredMethods();
        List<String> fieldNames = new ArrayList<>();

        for (Method method : methods) {
            String fieldName = getNotNullFieldName(entity, method);
            if (fieldName != null) {
                fieldNames.add(fieldName);
            }
        }
        return fieldNames;
    }

    /**
     * Return list of not null fields according to exclude field names
     *
     * @param entity       entity to get fields of
     * @param excludeNames parts of field names to exclude
     * @return not null fields of the entity according to exclude rule
     */
    @Override
    public List<String> getNotNullFields(Object entity, String... excludeNames) {
        List<String> fields = getNotNullFields(entity);

        for (String excludeName : excludeNames) {
            fields = fields.stream()
                           .filter(name -> !name.toLowerCase().contains(excludeName))
                           .collect(Collectors.toList());
        }
        return fields;
    }

    private String getNotNullFieldName(Object entity, Method method) {
        try {
            String methodName = method.getName();
            if (methodName.startsWith(METHOD_PREFIX)) {
                Object value = method.invoke(entity);
                if (value != null) {
                    return methodName.substring(PREFIX_LENGTH);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException ex) {
            log.error("Cannot invoke get method of entity", ex);
        }
        return null;
    }

}
