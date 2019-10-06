package pl.coderstrust.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql.properties")
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "sql")
@Import({DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class})
public class SqlConfiguration {
}
