package com.epam.esm.gcs.model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CertificateModel.class)
public abstract class CertificateModel_ extends NamedModel_ {

    public static volatile SingularAttribute<CertificateModel, String> description;
    public static volatile SingularAttribute<CertificateModel, BigDecimal> price;
    public static volatile SingularAttribute<CertificateModel, Integer> duration;
    public static volatile SingularAttribute<CertificateModel, LocalDateTime> createDate;
    public static volatile SingularAttribute<CertificateModel, LocalDateTime> lastUpdateDate;
    public static volatile SetAttribute<CertificateModel, TagModel> tags;

    public static final String TABLE = "certificate";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String DURATION = "duration";
    public static final String CREATE_DATE = "create_date";
    public static final String LAST_UPDATE_DATE = "last_update_date";

}
