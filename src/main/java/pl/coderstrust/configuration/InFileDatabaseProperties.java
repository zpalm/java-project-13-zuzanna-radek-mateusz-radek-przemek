package pl.coderstrust.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pl.coderstrust.database.in-file")
public class InFileDatabaseProperties {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }
}
