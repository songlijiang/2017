package com.slj.domain;

import com.slj.crawler.entity.Loupan;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by slj on 17/5/6.
 */
@Data
@AllArgsConstructor
public class LoupanChange {

    private Loupan origin ;

    private Loupan now;
}
