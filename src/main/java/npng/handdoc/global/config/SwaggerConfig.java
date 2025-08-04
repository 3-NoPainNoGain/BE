package npng.handdoc.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(
                        new Info()
                                .title("handDoc REST API")
                                .description("NoPainNoGain Backend Team")
                                .contact(
                                        new Contact().name("NoPainNoGain BE Github").url("https://github.com/3-NoPainNoGain/BE.git"))
                                .version("1.0.0"));
    }
}