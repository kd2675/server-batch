package com.example.batch.service.kospi.database;//package com.example.crawling.service.kospi.database;
//
//import com.example.crawling.common.database.RoutingDataSource;
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = "com.example.crawling.service.kospi.database.rep.jpa",
//        entityManagerFactoryRef = "pubEntityManagerFactory",
//        transactionManagerRef = "pubTransactionManager"
//)
//public class PubDataConfig {
////    private final PubDataSource pubDataSource;
//    @ConfigurationProperties("database.datasource.pub.master")
//    @Bean
//    public DataSourceProperties pubMasterDatasourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @ConfigurationProperties("database.datasource.pub.master.configure")
//    @Bean
//    public DataSource pubMasterDatasource() {
//        return pubMasterDatasourceProperties()
//                .initializeDataSourceBuilder()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @ConfigurationProperties("database.datasource.pub.slave1")
//    @Bean
//    public DataSourceProperties pubSlave1DatasourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @ConfigurationProperties("database.datasource.pub.slave1.configure")
//    @Bean
//    public DataSource pubSlave1Datasource() {
//        return pubSlave1DatasourceProperties()
//                .initializeDataSourceBuilder()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean pubEntityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            @Qualifier("pubMasterDatasource") DataSource masterDatasource,
//            @Qualifier("pubSlave1Datasource") DataSource slaveDatasource
//    ) {
//        RoutingDataSource routingDataSource = new RoutingDataSource();
//        Map<Object, Object> datasourceMap = new HashMap<Object, Object>() {
//            {
//                put("master", masterDatasource);
//                put("slave", slaveDatasource);
//            }
//        };
//
//        routingDataSource.setTargetDataSources(datasourceMap);
//        routingDataSource.setDefaultTargetDataSource(masterDatasource);
//        routingDataSource.afterPropertiesSet();
//
//        HashMap<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.hbm2ddl.auto", "validate");
//        properties.put("hibernate.default_batch_fetch_size", 1000);
//        properties.put("hibernate.show_sql", false);
//        properties.put("hibernate.format_sql", true);
//        properties.put("hibernate.use_sql_comments", true);
//
//        return builder
//                .dataSource(new LazyConnectionDataSourceProxy(routingDataSource))
//                .packages("com.example.crawling.service.kospi.database.rep.jpa")
//                .properties(properties)
//                .persistenceUnit("pubEntityManagerFactory")
//                .build();
//    }
//
//    @Bean(name = "pubTransactionManager")
//    public PlatformTransactionManager pubTransactionManager(
//            final @Qualifier("pubEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
//    ) {
//        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
//    }
//}