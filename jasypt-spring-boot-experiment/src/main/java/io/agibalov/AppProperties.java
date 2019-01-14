package io.agibalov;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
@Data
@NoArgsConstructor
public class AppProperties {
    private String username;
    private String password;
}
