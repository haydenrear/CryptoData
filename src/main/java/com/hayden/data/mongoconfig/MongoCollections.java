package com.hayden.data.mongoconfig;

import com.hayden.decision.models.sectors.model.GetCorrelate;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.SneakyThrows;
import org.bson.Document;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;



@Service
public class MongoCollections implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    public Flux<String> showCollections(GetCorrelate getCorrelate) {
        System.out.println(getCorrelate);
        MongoDatabase database = applicationContext.getBean(getCorrelate.valueOf() + "Database", MongoDatabase.class);
        return Flux.from(database.listCollectionNames());
    }

    public Flux<Document> showDocumentsForCollection(String collectionName, GetCorrelate getCorrelate) {
        MongoDatabase database = applicationContext.getBean(getCorrelate.valueOf() + "Database", MongoDatabase.class);
        return Flux.from(database.getCollection(collectionName).find());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
