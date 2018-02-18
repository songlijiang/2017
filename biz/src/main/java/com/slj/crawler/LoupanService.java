package com.slj.crawler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.slj.crawler.dao.LoupanDao;
import com.slj.crawler.entity.Loupan;
import com.slj.domain.LoupanChange;
import com.slj.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by slj on 17/2/11.
 */
@Service
public class LoupanService {

    @Autowired
    private LoupanDao loupanDao;

    public BigDecimal average(String cityName, Date date){
        BigDecimal average = loupanDao.getAverage(cityName, DateUtils.getDayStart(date),DateUtils.getDayStart(new DateTime(date).plusDays(1).toDate()));
        if(average==null){
            return BigDecimal.ZERO;
        }
        return average.setScale(2,BigDecimal.ROUND_HALF_UP);
    }

    public List<Pair<Loupan,Loupan>> change (String cityName, Date date){
        Date before = DateUtils.getDayStart(new DateTime(date).minusDays(1).toDate());
        Date now =  DateUtils.getDayStart(date);
        List<Loupan> loupansNow = loupanDao.queryByCityNameAndTimeStamp(cityName,now);
        List<Loupan> loupansBefore = loupanDao.queryByCityNameAndTimeStamp(cityName,before);
        if(CollectionUtils.isEmpty(loupansNow)){
            return Lists.newArrayList();
        }
       Map<String,Loupan> beforeMap = loupansBefore.stream().collect(Collectors.toMap(Loupan::getResourceId, Function.identity()));
       return loupansNow.stream().map(e-> Pair.of(beforeMap.getOrDefault(e.getResourceId(),new Loupan()),e)).filter(e->e.getLeft().getPrice().compareTo(e.getRight().getPrice())!=0).collect(Collectors.toList());
    }

    @Cacheable(value = "stat",key= "#cityName")
    public Map<String,Object> render(String cityName){
        return ImmutableMap.<String,Object>builder().put("average",average(cityName,new Date()))
            .put("change",change(cityName,new Date()).stream().map(e->new LoupanChange(e.getLeft(),e.getRight())).collect(Collectors.toList()))
            .put("month",loupanDao.queryMonthAverage(new DateTime().minusYears(1).toDate(),cityName))
            .put("day",loupanDao.queryDayAverage(new DateTime().minusYears(1).toDate(),cityName))
            .build();
    }

    public Loupan getLouPan(String resourceId , String name , String region , String area , BigDecimal price,String cityName,int type){
        Loupan loupan = new Loupan();
        loupan.setResourceType(type);
        loupan.setResourceId(resourceId);
        loupan.setName(name);
        loupan.setRegion(region);
        loupan.setArea(area);
        loupan.setPrice(price);
        loupan.setCityName(cityName);
        loupan.setTimeStamp(DateUtils.getDayStart(new Date()));
        return loupan;
    }

}
