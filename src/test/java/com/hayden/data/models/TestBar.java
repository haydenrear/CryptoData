package com.hayden.data.models;

import com.hayden.decision.models.enums.PriceType;
import com.hayden.decision.models.shared.Bar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.time.Instant;


@SpringBootTest(classes = {Bar.class})
@ExtendWith(SpringExtension.class)
public class TestBar {

    @Autowired
    Bar bar;

    @Test
    public void testBar(){
        bar.setT(Date.from(Instant.now()));
        bar.setC(1d);
        bar.setV(1d);
        bar.setL(1d);
        bar.setH(1d);
        bar.setO(1d);
        System.out.println(bar.fromPriceType(PriceType.OPEN));
        System.out.println(bar.fromPriceType(PriceType.HIGH));
        System.out.println(bar.fromPriceType(PriceType.OPEN));
        System.out.println(bar.fromPriceType(PriceType.LOW));
        System.out.println(bar.fromPriceType(PriceType.VOLUME));
    }

}
