package com.alienworkspace.cdr.patient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class for JPA auditing.
 *
 * @author Ikenumah (enumahinm@gmail.com)
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class JpaAuditingConfiguration {
}

