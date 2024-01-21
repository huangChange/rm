package com.shell.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/16 22:24
 * @Description
 */
@Configuration
public class SwaggerConfig {

    @Value("${rm.gateway.url:127.0.0.1:8880}")
    private String gatewayUrl;

    @Bean
    public OpenAPI openAPI(InetUtilsProperties inetUtilsProperties) {
        String tokenUrl = getDefaultTokenUrl(inetUtilsProperties);
        return new OpenAPI()
                .info(info())
                .addSecurityItem(new SecurityRequirement().addList("oauthScheme"))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "oauthScheme",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.OAUTH2)
                                                .bearerFormat("Bearer")
                                                .flows(new OAuthFlows().password(new OAuthFlow().tokenUrl(tokenUrl)))
                                )
                );
    }

    private Info info() {
        return new Info()
                .title("RM Api Docmentation")
                .description("Rm Server Api Documentation ......")
                .contact(new Contact().email("shell@qq.com").name("Shell").url("https:xxx.shell.com"))
                .version("1.0.0");
    }

    private String getDefaultTokenUrl(InetUtilsProperties inetUtilsProperties) {
        String tokenEndpoint = "/auth/oauth/token";
        return gatewayUrl + tokenEndpoint;
    }

}
