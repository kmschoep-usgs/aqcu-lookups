package gov.usgs.aqcu.config;

import java.time.Clock;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import com.google.gson.Gson;

import gov.usgs.aqcu.serializer.SwaggerGsonSerializer;
import gov.usgs.aqcu.util.AqcuGsonBuilderFactory;
import springfox.documentation.spring.web.json.Json;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter < ? >> converters) {
		GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
		gsonHttpMessageConverter.setGson(gson());
		converters.add(gsonHttpMessageConverter);
	}

	@Bean
	public Gson gson() {
		return AqcuGsonBuilderFactory.getConfiguredGsonBuilder()
			.registerTypeAdapter(Json.class, new SwaggerGsonSerializer())
			.create();
	}

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
}
