package com.hayden.data.mongoconfig;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MongoTemplatesConfiguration {

    @Bean("CyclicalityDatabase")
    public MongoDatabase cyclicalityDatabase(@Qualifier("mongoPropertiesDataService") MongoPropertiesDataService props) {
        MongoClient mongoClient = MongoClients.create(mongoUri(props.Cyclicality));
        return mongoClient.getDatabase(props.Cyclicality.getDatabase());
    }

    @Bean("TechnologyDatabase")
    public MongoDatabase technologyDatabase(@Qualifier("mongoPropertiesDataService") MongoPropertiesDataService props) {
        MongoClient mongoClient = MongoClients.create(mongoUri(props.Technology));
        return mongoClient.getDatabase(props.Technology.getDatabase());
    }

    @Bean("InterestRatesDatabase")
    public MongoDatabase interestRatesDatabase(@Qualifier("mongoPropertiesDataService") MongoPropertiesDataService props) {
        MongoClient mongoClient = MongoClients.create(mongoUri(props.InterestRates));
        return mongoClient.getDatabase(props.InterestRates.getDatabase());
    }

    @Bean("GrowthValueDatabase")
    public MongoDatabase growthValueDatabase(@Qualifier("mongoPropertiesDataService") MongoPropertiesDataService props) {
        MongoClient mongoClient = MongoClients.create(mongoUri(props.GrowthValue));
        return mongoClient.getDatabase(props.GrowthValue.getDatabase());
    }

    @Bean("InflationDatabase")
    public MongoDatabase inflationDatabase(@Qualifier("mongoPropertiesDataService") MongoPropertiesDataService props) {
        MongoClient mongoClient = MongoClients.create(mongoUri(props.Inflation));
        return mongoClient.getDatabase(props.Inflation.getDatabase());
    }

    @Bean("WealthConcentrationDatabase")
    public MongoDatabase wealthConcentrationDatabase(@Qualifier("mongoPropertiesDataService") MongoPropertiesDataService props) {
        MongoClient mongoClient = MongoClients.create(mongoUri(props.WealthConcentration));
        return mongoClient.getDatabase(props.WealthConcentration.getDatabase());
    }

    public String mongoUri(MongoProperties propertiesDB) {
        return "mongodb://" + propertiesDB.getUsername() + ":" + new String(propertiesDB.getPassword()) + "@" + propertiesDB.getHost() + ":" + propertiesDB.getPort() + "/" + propertiesDB.getDatabase() + "?authSource=admin";
    }

}
