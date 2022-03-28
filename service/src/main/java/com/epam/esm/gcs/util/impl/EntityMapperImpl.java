package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.util.EntityMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class EntityMapperImpl implements EntityMapper {

    private final ModelMapper modelMapper;

    @Override
    public <T, F> F map(T model, Class<F> clazz) {
        return modelMapper.map(model, clazz);
    }

    @Override
    public <T, F> List<F> map(List<T> list, Class<F> clazz) {
        return retrieveStream(list, clazz).collect(Collectors.toList());
    }

    @Override
    public <T, F> Set<F> map(Set<T> set, Class<F> clazz) {
        return retrieveStream(set, clazz).collect(Collectors.toSet());
    }

    private <T, F> Stream<F> retrieveStream(Collection<T> collection, Class<F> clazz) {
        return collection.stream()
                         .map(t -> modelMapper.map(t, clazz));
    }

}
