package com.epam.esm.gcs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = AccountModel_.TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public class AccountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = AccountModel_.EMAIL, unique = true, nullable = false)
    private String email;

    // FIXME: 2/28/2022 make not nullable
    @Column(name = AccountModel_.ACCOUNT_PASSWORD)
    private String password;

    @Column(name = AccountModel_.ENABLED, nullable = false)
    private Boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "accounts_by_roles",
            joinColumns = @JoinColumn(
                    name = "account_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id")
    )
    private Set<AccountRoleModel> roles;

    @PrePersist
    protected void onPrePersist() {
        enabled = true;
    }

}
