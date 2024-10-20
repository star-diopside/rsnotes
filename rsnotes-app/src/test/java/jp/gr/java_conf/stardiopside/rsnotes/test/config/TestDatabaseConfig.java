package jp.gr.java_conf.stardiopside.rsnotes.test.config;

import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import com.github.springtestdbunit.dataset.DataSetLoader;
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;
import jp.gr.java_conf.stardiopside.rsnotes.test.dataset.CsvDataSetLoader;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TestDatabaseConfig {

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        return new DatabaseDataSourceConnectionFactoryBean(dbUnitDataSource());
    }

    @Bean
    public DataSource dbUnitDataSource() {
        return dbUnitDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties("dbunit.datasource")
    public DataSourceProperties dbUnitDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSetLoader dbUnitDataSetLoader() {
        return new ReplacementDataSetLoader(new CsvDataSetLoader());
    }
}
