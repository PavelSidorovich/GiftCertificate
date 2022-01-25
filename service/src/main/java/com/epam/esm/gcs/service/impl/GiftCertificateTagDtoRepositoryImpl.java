package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.repository.GiftCertificateTagRepository;
import com.epam.esm.gcs.service.GiftCertificateTagDtoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.gcs.mapper.GiftCertificateColumn.*;

@Component
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class GiftCertificateTagDtoRepositoryImpl implements GiftCertificateTagDtoRepository {

    private final GiftCertificateTagRepository linker;
    private final ModelMapper modelMapper;

    @Override
    public void link(GiftCertificateDto model) {
        linker.link(modelMapper.map(model, GiftCertificateModel.class));
    }

    @Override
    public void unlink(GiftCertificateDto model) {
        linker.unlink(modelMapper.map(model, GiftCertificateModel.class));
    }

    @Override
    public GiftCertificateDto findById(long id) {
        return modelMapper.map(linker.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, ID.getColumnName(), id)
        ), GiftCertificateDto.class);
    }

    @Override
    public GiftCertificateDto findByName(String name) {
        return modelMapper.map(linker.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, NAME.getColumnName(), name)
        ), GiftCertificateDto.class);
    }

    // TODO: 1/25/2022
    @Override
    public List<GiftCertificateDto> findAll() {
        return linker.findAll().stream()
                     .map(certificate -> modelMapper.map(certificate, GiftCertificateDto.class))
                     .collect(Collectors.toList());
    }

}
