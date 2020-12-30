package com.normalizedb.security.entities;

import java.util.HashMap;
import java.util.Map;

public enum Role {
    ADMIN("admin"),
    USER("user");

    private static final Map<String, Role> valueMappings = new HashMap<>();
    private String role;

    static {
        for(Role current: Role.values()) {
            valueMappings.put(current.getValue(), current);
        }
    }

    Role(String value) {
        this.role = value;
    }

    public String getValue() {
        return this.role;
    }

    public static Role fromString(String val) throws Exception {
        Role role = valueMappings.get(val);
        if(role == null) {
            throw new Exception(String.format("Role {%s} not found", val));
        }
        return role;
    }

    @Override
    public String toString() {
        return this.role;
    }
}
