package com.hayden.data.models.rest.response;

import com.hayden.data.models.rest.To;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.decision.models.shared.Bar;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reactivestreams.Publisher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@To(Bars.class)
public class BinanceBars implements StringResponse<Bars> {

    String data;

    AssetDTOFactory assetDTOService;

    public BinanceBars(String data)
    {
        this.data = data;
    }

    @Override
    public Publisher<Bars> responses()
    {
        JSONParser jsonParser = new JSONParser();
        ConcurrentSkipListMap<Date, Bar> priceInstances = new ConcurrentSkipListMap<>();
        try {
            JSONArray parse = (JSONArray) jsonParser.parse(data);
            for(var o : parse){
                if(o instanceof JSONArray inner){
                    var arr = jsonParser.parse(inner.toJSONString());
                    if(arr instanceof JSONArray metrics){
                        var priceInstance = assetDTOService
                                .newBar(
                                        Date.from(Instant.ofEpochMilli((Long)metrics.get(0))),
                                        Double.valueOf((String)metrics.get(1)),
                                        Double.valueOf((String)metrics.get(2)),
                                        Double.valueOf((String)metrics.get(3)),
                                        Double.valueOf((String)metrics.get(4)),
                                        Double.valueOf((String)metrics.get(5))
                                );
                        priceInstances.put(priceInstance.getT(), priceInstance);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Mono.just(assetDTOService.newBars(priceInstances));
    }

    @Autowired
    public void setAssetFactory(AssetDTOFactory assetDTOService) throws BeansException
    {
        this.assetDTOService = assetDTOService;
    }

}
