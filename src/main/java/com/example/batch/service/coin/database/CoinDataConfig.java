package com.example.batch.service.coin.database;

import com.zaxxer.hikari.HikariDataSource;
import org.example.database.common.RoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.batch.service.coin.database.rep.jpa",
        entityManagerFactoryRef = "coinEntityManagerFactory",
        transactionManagerRef = "coinTransactionManager"
)
public class CoinDataConfig {
    @ConfigurationProperties("database.datasource.coin.master")
    @Bean
    public DataSourceProperties coinMasterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @ConfigurationProperties("database.datasource.coin.master.configure")
    @Bean
    public DataSource coinMasterDatasource() {
        return coinMasterDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @ConfigurationProperties("database.datasource.coin.slave1")
    @Bean
    public DataSourceProperties coinSlave1DatasourceProperties() {
        return new DataSourceProperties();
    }

    @ConfigurationProperties("database.datasource.coin.slave1.configure")
    @Bean
    public DataSource coinSlave1Datasource() {
        return coinSlave1DatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "coinEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coinEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("coinMasterDatasource") DataSource masterDatasource,
            @Qualifier("coinSlave1Datasource") DataSource slaveDatasource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> datasourceMap = new HashMap<Object, Object>() {
            {
                put("master", masterDatasource);
                put("slave", slaveDatasource);
            }
        };

        routingDataSource.setTargetDataSources(datasourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDatasource);
        routingDataSource.afterPropertiesSet();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
//        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.default_batch_fetch_size", 1000);
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.use_sql_comments", true);

        return builder
                .dataSource(new LazyConnectionDataSourceProxy(routingDataSource))
                .packages("com.example.batch.service.coin.database.rep.jpa")
                .properties(properties)
                .persistenceUnit("coinEntityManagerFactory")
                .build();
    }

    @Bean(name = "coinTransactionManager")
    public PlatformTransactionManager coinTransactionManager(
            final @Qualifier("coinEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }
}