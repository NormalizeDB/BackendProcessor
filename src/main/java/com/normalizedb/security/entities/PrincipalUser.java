package com.normalizedb.security.entities;

import com.normalizedb.security.entities.application.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class PrincipalUser {
    @Id
    @Column(name = "username", length = 20, updatable = false, nullable = false, unique = true)
    @NonNull
    private String username;

    @Column(name = "password", nullable = false)
    @NonNull
    private String password;

    @Column(name = "role", nullable = false)
    @NonNull
    private String role;

    @ManyToOne
    @JoinColumn(name = "role", foreignKey = @ForeignKey(name = "fk_role"),
            insertable = false, updatable = false, referencedColumnName = "name")
    private AuthorizedRole authorizedRole;
}
