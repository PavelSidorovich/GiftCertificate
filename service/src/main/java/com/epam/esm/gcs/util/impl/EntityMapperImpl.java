package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.util.EntityMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        return list.stream()
                   .map(t -> modelMapper.map(t, clazz))
                   .collect(Collectors.toList());
    }

}
