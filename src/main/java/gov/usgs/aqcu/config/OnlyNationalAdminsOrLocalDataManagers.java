
package gov.usgs.aqcu.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('" + Roles.NATIONAL_ADMIN + "') OR hasRole('" + Roles.LOCAL_DATA_MANAGER + "')")
public @interface OnlyNationalAdminsOrLocalDataManagers {
}
