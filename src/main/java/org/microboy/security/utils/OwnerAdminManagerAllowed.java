package org.microboy.security.utils;

import jakarta.annotation.security.RolesAllowed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.microboy.security.constants.RoleConstants.*;

/**
 * This annotation is used to restrict access to methods or classes
 * to users with specific roles: OWNER, ADMIN, or MANAGER.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RolesAllowed({OWNER, ADMIN, MANAGER})
public @interface OwnerAdminManagerAllowed {
}
