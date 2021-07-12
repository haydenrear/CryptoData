package com.hayden.data.controller;

import com.hayden.data.services.EconDataService;
import com.hayden.decision.models.shared.EconDataWSDTO;
import com.hayden.decision.models.shared.GetCorrelateReq;
import com.hayden.decision.models.shared.GetCorrelatesReq;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;


@Controller
public class EconomicDataController {

    EconDataService econDataService;

    public EconomicDataController(EconDataService econDataService) {
        this.econDataService = econDataService;
    }

    @MessageMapping("allEcon")
    public Flux<EconDataWSDTO> all(GetCorrelatesReq dto) {
         return econDataService.getData(dto);
    }

    @MessageMapping("oneEcon")
    public Flux<EconDataWSDTO> one(GetCorrelateReq getCorrelateReq){
        return econDataService.findEconData(getCorrelateReq.correlation());
    }

}