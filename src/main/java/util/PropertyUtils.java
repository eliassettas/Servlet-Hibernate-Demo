package util;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import factory.DataSourceHolder;

public class PropertyUtils {

    public static Properties getApplicationProperties() {
        Properties properties = new Properties();
        URL propertiesURL = DataSourceHolder.class.getClassLoader().getResource("application.properties");

        try (FileReader fileReader = new FileReader(propertiesURL.getFile())) {
            properties.load(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }
}
