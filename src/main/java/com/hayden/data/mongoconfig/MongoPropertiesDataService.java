package com.hayden.data.mongoconfig;

import lombok.Data;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@ConfigurationProperties("mongorepositories")
@Component
public class MongoPropertiesDataService {

    public MongoProperties Technology = new MongoProperties();
    public MongoProperties Cyclicality = new MongoProperties();
    public MongoProperties Inflation = new MongoProperties();
    public MongoProperties InterestRates = new MongoProperties();
    public MongoProperties WealthConcentration = new MongoProperties();
    public MongoProperties GrowthValue = new MongoProperties();

    public List<MongoProperties> getAll(){
        return Arrays.stream(this.getClass().getFields())
                .map(field -> {
                    try {
                        Object mongoProperties = field.get(this);
                        if(mongoProperties instanceof MongoProperties)
                            return mongoProperties;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return Stream.empty();
                })
                .map(mongo -> (MongoProperties) mongo)
                .collect(Collectors.toList());
    }
}
