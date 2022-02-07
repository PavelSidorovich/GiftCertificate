package com.epam.esm.gcs.comparator;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

@Configuration
public class ComparatorConfig {

    @Bean
    public ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(GiftCertificateComparatorFactory.class);
        return factoryBean;
    }

    @Bean(name = "dateComparator")
    public Comparator<GiftCertificateDto> dateComparator() {
        return Comparator.comparing(GiftCertificateDto::getCreateDate);
    }

    @Bean(name = "nameComparator")
    public Comparator<GiftCertificateDto> nameComparator() {
        return Comparator.comparing(GiftCertificateDto::getName);
    }

}
