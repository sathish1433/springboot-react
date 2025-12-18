package demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@EnableJpaRepositories("demo.repository")
@ComponentScan(basePackages = {"demo"})
@Configuration
public class DemoConfiguration {
}
