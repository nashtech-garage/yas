import com.yas.commonlibrary.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

    @SpringBootApplication
    @EnableConfigurationProperties(ServiceUrlConfig.class)
    public class CommonLibraryApplication {

        public static void main(String[] args) {
            SpringApplication.run(CommonLibraryApplication.class, args);
        }
    }