package com.normalizedb.security.entities.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Role {
    ADMIN("ADMIN", 0),
    USER("USER", 1);


    private static final Map<String, Role> valueMappings = new HashMap<>();
    private String role;
    private int rank;

    static {
        for(Role current: Role.values()) {
            valueMappings.put(current.getRole(), current);
        }
    }

    Role(String role, int rank) {
        this.role = role;
        this.rank = rank;
    }

    public String getRole() {
        return role;
    }

    public int getRank() {
        return rank;
    }

    /**
     * Fetching the corresponding Role object associated with a string literal
     * @param val
     * @return Role object
     */
    public static Role fromString(String val) {
        Role role = valueMappings.get(val);
        if(role == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    String.format("Role {%s} not found", val));
        }
        return role;
    }

    /**
     * Fetch all the relevant roles that correspond to a given rank
     * @param rank
     * @return List of roles
     */
    public static List<Role> fromRank(int rank) {
        List<Role> rankingList = new ArrayList<>();
        for(Role role: Role.values()) {
            if(role.getRank() == rank) {
                rankingList.add(role);
            }
        }
        return rankingList;
    }

    @Override
    public String toString() {
        return this.role;
    }

}
