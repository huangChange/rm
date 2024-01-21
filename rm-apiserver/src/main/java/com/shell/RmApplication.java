package com.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/13 15:41
 * @Description
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.shell"})
public class RmApplication {

    public static void main(String[] args) {
        SpringApplication.run(RmApplication.class, args);
        System.out.println("       .__           .__  .__   \n" +
                "  _____|  |__   ____ |  | |  |  \n" +
                " /  ___/  |  \\_/ __ \\|  | |  |  \n" +
                " \\___ \\|   Y  \\  ___/|  |_|  |__\n" +
                "/____  >___|  /\\___  >____/____/\n" +
                "     \\/     \\/     \\/           ");
    }

}
