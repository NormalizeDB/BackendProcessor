package com.normalizedb.security.configuration;

import com.normalizedb.security.SecurityConstants;
import com.normalizedb.security.entities.application.Role;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityExpressionConfig {
    private final SecurityConstants constants;
    @Autowired
    public SecurityExpressionConfig(SecurityConstants constants) {
        this.constants = constants;
    }

    private String generateRoleHierachy() {
        StringBuilder builder = new StringBuilder("");
        Role[] roles = Role.values();
        if(roles.length == 0) {
            return builder.toString();
        }
        Arrays.sort(roles, (o1, o2) -> Integer.compare(o1.getRank(), o2.getRank()));

        Pair<Integer, Role> parentRole = new Pair<>(roles[0].getRank(), roles[0]);

        List<Role> equalToParentRoles = new ArrayList<>();
        equalToParentRoles.add(parentRole.getValue());

        int currentIndex = 1;
        while(currentIndex < roles.length) {
            //LOWEST NUMBER = HIGHEST RANKING
            if(roles[currentIndex].getRank() > parentRole.getKey()) {
                //Check forward
                List<Role> equalToChildRole = new ArrayList<>();
                equalToChildRole.add(roles[currentIndex]);
                int interimPos = currentIndex;
                while((interimPos + 1) < roles.length &&
                        (roles[currentIndex].getRank() == roles[interimPos + 1].getRank())) {
                    interimPos++;
                    equalToChildRole.add(roles[interimPos]);
                }
                currentIndex = interimPos;

                for(Role parent: equalToParentRoles) {
                    for(Role child: equalToChildRole) {
                        if(!builder.toString().isEmpty()) {
                            builder.append(" and ");
                        }
                        String prefixedChild = grantedAuthorityDefaults().getRolePrefix().concat(child.getRole());
                        String prefixedParent = grantedAuthorityDefaults().getRolePrefix().concat(parent.getRole());
                        builder.append(String.format("%s > %s", prefixedParent, prefixedChild));
                    }
                }

                equalToParentRoles.clear();
                equalToParentRoles.addAll(equalToChildRole);
                parentRole = new Pair<>(roles[currentIndex].getRank(), roles[currentIndex]);

            } else if(parentRole.getKey() == roles[currentIndex].getRank()) {
                equalToParentRoles.add(roles[currentIndex]);
            }
            currentIndex++;
        }
        return builder.toString();
    }

    @Bean
    public RoleHierarchy fetchRoleHierachy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(generateRoleHierachy());
        return roleHierarchy;
    }

    @Bean
    public RoleHierarchyVoter fetchRoleHierachyVoter() {
        RoleHierarchyVoter voter = new RoleHierarchyVoter(fetchRoleHierachy());
        voter.setRolePrefix(grantedAuthorityDefaults().getRolePrefix());
        return voter;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(constants.getAuthorityPrefix());
    }

    private SecurityExpressionHandler<FilterInvocation> getFilterExpressionHandler() {
        //The default PermissionEvaluator instance for DefaultWebSecurity is DenyAllPermissionEvaluator.
        //This implies that any invocation to 'hasPermission(...)' will be denied
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setDefaultRolePrefix(grantedAuthorityDefaults().getRolePrefix());
        expressionHandler.setRoleHierarchy(fetchRoleHierachy());
        return expressionHandler;
    }

    @Bean
    @Primary
    public AccessDecisionManager getDecisionManager() {
        AccessDecisionVoter<Object> roleHierarchyVoter = fetchRoleHierachyVoter();
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(getFilterExpressionHandler());
        return new AffirmativeBased(Arrays.asList(roleHierarchyVoter, webExpressionVoter));
    }

}
