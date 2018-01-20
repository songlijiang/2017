package com.slj.crawler.anjuke.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slj.crawler.AbstractDiscoverCrawler;
import com.slj.crawler.LoupanService;
import com.slj.crawler.entity.Loupan;
import com.slj.crawler.pipeline.LoupanPipleLine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by slj on 17/2/19.
 */
@Component
@Slf4j
public class AnjukePageProcessor extends AbstractDiscoverCrawler implements PageProcessor {

    private static String cityName ="xa";

    public static String anjukuUrl = ".fang.anjuke.com/loupan";


    @Autowired
    private LoupanPipleLine loupanPipleLine;

    @Autowired
    private LoupanService loupanService;

    @Override
    public void process(Page page) {
       Document document =  page.getHtml().getDocument();
       Element list = document.getElementsByClass("key-list").first();
       page.putField("housePair",list.getElementsByClass("item-mod").stream().map(e->{
           try {
               String href = e.select("a.pic").attr("href");
               String resourceId =executeMatch("fang.anjuke.com/loupan/(\\d*).html",href,1);
               Element info = e.getElementsByClass("infos").first();
               String name = info.getElementsByClass("lp-name").first().text();
               String address = info.getElementsByClass("address").first().text();
               String area = info.getElementsByClass("address").first().nextElementSibling().text();
               Element favor = e.getElementsByClass("favor-pos").first();
               String priceText = "均价0元";
               try {
                   priceText = favor.text();
               } catch (Exception e1) {
                   e1.printStackTrace();
               }
               Map tagMap = Maps.newHashMap();
               BigDecimal price = StringUtils.isNumeric(executeMatch("均价(\\d)元",priceText,1))?new BigDecimal(priceText):BigDecimal.ZERO;
               return Pair.of(loupanService.getLouPan(resourceId,name,address,area,price,cityName, Loupan.TYPE_ANJUKE),tagMap);
           } catch (Exception e1) {
               e1.printStackTrace();
           }
           return null;
       }).filter(Objects::nonNull).collect(Collectors.toList())
       );
       if(CollectionUtils.isNotEmpty(document.select("span.next-page.stat-disable"))){
           return;
       }else {
           int currentPage = 1;
           try {
              currentPage = Integer.parseInt(executeMatch("fang.anjuke.com/loupan/all/p(\\d)", page.getRequest().getUrl(), 1));
           } catch (Exception e) {
               e.printStackTrace();
           }
           page.addTargetRequest(getUrl()+"/all/p"+(currentPage+1)+"/");
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
        return loupanPipleLine;
    }

    @Override
    protected List<String> getStartUrls() {
        return Lists.newArrayList(getUrl()+"/all/p1/");
    }

    private String getUrl(){
        return "http://"+cityName+anjukuUrl;
    }

    @Override
    public void discoverAll(String cityName) {
        super.discoverAll(cityName);
    }

    @Scheduled(cron = "0 5/10 * * * ?")
    public void run(){
        discoverAll(cityName);
    }

    public static void main(String[] args) {
        AnjukePageProcessor anjukePageProcessor = new AnjukePageProcessor();
        anjukePageProcessor.discoverAll("xa");
    }

}

