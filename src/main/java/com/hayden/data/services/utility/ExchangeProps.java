package com.hayden.data.services.utility;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@ConfigurationProperties(prefix = "exchanges")
@PropertySource(value = "classpath:application.yml", factory = YamlProp.class)
@Data
@Component
public class ExchangeProps {

    String messariUrl;
    String binanceSocketUrl;
    String binanceRestUrl;
    String coinGeckoUrl;
    String messariApiKey;
    String binanceApiKey;

}
