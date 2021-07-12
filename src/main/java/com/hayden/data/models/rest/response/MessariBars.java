package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public class MessariBars extends PageableResponse implements ExchangeResponse {

    public Data data;
    public Status status;

    @JsonIgnore
    static volatile int nextVal;

    @Override
    public synchronized String nextVal()
    {
        return String.valueOf(++nextVal);
    }

    @Override
    public synchronized void finish(){
        nextVal = 0;
        this.finished = true;
    }

    @Override
    public boolean notEmpty()
    {
        return !(this.data == null || this.data.values == null || this.data.values.length == 0);
    }

    @Override
    protected void resetVal()
    {
        nextVal = 0;
        this.finished = true;
    }

    public static class Status {
        public Long elapsed;
        public String timestamp;
    }

    public static class Data {

        public Schema schema;
        public String[][] values;
        public String market_id;
        public String market_slug;
        @JsonProperty("class")
        public String clzz;
        public Parameters parameters;
        public String market_name;
        public Boolean is_included_in_messari_price;

        public static class Schema {

            public String metric_id;
            public String minimum_interval;
            public Map[] source_attribution;
            public ValuesSchema values_schema;
            public String first_available;
            public String description;

            public static class ValuesSchema {
                public String volume;
                public String high;
                public String low;
                public String close;
                public String open;
                public String timestamp;
            }

        }

        public static class Parameters {
            public String timestamp_format;
            public String market_key;
            public String[] columns;
            public String start;
            public String format;
            public String market_id;
            public String end;
            public String interval;
            public String order;
        }
    }

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        Arrays.stream(this.data.values)
                .forEach(metrics -> {
                    dto.addBar(
                            Date.from(Instant.ofEpochMilli(Long.parseLong(metrics[0]))),
                            Double.valueOf(metrics[1]),
                            Double.valueOf(metrics[2]),
                            Double.valueOf(metrics[3]),
                            Double.valueOf(metrics[4]),
                            Double.valueOf(metrics[5])
                    );
                });
        return dto;
    }
}
