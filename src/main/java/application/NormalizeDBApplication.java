package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"functions", "services", "configuration"})
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class,
                                    DataSourceAutoConfiguration.class})
public class NormalizeDBApplication {

    public static void main(String[] args){
        SpringApplication.run(NormalizeDBApplication.class, args);
    }
}
