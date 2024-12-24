package factory;

import java.util.Properties;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import util.PropertyUtils;

public class DataSourceHolder {

    private static DataSource dataSource;

    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = initializeDataSource();
        }
        return dataSource;
    }

    private static DataSource initializeDataSource() {
        Properties applicationProperties = PropertyUtils.getApplicationProperties();

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(applicationProperties.getProperty("database.url"));
        dataSource.setUser(applicationProperties.getProperty("database.username"));
        dataSource.setPassword(applicationProperties.getProperty("database.password"));
        return dataSource;
    }
}
