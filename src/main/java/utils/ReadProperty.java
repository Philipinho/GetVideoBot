package utils;

import java.io.InputStream;
import java.util.Properties;

public class ReadProperty {


    public static String getValue(String key) {
        Properties props = new Properties();

        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("settings.properties");
            props.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return props.getProperty(key);
    }

}