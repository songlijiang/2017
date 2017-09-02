package com.slj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by slj on 16/10/3.
 */
@Data
@AllArgsConstructor
public class Content extends Entity {

    public static int WANGYI=1;
    private int type;
    private String key;
    private String content;
}
