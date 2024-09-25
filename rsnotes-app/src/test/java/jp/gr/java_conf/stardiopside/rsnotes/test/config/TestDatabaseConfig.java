package jp.gr.java_conf.stardiopside.rsnotes.test.config;

import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TestDatabaseConfig {

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        return new DatabaseDataSourceConnectionFactoryBean(dbunitDataSource());
    }

    @Bean
    public DataSource dbunitDataSource() {
        DataSourceProperties properties = dbunitDataSourceProperties();
        return DataSourceBuilder.create()
                .type(properties.getType())
                .url(properties.getUrl())
                .driverClassName(properties.getDriverClassName())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    @Bean
    @ConfigurationProperties("dbunit.datasource")
    public DataSourceProperties dbunitDataSourceProperties() {
        return new DataSourceProperties();
    }
}
