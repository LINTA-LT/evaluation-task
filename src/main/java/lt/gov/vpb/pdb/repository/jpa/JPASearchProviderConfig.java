package lt.gov.vpb.pdb.repository.jpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@ComponentScan
@Configuration
@EnableJpaRepositories(basePackages = "lt.gov.vpb.pdb.repository.jpa.patent.dao", entityManagerFactoryRef = "pdbpatentsEntityManager")
public class JPASearchProviderConfig {

    public static final String PDBPATENTS_PERSISTENCE_UNIT = "pdbpatents-persistence-unit";
    public static final String TMVIEW_PERSISTENCE_UNIT = "tmview-persistence-unit";
    public static final String DSVIEW_PERSISTENCE_UNIT = "dsview-persistence-unit";
    public static final String PDB_PERSISTENCE_UNIT = "pdb-persistence-unit";


    @Primary
    @Bean("pdbDataSource")
    public DataSource pdbDataSource(@Value("${pdb.jdbc.driver-class-name}") String jdbcDriver,
                                    @Value("${pdb.jdbc.url}") String jdbcUrl,
                                    @Value("${pdb.jdbc.username}") String jdbcUsername,
                                    @Value("${pdb.jdbc.password}") String jdbcPassword) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        return dataSource;

    }

    @Primary
    @Bean("pdbEntityManager")
    public LocalContainerEntityManagerFactoryBean pdbEntityManager(@Qualifier("pdbDataSource") DataSource pdbDataSource,
                                                                   @Value("${pdb.jpa.hibernate-dialect}") String hibernateDialect
    ) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pdbDataSource);
        em.setPackagesToScan("lt.gov.vpb.pdb.repository.jpa.domain");
        em.setPersistenceUnitName(PDB_PERSISTENCE_UNIT);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", hibernateDialect);
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean("pdbpatentsEntityManager")
    public LocalContainerEntityManagerFactoryBean pdbpatentsEntityManager(@Qualifier("pdbpatentsDataSource") DataSource pdbpatentsDataSource,
                                                                          @Value("${pdbpatents.jpa.hibernate-dialect}") String hibernateDialect
    ) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pdbpatentsDataSource);
        em.setPackagesToScan("lt.gov.vpb.pdb.repository.jpa.patent.domain");
        em.setPersistenceUnitName(PDBPATENTS_PERSISTENCE_UNIT);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", hibernateDialect);
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean("pdbpatentsTransactionManager")
    public PlatformTransactionManager pdbpatentsTransactionManager(@Qualifier("pdbpatentsEntityManager") EntityManagerFactory pdbpatentsEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setPersistenceUnitName(PDBPATENTS_PERSISTENCE_UNIT);
        transactionManager.setEntityManagerFactory(pdbpatentsEntityManager);
        return transactionManager;
    }

    @Bean("pdbpatentsDataSource")
    public DataSource pdbpatentsDataSource(@Value("${pdbpatents.jdbc.driver-class-name}") String jdbcDriver,
                                           @Value("${pdbpatents.jdbc.url}") String jdbcUrl,
                                           @Value("${pdbpatents.jdbc.username}") String jdbcUsername,
                                           @Value("${pdbpatents.jdbc.password}") String jdbcPassword) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        return dataSource;

    }

    @Bean("tmviewDataSource")
    public DataSource tmviewDataSource(@Value("${tmview.jdbc.driver-class-name}") String jdbcDriver,
                                       @Value("${tmview.jdbc.url}") String jdbcURL,
                                       @Value("${tmview.jdbc.username}") String jdbcUsername,
                                       @Value("${tmview.jdbc.password}") String jdbcPassword) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcURL);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        return dataSource;

    }

    @Bean("tmviewEntityManager")
    public LocalContainerEntityManagerFactoryBean tmviewEntityManager(@Qualifier("tmviewDataSource") DataSource tmviewDataSource,
                                                                      @Value("${tmview.jpa.hibernate-dialect}") String hibernateDialect
    ) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(tmviewDataSource);
        em.setPackagesToScan("org.tmdn.cti.repository.jpa.domain.trademark", "org.tmdn.cti.repository.jpa.domain.common");
        em.setPersistenceUnitName(TMVIEW_PERSISTENCE_UNIT);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", hibernateDialect);
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean("dsviewDataSource")
    public DataSource dsviewDataSource(@Value("${dsview.jdbc.driver-class-name}") String jdbcDriver,
                                       @Value("${dsview.jdbc.url}") String jdbcURL,
                                       @Value("${dsview.jdbc.username}") String jdbcUsername,
                                       @Value("${dsview.jdbc.password}") String jdbcPassword) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcURL);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        return dataSource;

    }

    @Bean("dsviewEntityManager")
    public LocalContainerEntityManagerFactoryBean dsviewEntityManager(@Qualifier("dsviewDataSource") DataSource dsviewDataSource,
                                                                      @Value("${dsview.jpa.hibernate-dialect}") String hibernateDialect
    ) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dsviewDataSource);
        em.setPackagesToScan("org.tmdn.cti.repository.jpa.domain.design", "org.tmdn.cti.repository.jpa.domain.common");
        em.setPersistenceUnitName(DSVIEW_PERSISTENCE_UNIT);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", hibernateDialect);
        em.setJpaPropertyMap(properties);
        return em;
    }


}
