package jp.gr.java_conf.stardiopside.rsnotes.config;

import jp.gr.java_conf.stardiopside.rsnotes.data.domain.SecurityContextAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {

    @Bean
    public SecurityContextAuditorAware securityContextAuditorAware() {
        return new SecurityContextAuditorAware();
    }
}
