package com.epam.esm.gcs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = UserModel_.TABLE)
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FIXME: 2/28/2022 make not nullable
    @Column(name = UserModel_.ACCOUNT_PASSWORD)
    private String password;

    @Column(name = UserModel_.FIRST_NAME, nullable = false)
    private String firstName;

    @Column(name = UserModel_.LAST_NAME, nullable = false)
    private String lastName;

    @Column(name = UserModel_.EMAIL, unique = true, nullable = false)
    private String email;

    @Column(name = UserModel_.BALANCE, nullable = false, columnDefinition = "numeric(8,2) default 0")
    private BigDecimal balance;

    @ManyToMany
    @JoinTable(
            name = "account_privilege",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id")
    )
    private Collection<AccountRoleModel> roles;

}
