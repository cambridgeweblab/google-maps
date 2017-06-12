package ucles.weblab.common.googlemaps.config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import ucles.weblab.common.googlemaps.bluemix.OkHttp3RequestHandler;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Provides the GeoApiContext which holds the CA api key, and with a bluemix compatible request handler (apparently
 * OkHttp is not compatible, and OkHttp3 needs to be used).
 */
@Configuration
@ComponentScan(basePackages = "ucles.weblab.common.googlemaps")
@PropertySource("classpath:/application.properties")
public class ProvideApiContextConfig {
    /**
     * The google locations API context.
     *
     * According to the documentation "in real world scenarios, it's important to
     * instantiate GeoApiContext as a static variable or inside a singleton".
     * @link "https://github.com/googlemaps/google-maps-services-java"
     */
    @Bean
    @Scope(SCOPE_SINGLETON) //For documentation or any future scope changes
    public GeoApiContext geoApiContext(@Value("${google.api.key}") String apiKey) {
        return new GeoApiContext(new OkHttp3RequestHandler()).setApiKey(apiKey);
    }
}
