package com.normalizedb.security.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
public class PrincipalUser {
    @Id
    @Column(name = "username", length = 20, insertable = false, updatable = false)
    private String username;

    @Column(name = "password", insertable = false, updatable = false)
    private String password;

    @Column(name = "role", insertable = false, updatable = false)
    private String role;
}
