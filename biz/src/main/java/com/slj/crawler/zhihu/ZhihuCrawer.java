package com.slj.crawler.zhihu;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.List;

/**
 * Created by Albee on 2017/4/13.
 */
public class ZhihuCrawer implements PageProcessor {
    // 设置编码 ，超时时间，重试次数，
    private Site site = Site.me().setRetryTimes(10).setSleepTime(5000).setTimeOut(5000)
            .addCookie("Domain", "zhihu.com")
            .addCookie("z_c0", "Mi4wQUZCQ05UN3dqUXNBVUFMVzJtMXpDeGNBQUFCaEFsVk5DODBtV1FCQkFwYUs0ZlRQeTRtRGFhQzN3V3BOQlZuZ3NR|1493548112|215cb731810d778646df5362659099fedefa43ef")
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
    //问题的索引
    //https://www.zhihu.com/question/20902967
    private static final String URL_question = "^https://www\\.zhihu\\.com/question/\\d+$";
    //https://www.zhihu.com/question/19647535/answer/110944270
    private  static  final String URL_answer = "https://www\\.zhihu\\.com/question/\\d+/answer/\\d+";

    private static   String questionId ="" ;
    @Override
    public void process(Page page) {
        //页面为问题页，则将答案链接循环加入Downloader
        if(page.getUrl().regex(URL_question).match()){
            int total = 20;
            int time = total/20;
            for(int i=0;i<=time;i++){
                int  offset = i*20;
                int limit= total<(i+1)*20?total:((i+1)*20-1);
                String url  ="https://www.zhihu.com/api/v4/questions/"+questionId+"/answers?include=data%5B*%5D.is_normal%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Cmark_infos%2Ccreated_time%2Cupdated_time%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cupvoted_followees%3Bdata%5B*%5D.author.badge%5B%3F(type%3Dbest_answerer)%5D.topics&offset="+offset+"&limit="+limit+"&sort_by=default";
                page.addTargetRequest(url);
            }
            //某个具体答案详情页面，则获取详情信息 。
        }else if(page.getUrl().regex(URL_answer).match()){
            String questionTitle = page.getHtml().xpath("//h1[@class=QuestionHeader-title]/text()").toString();
            String user_Avatar  =  page.getHtml().xpath("//span[@class=AuthorInfo-avatarWrapper]//img/@src").toString();
            List<String> urlList  = page.getHtml().xpath("//div[@class=RichContent-inner]//img/@src").all();
           // String filePath = "D:\\图说知乎\\图片\\"+questionTitle;
            String title  =  questionTitle+"_"+user_Avatar;
            try {
                System.out.println(""+urlList+title);
               // DownLoadPics.downLoadPics(urlList, title, filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<String> id = new JsonPathSelector("$.data[*].id").selectList(page.getRawText());
            for(int i=0;i<id.size();i++){
                String answerUrl = "https://www.zhihu.com/question/"+questionId+"/answer/"+id.get(i);
                page.addTargetRequest(answerUrl);
            }
        }
    }
    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        questionId = "37787176";
        Spider.create(new ZhihuCrawer())
                .addUrl("https://www.zhihu.com/question/"+questionId)
                .thread(2)
                .run();

    }
}