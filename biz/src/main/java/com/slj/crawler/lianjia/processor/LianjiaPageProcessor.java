package com.slj.crawler.lianjia.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slj.crawler.AbstractDiscoverCrawler;
import com.slj.crawler.LoupanService;
import com.slj.crawler.lianjia.domain.LianjiaPage;
import com.slj.crawler.entity.Loupan;
import com.slj.crawler.pipeline.LoupanPipleLine;
import com.slj.util.DateUtils;
import com.slj.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by slj on 17/2/11.
 */
@Service
@Slf4j
public class LianjiaPageProcessor extends AbstractDiscoverCrawler implements us.codecraft.webmagic.processor.PageProcessor {

    private static String cityName ="xa";

    public static String lianjiaUrl = ".fang.lianjia.com/loupan";

    @Autowired
    private LoupanPipleLine lianjiaPipleLine;

    @Autowired
    private LoupanService loupanService;

    @Override
    public void process(Page page) {
      Document document =  page.getHtml().getDocument();
     List<Element> houseListParent =document.getElementsByClass("house-lst");
     if(CollectionUtils.isEmpty(houseListParent)){
         return;
     }
        List<Element> houseList = houseListParent.get(0).getElementsByAttributeValue("data-index","0");
     if(CollectionUtils.isEmpty(houseList)){
         return;
     }
     String pattern = "/loupan/(.*)/";
     String pricePattern = "均价 (\\d*) 元/平";
     page.putField("housePair",houseList.stream().map(e->{
         try {
             Element pic = e.getElementsByClass("pic-panel").get(0).getElementsByTag("a").first();
             String housePic  = pic.attr("href");
             String resourceId = executeMatch(pattern,housePic,1);
             Element info = e.getElementsByClass("info-panel").first().getElementsByClass("col-1").first();
             String name = info.getElementsByTag("h2").text();
             String region = info.getElementsByClass("where").first().text();
             String area = info.getElementsByClass("area").first().text();
             Map tag = Maps.newHashMap();
             Element redTagElemt = info.getElementsByClass("redTag").first();
             String redTag ="";
             if(redTagElemt!=null){
                 redTag = redTagElemt.text();
             }
             tag.put("redTag",redTag);
             Element type = info.getElementsByClass("type").first();
             type.getElementsByTag("span").stream().forEach(span->tag.put(span.attr("class"),span.text()));
             Element priceElemet = e.getElementsByClass("average").first();
             String priceText ="0";
             if(priceElemet!=null && StringUtils.isNotEmpty(priceElemet.text())){
                 String match  = executeMatch(pricePattern,priceElemet.text(),1);
                 if(StringUtils.isNotEmpty(match)){
                     priceText = match;
                 }
             }
             BigDecimal price = BigDecimal.valueOf(Double.parseDouble(priceText));
             return Pair.of(loupanService.getLouPan(resourceId,name,region,area,price,cityName,Loupan.TYPE_LIANJIA),tag);
         } catch (Exception e1) {
             e1.printStackTrace();
             return null;
         }
     }).filter(e->e!=null).collect(Collectors.toList()));
     Element pageElement = document.getElementsByClass("house-lst-page-box").first();
     String pageData = pageElement.attr("page-data");
     LianjiaPage lianjiaPage = null;
    try {
       lianjiaPage =JsonUtils.fromStr(pageData,LianjiaPage.class);
    } catch (IOException e) {
        e.printStackTrace();
    }
    if(lianjiaPage!=null && lianjiaPage.getCurPage()<lianjiaPage.getTotalPage()){
        page.addTargetRequest(getLianjiaUrl()+"/pg"+(lianjiaPage.getCurPage()+1));
    }

    }




    public String executeMatch(String pattern , String target,int groupId){
        Matcher matcher = Pattern.compile(pattern).matcher(target);
        if(matcher.find()){
            return matcher.group(groupId);
        }else {
            log.error("not match for pattern {} , target {} ",pattern,target);
            return "";
        }
    }






    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(5).setSleepTime(0).setTimeOut(5000).setCycleRetryTimes(3).setCharset("utf-8");
    }

    @Override
    protected us.codecraft.webmagic.processor.PageProcessor getPageProcessor(String cityName) {
        this.cityName=cityName;

        return this;
    }

    @Override
    protected Pipeline getPipeline() {
        return lianjiaPipleLine;
    }

    @Override
    protected List<String> getStartUrls() {
        return Lists.newArrayList(getLianjiaUrl()+"/pg1/");
    }

    private String getLianjiaUrl(){
       return "http://"+cityName+lianjiaUrl;
    }


    @Scheduled(cron = "0 0/10 * * * ?")
    public void run(){
        discoverAll(cityName);
    }

    public static void main(String[] args) {
        LianjiaPageProcessor lianjiaPageProcessor = new LianjiaPageProcessor();
        lianjiaPageProcessor.discoverAll("xa");
    }
}
