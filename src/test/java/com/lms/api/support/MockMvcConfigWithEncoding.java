package com.lms.api.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.CharacterEncodingFilter;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MockMvcConfigWithEncoding.Utf8Encoding.class)
public @interface MockMvcConfigWithEncoding {

  @TestConfiguration
  class Utf8Encoding {
    @Bean
    public MockMvcBuilderCustomizer utf8Config() {
      return builder -> builder.addFilters(new CharacterEncodingFilter("UTF-8", true));
    }
  }
}
