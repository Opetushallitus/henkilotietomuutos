package fi.oph.henkilotietomuutospalvelu.config;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrikaMapperFactory {
    @Bean
    public MapperFactory mapperFactory() {
        return new DefaultMapperFactory.Builder()
                .build();
    }
}
