package com.epam.esm.gcs.model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountModel.class)
public abstract class AccountModel_ {

    public static volatile SingularAttribute<AccountModel, Long> id;
    public static volatile SingularAttribute<AccountModel, String> email;
    public static volatile SingularAttribute<AccountModel, String> password;
    public static volatile SetAttribute<AccountModel, AccountRoleModel> roles;
    public static volatile SingularAttribute<AccountModel, Boolean> enabled;

    public static final String TABLE = "account";
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String ACCOUNT_PASSWORD = "account_password";
    public static final String ENABLED = "is_enabled";

}
