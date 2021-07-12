package com.hayden.data.repo;

import com.hayden.data.mongoconfig.MongoCollections;
import com.hayden.data.services.EconDataService;
import com.hayden.decision.models.sectors.model.Cyclicality;
import com.hayden.decision.util.DateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestRepo {

    @Autowired
    EconDataService econDataService;
    @Autowired
    MongoCollections mongoCollections;

    @Test
    public void testMongoCollections(){
        assertThat(mongoCollections.showCollections(Cyclicality.NEUTRAL)
                .next().block()).isNotNull();
    }

    @Test
    public void testMongoDoc() {
        mongoCollections.showCollections(Cyclicality.NEUTRAL)
                .next().flatMap(block -> mongoCollections.showDocumentsForCollection(block, Cyclicality.NEUTRAL)
                .next()).subscribe(document -> {
            Object dateString = document.get("Date Value");
            if(dateString == null){
                dateString = document.get("Date");
            }
            Double valueToParse = (Double) document.get("Value");
            Date date = DateService.parseDate((String) dateString);
            System.out.println(date.toString() +":"+ valueToParse);
            System.out.println(document);
        });
    }

}
