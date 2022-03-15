package com.epam.esm.gcs.model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountRoleModel.class)
public abstract class AccountRoleModel_ {

    public static volatile SingularAttribute<AccountRoleModel, Long> id;
    public static volatile SingularAttribute<AccountRoleModel, String> name;

    public static final String TABLE = "role";
    public static final String ID = "id";
    public static final String ROLE_NAME = "role_name";

}
