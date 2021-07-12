package com.hayden.data;

import com.hayden.data.models.Exchanges;
import com.hayden.data.mongoconfig.MongoPropertiesDataService;
import com.hayden.data.services.utility.ExchangeProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties({MongoPropertiesDataService.class, ExchangeProps.class})
public class StockdataApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockdataApplication.class, args);
    }

}
