package com.slj.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by slj on 17/5/6.
 */
@Data
public class LoupanAverage {

    private int year;

    private int month;

    private int day;

    private BigDecimal  price;
}
