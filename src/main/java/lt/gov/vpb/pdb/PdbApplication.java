package lt.gov.vpb.pdb;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.model.util.StringMatching;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
@Slf4j
public class PdbApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(PdbApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Value("${opendata.files-dir}")
    private String opendataFilesDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/opendata/xsd/**")
                .addResourceLocations("classpath:/xsd/opendata/");
        registry
                .addResourceHandler("/opendata/files/**")
                .addResourceLocations("file:" + opendataFilesDir + (opendataFilesDir.endsWith("/") ? "" : "/"));
        //keep common static resources accessible too
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("lt"));
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor(){
            @Override
            protected Locale parseLocaleValue(String localeValue) {
                if(!"en".equals(localeValue) && !"lt".equals(localeValue)){
                    log.warn("Unsupported locale language requested {}, default value will be used ", localeValue);
                    localeValue = "lt";
                }
                return super.parseLocaleValue(localeValue);
            }
        };
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        ByteArrayHttpMessageConverter byteArrayConverter = new ByteArrayHttpMessageConverter();
        byteArrayConverter.setSupportedMediaTypes(
                Collections.singletonList(MediaType.valueOf("image/tiff"))
        );
        messageConverters.add(byteArrayConverter);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, StringMatching>() { //NOSONAR
            @Override
            public StringMatching convert(String s) {
                return StringMatching.fromString(s.toLowerCase());
            }
        });
    }

    @Bean
    public ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addDialect(new Java8TimeDialect());
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

}


