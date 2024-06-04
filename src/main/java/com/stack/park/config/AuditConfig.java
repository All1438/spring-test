package com.stack.park.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // pour configurez le système pour suivre auto certaines actions sur les entity
@EnableJpaAuditing // permet d'utiliser les annotations spécifiques comme '@CreatedDate' et 'LastModifiedData'
public class AuditConfig {

}
