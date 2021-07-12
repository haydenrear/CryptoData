package com.hayden.data.mongoconfig;

import com.hayden.decision.models.sectors.model.GetCorrelate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Flux;

@NoArgsConstructor
@Data
public class MongoConfiguration<T extends GetCorrelate> {

    protected MongoPropertiesDataService mongoProperties;
//    protected MongoCollections mongoCollections;
    protected T getCorrelate;
    protected int lastIndex;
    Class<T> getCorrelateType;

    public Flux<String> showCollections(){
        return Flux.empty();
    }

//    public String getCollectionName(int index) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        String getCorrelateName = this.getCorrelate.getClass().getSimpleName();
//        Method method = mongoCollections.getClass().getMethod("get" + getCorrelateName);
//        return (String) method.invoke(index);
//    }

//    public int incrementFileIndex() {
//        if (lastIndex >= mongoCollections.fileProps.filesMap.get(getCorrelate.getClass().getSimpleName()).size()){
//            return 0;
//        }
//        return lastIndex+=1;
//    }
//
//    @Autowired
//    public void setMongoProperties(@Qualifier("mongoPropertiesDataService") MongoPropertiesDataService mongoProperties) {
//        this.mongoProperties = mongoProperties;
//    }


//    @Autowired
//    public void setMongoCollections(MongoCollections mongoCollections) {
//        this.mongoCollections = mongoCollections;
//    }

}
