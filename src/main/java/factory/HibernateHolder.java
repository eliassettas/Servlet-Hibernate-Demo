package factory;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import model.Message;
import util.PropertyUtils;

public class HibernateHolder {

    private static SessionFactory sessionFactory;

    private static void init() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                // Without this we would need hibernate.properties in resources
                .applySettings(getProperties())
                .build();

        try {
            sessionFactory = new MetadataSources(serviceRegistry)
                    .addAnnotatedClass(Message.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception exception) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    private static Properties getProperties() {
        Properties applicationProperties = PropertyUtils.getApplicationProperties();
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.connection.url", applicationProperties.getProperty("database.url"));
        hibernateProperties.put("hibernate.connection.username", applicationProperties.getProperty("database.username"));
        hibernateProperties.put("hibernate.connection.password", applicationProperties.getProperty("database.password"));
        hibernateProperties.put("hibernate.connection.driver_class", applicationProperties.getProperty("hibernate.connection.driver_class"));
        hibernateProperties.put("hibernate.show_sql", applicationProperties.getProperty("hibernate.show_sql"));
        hibernateProperties.put("hibernate.hbm2ddl.auto", applicationProperties.getProperty("hibernate.hbm2ddl.auto"));
        return hibernateProperties;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void close() {
        sessionFactory.close();
    }
}
