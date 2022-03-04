package com.epam.esm.gcs.config;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.model.GiftCertificateModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                   .setSkipNullEnabled(true)
                   .setFieldAccessLevel(AccessLevel.PRIVATE);

        return modelMapper;
    }

    @Bean
    public ModelMapper certificateUpdateMapper() {
        final ModelMapper modelMapper = modelMapper();

        modelMapper.addMappings(skipModifiedFieldsMap());

        return modelMapper;
    }

    private PropertyMap<GiftCertificateDto, GiftCertificateModel> skipModifiedFieldsMap() {
        return new PropertyMap<>() {
            protected void configure() {
                skip().setTags(null);
                skip().setCreateDate(null);
                skip().setLastUpdateDate(null);
            }
        };
    }

}
