package org.clicknshop.aspect;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.clicknshop.annotation.RequireRole;
import org.clicknshop.exception.ForbiddenException;
import org.clicknshop.exception.UnauthorizedException;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.service.implementation.UserContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleAuthorizationAspect {

    private final UserContext userContext;

    @Before("@annotation(org.clicknshop.annotation.RequireAuth)")
    public void checkAuthentication(JoinPoint joinPoint) {
        User currentUser = userContext.getCurrentUser();

        if (currentUser == null) {
            log.warn("Accès non authentifié à: {}", joinPoint.getSignature().getName());
            throw new UnauthorizedException("Vous devez être connecté pour accéder à cette ressource");
        }

        log.info("Utilisateur authentifié: {} ({})", currentUser.getUsername(), currentUser.getRole());
    }

    @Before("@annotation(org.clicknshop.annotation.RequireRole)")
    public void checkRole(JoinPoint joinPoint) {
        User currentUser = userContext.getCurrentUser();

        if (currentUser == null) {
            log.warn("Accès non authentifié à: {}", joinPoint.getSignature().getName());
            throw new UnauthorizedException("Vous devez être connecté pour accéder à cette ressource");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);

        Role[] requiredRoles = requireRole.value();
        Role userRole = currentUser.getRole();

        boolean hasRequiredRole = Arrays.asList(requiredRoles).contains(userRole);

        if (!hasRequiredRole) {
            log.warn("Accès refusé pour {} ({}). Rôles requis: {}",
                    currentUser.getUsername(), userRole, Arrays.toString(requiredRoles));
            throw new ForbiddenException("Vous n'avez pas les autorisations nécessaires");
        }

        log.info("Autorisation accordée à {} ({}) pour {}",
                currentUser.getUsername(), userRole, joinPoint.getSignature().getName());
    }
}