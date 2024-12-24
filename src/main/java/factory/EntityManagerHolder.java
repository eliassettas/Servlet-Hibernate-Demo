package factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.jpa.HibernatePersistenceProvider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import util.PropertyUtils;

public class EntityManagerHolder {

    private static EntityManagerFactory entityManagerFactory;

    public static void init() {
        entityManagerFactory = new HibernatePersistenceProvider()
                // Without this we would need META-INF/persistence.xml in resources
                .createContainerEntityManagerFactory(new DemoPersistenceUnitInfo(), getProperties());
    }

    private static Map<String, String> getProperties() {
        Properties applicationProperties = PropertyUtils.getApplicationProperties();
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.show_sql", applicationProperties.getProperty("hibernate.show_sql"));
        properties.put("jakarta.persistence.schema-generation.database.action", applicationProperties.getProperty("hibernate.hbm2ddl.auto"));
        return properties;
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void close() {
        entityManagerFactory.close();
    }
}
