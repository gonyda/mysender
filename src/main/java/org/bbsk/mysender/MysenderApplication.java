package org.bbsk.mysender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄링
@SpringBootApplication
public class MysenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysenderApplication.class, args);
    }

}
