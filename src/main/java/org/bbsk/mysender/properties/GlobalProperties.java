package org.bbsk.mysender.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "mysender")
public class GlobalProperties {
    private Chrome chrome;

    @Setter
    @Getter
    public static class Chrome {
        private Driver driver;

        @Setter
        @Getter
        public static class Driver {
            private String path;

        }
    }
}