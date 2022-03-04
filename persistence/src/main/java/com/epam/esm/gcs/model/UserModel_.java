package com.epam.esm.gcs.model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserModel.class)
public abstract class UserModel_ {

    public static volatile SingularAttribute<UserModel, Long> id;
    public static volatile SingularAttribute<UserModel, String> password;
    public static volatile SingularAttribute<UserModel, String> firstName;
    public static volatile SingularAttribute<UserModel, String> lastName;
    public static volatile SingularAttribute<UserModel, String> email;
    public static volatile SingularAttribute<UserModel, BigDecimal> balance;
    public static volatile CollectionAttribute<UserModel, AccountRoleModel> roles;

    public static final String TABLE = "user_account";
    public static final String ID = "id";
    public static final String ACCOUNT_PASSWORD = "account_password";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String BALANCE = "balance";

}
