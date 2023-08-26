package org.danuja25.cinesurfer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("imdb-service")
public class Configuration {
    private String rapidApiHost;
    private String rapidApiKey;

    public String getRapidApiHost() {
        return rapidApiHost;
    }

    public void setRapidApiHost(String rapidApiHost) {
        this.rapidApiHost = rapidApiHost;
    }

    public String getRapidApiKey() {
        return rapidApiKey;
    }

    public void setRapidApiKey(String rapidApiKey) {
        this.rapidApiKey = rapidApiKey;
    }
}
