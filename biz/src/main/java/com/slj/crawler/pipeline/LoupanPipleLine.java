package com.slj.crawler.pipeline;

import com.slj.crawler.dao.LoupanDao;
import com.slj.crawler.dao.TagDao;
import com.slj.crawler.entity.Loupan;
import com.slj.crawler.entity.Tag;
import com.slj.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by slj on 17/2/11.
 */
@Slf4j
@Service
public class LoupanPipleLine implements Pipeline{
    @Autowired
    private LoupanDao loupanDao;
    @Autowired
    private TagDao tagDao;

    @Override
    public void process(ResultItems resultItems, Task task) {
      List<Pair<Loupan,Map<String,String>>> housePair = resultItems.get("housePair");
      if(CollectionUtils.isEmpty(housePair)){
          return;
      }
      log.info("housepair = {}",housePair.size());
      housePair.stream().forEach(e->{
         Loupan loupanDb = loupanDao.findByResourceTypeAndResourceIdMoreDay(e.getKey().getResourceType(),e.getKey().getResourceId(), DateUtils.getDayStart(new Date()));
         if(loupanDb==null){
             loupanDao.insert(e.getKey());
             if(e.getValue()==null || e.getValue().isEmpty()){
                 return;
             }
             tagDao.batchInsert(e.getValue().entrySet().stream().map(map->{
                 Tag tag = new Tag();
                 tag.setLoupanId(e.getKey().getId());
                 tag.setType(map.getKey());
                 tag.setValue(map.getValue());
                 return tag;
             }).collect(Collectors.toList()));
         }else {
             if(loupanDb.getPrice().compareTo(e.getKey().getPrice())!=0) {
                 loupanDb.setPrice(e.getKey().getPrice());
                 loupanDao.update(loupanDb);
             }
         }
      });
    }
}
