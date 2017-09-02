package com.slj.crawler.entity;

import com.slj.entity.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by slj on 17/2/11.
 */
@Data
public class Loupan extends Entity{

    public static final int TYPE_LIANJIA =1;

    public static final int TYPE_ANJUKE=2;

    private int resourceType;

    private String cityName;

    private String resourceId;

    private String name;

    private String region;

    private  String area;

    private BigDecimal price =BigDecimal.ZERO;

    private Date timeStamp;


}
