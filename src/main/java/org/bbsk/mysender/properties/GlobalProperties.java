package org.bbsk.mysender.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "mysender")
@Component
public class GlobalProperties {

    private Chrome chrome;

    @Getter
    public static class Chrome {
        private Driver driver;

        @Getter
        public static class Driver {
            private String path;
        }
    }
}
