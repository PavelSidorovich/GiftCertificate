package com.epam.esm.gcs.model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OrderModel.class)
public abstract class OrderModel_ {

    public static volatile SingularAttribute<OrderModel, Long> id;
    public static volatile SingularAttribute<OrderModel, GiftCertificateModel> certificate;
    public static volatile SingularAttribute<OrderModel, UserModel> user;
    public static volatile SingularAttribute<OrderModel, BigDecimal> cost;
    public static volatile SingularAttribute<OrderModel, LocalDateTime> purchaseDate;

    public static final String TABLE = "user_order";
    public static final String ID = "id";
    public static final String CERTIFICATE_ID = "certificate_id";
    public static final String USER_ID = "user_id";
    public static final String TOTAL_COST = "total_cost";
    public static final String PURCHASE_DATE = "purchase_date";

}
