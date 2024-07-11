package com.example.batch.service.music.database;

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
        basePackages = "com.example.batch.service.music.database.rep.jpa",
        entityManagerFactoryRef = "musicEntityManagerFactory",
        transactionManagerRef = "musicTransactionManager"
)
public class MusicDataConfig {
    @ConfigurationProperties("database.datasource.music.master")
    @Bean
    public DataSourceProperties musicMasterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @ConfigurationProperties("database.datasource.music.master.configure")
    @Bean
    public DataSource musicMasterDatasource() {
        return musicMasterDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @ConfigurationProperties("database.datasource.music.slave1")
    @Bean
    public DataSourceProperties musicSlave1DatasourceProperties() {
        return new DataSourceProperties();
    }

    @ConfigurationProperties("database.datasource.music.slave1.configure")
    @Bean
    public DataSource musicSlave1Datasource() {
        return musicSlave1DatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "musicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean musicEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("musicMasterDatasource") DataSource masterDatasource,
            @Qualifier("musicSlave1Datasource") DataSource slaveDatasource
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
                .packages("com.example.batch.service.music.database.rep.jpa")
                .properties(properties)
                .persistenceUnit("musicEntityManagerFactory")
                .build();
    }

    @Bean(name = "musicTransactionManager")
    public PlatformTransactionManager musicTransactionManager(
            final @Qualifier("musicEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }
}