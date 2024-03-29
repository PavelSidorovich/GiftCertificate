package com.epam.esm.gcs.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@Entity
@Table(name = UserModel_.TABLE)
public class UserModel extends AccountModel {

    @Column(name = UserModel_.FIRST_NAME, nullable = false)
    private String firstName;

    @Column(name = UserModel_.LAST_NAME, nullable = false)
    private String lastName;

    @Column(name = UserModel_.BALANCE, nullable = false, columnDefinition = "numeric(8,2) default 0")
    private BigDecimal balance;

    @Builder
    private UserModel(Long id, String email, String password, Boolean enabled,
                      String firstName, String lastName, BigDecimal balance,
                      Set<AccountRoleModel> roles) {
        super(id, email, password, enabled, roles);
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    @PrePersist
    protected void onPrePersist() {
        super.onPrePersist();
        balance = BigDecimal.ZERO;
    }

}
