package com.slj.crawler.dao;

import com.piaoniu.annotations.DaoGen;
import com.slj.dao.EntityDao;
import com.slj.crawler.entity.Loupan;
import com.slj.domain.LoupanAverage;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by slj on 17/2/11.
 */

@DaoGen(tablePrefix = "N_",tableName = "Loupan")
public interface LoupanDao extends EntityDao<Loupan>{

    String FIELDS = " `id`, `resourceType`,`resourceId` , `cityName`,  `name`,`region`,`area`,`price`,`timeStamp`,`addTime`, `updateTime`" ;

    @Select("select "+FIELDS+" from N_Loupan where resourceType =#{resourceType} and resourceId = #{resourceId} and `timeStamp` >= #{start}")
    Loupan findByResourceTypeAndResourceIdMoreDay(@Param("resourceType") int resourceType,@Param("resourceId") String resourceId,@Param("start") Date start);

    @Select("SELECT AVG(price) from N_Loupan where cityName = #{cityName} and `timeStamp` >=#{start} and `timeStamp` < #{end} and price >3000 and price <20000")
    BigDecimal getAverage(@Param("cityName")String cityName , @Param("start") Date start , @Param("end") Date end);

    List<Loupan> queryByCityNameAndTimeStamp(@Param("cityName") String cityName, @Param("timeStamp") Date timeStamp);

    @Select("select YEAR(addTime) as year , MONTH(addTime) as month ,AVG(price) as price from N_Loupan where cityName =#{cityName} and addTime>#{start} and price >3000 and price<30000   group by  YEAR(addTime), MONTH(addTime)")
    List<LoupanAverage> queryMonthAverage(@Param("start")Date start,@Param("cityName") String cityName);

    @Select("select YEAR(addTime) as year , MONTH(addTime) as month ,DAY(addTime) as day,AVG(price) as price from N_Loupan where cityName =#{cityName} and addTime>#{start} and price >3000 and price<30000 group by  YEAR(addTime), MONTH(addTime),Day(addTime)")
    List<LoupanAverage> queryDayAverage(@Param("start")Date start,@Param("cityName") String cityName);
}

