package org.danuja25.cinesurfer.model;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Data
@Component
@PropertySource("classpath:cineSurfer.properties")
@ConfigurationProperties(prefix = "cinesurfer.mapper")
@Getter
public class SurferProps {
    @NotEmpty
    private String startDrive;
    @NotEmpty
    private String endDrive;
    private String otherLocations;
}
