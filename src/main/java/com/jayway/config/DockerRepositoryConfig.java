package com.jayway.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.SpringSessionContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.jayway.repository.AccountRepository;

@Profile("docker")
@Configuration
@EnableJpaRepositories("com.jayway.repository")
public class DockerRepositoryConfig implements RepositoryConfig {
	
    @Bean
    @Override
    public DataSource dataSource() {
    	String mySqlJdbcUrl = System.getProperty("MYSQL_JDBC_URL", "jdbc:mysql://mysql:3306/spring_testing");
    	String mySqlJdbcUser = System.getProperty("MYSQL_JDBC_USER", "root");
    	String mySqlJdbcPassword = System.getProperty("MYSQL_JDBC_PASSWORD", "password");

    	DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(mySqlJdbcUrl);
        dataSource.setUsername(mySqlJdbcUser);
        dataSource.setPassword(mySqlJdbcPassword);
        return dataSource;
    }

    @Bean
    @Override
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan(AccountRepository.class.getPackage().getName());
        entityManagerFactoryBean.setJpaProperties(new Properties() {{
            put("hibernate.current_session_context_class", SpringSessionContext.class.getName());
        }});
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter() {{
            setDatabase(Database.MYSQL);
        }});
        return entityManagerFactoryBean;
    }

    @Bean
    @Override
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

}
