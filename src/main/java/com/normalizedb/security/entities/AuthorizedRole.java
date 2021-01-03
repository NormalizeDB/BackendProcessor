package com.normalizedb.security.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "authorized_role")
@Getter
@Setter
@RequiredArgsConstructor()
@NoArgsConstructor
public class AuthorizedRole {
    @Id
    @Column(name = "name", unique = true, nullable = false)
    @NonNull
    private String name;

    @OneToMany(targetEntity = PrincipalUser.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY,
                mappedBy = "authorizedRole", orphanRemoval = true)
    private List<PrincipalUser> principalUsers;
}
