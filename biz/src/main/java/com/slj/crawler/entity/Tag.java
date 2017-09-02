package com.slj.crawler.entity;

import com.slj.entity.Entity;
import lombok.Data;

/**
 * Created by slj on 17/2/11.
 */
@Data
public class Tag  extends Entity{

    private int loupanId;

    private String type;

    private String value;
}
