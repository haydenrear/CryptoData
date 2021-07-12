package com.hayden.data.services;

import com.hayden.data.mongoconfig.MongoCollections;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.decision.models.sectors.model.*;
import com.hayden.decision.models.shared.DataMetrics;
import com.hayden.decision.models.shared.EconDataWSDTO;
import com.hayden.decision.models.shared.GetCorrelatesReq;
import com.hayden.decision.util.DateService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Service
public class EconDataService implements ApplicationContextAware {

    ApplicationContext applicationContext;
    MongoCollections mongoCollections;
    AssetDTOFactory assetService;

    public EconDataService(MongoCollections mongoCollections, AssetDTOFactory assetService) {
        this.mongoCollections = mongoCollections;
        this.assetService = assetService;
    }

    public Flux<EconDataWSDTO> findEconData(Correlation correlation){
        ConcurrentSkipListMap<Date, Double> skipListMap = new ConcurrentSkipListMap<>();
        return mongoCollections.showCollections(correlation.getGetCorrelate())
                .flatMap(collectionName -> {
                    return mongoCollections.showDocumentsForCollection(collectionName, correlation.getGetCorrelate())
                            .map(document -> {
                                Object dateString = document.get("Date Value") == null ? document.get("Date") : document.get("Date Value");
                                Object valueToParse = document.get("Value");
                                Double val = null;
                                if(valueToParse == null || dateString == null){
                                    dateString = DateService.defaultDate();
                                    val = 0D;
                                }
                                if(valueToParse != null){
                                    if (valueToParse instanceof Double)
                                        val = (Double) valueToParse;
                                    else {
                                        return Tuples.of(DateService.defaultDate(), 0d);
                                    }
                                }
                                if (dateString instanceof Date)
                                    return Tuples.of((Date)dateString, val);
                                Date date = DateService.parseDate((String) dateString);
                                return Tuples.of(date, val);
                            })
                            .onErrorContinue((object, throwable) -> System.out.println(object + throwable.toString()))
                            .filter(tuple -> !tuple.getT1().equals(DateService.defaultDate()))
                            .collect(Collectors.toConcurrentMap(Tuple2::getT1, Tuple2::getT2, (val1, val2) -> val1, () -> skipListMap))
                            .onErrorContinue((object, throwable) -> System.out.println(object + throwable.toString()))
                            .map(dataMap -> {
                                DataMetrics dataMetrics = assetService.newDataMetrics();
                                dataMetrics.setOriginalPrices(dataMap);
                                return new EconDataWSDTO.MetricsRecord(collectionName, dataMetrics);
                            });
                }).map(finalMap -> assetService.getEconDataWSDTO(finalMap, correlation));
    }

    public Flux<EconDataWSDTO> getData(GetCorrelatesReq dto) {
        List<Flux<EconDataWSDTO>> fluxList= new ArrayList<>();
        for(GetCorrelate g : dto.correlates()) {
               fluxList.add(getData(g));
            }
        return Flux.concat(fluxList);
    }

    private Flux<EconDataWSDTO> getData(GetCorrelate getCorrelate) {
        Correlation correlation = assetService.newGetCorrelation(getCorrelate);
        return findEconData(correlation);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
