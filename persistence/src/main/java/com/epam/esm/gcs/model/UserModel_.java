package com.epam.esm.gcs.model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserModel.class)
public abstract class UserModel_ extends AccountModel_ {

    public static volatile SingularAttribute<AccountModel, String> firstName;
    public static volatile SingularAttribute<AccountModel, String> lastName;
    public static volatile SingularAttribute<AccountModel, BigDecimal> balance;

    public static final String TABLE = "user_detail";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String BALANCE = "balance";

}