package com.slj.crawler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 *         Date: 15/10/3
 *         Time: 下午8:56
 */
@Slf4j
@Service
public abstract class AbstractDiscoverCrawler {

	private Spider discoverIdSpider;

	public void init(String cityName) {
		discoverIdSpider = Spider.create(getPageProcessor(cityName))
				.thread(5).addPipeline(getPipeline());
	}

	protected abstract PageProcessor getPageProcessor(String cityName);

	protected abstract Pipeline getPipeline();

	protected abstract List<String> getStartUrls();


	public void discoverAll(String cityName) {
		init(cityName);

		log.info("start discover crawler");
		getStartUrls().forEach(url -> discoverIdSpider.addUrl(url));
		discoverIdSpider.run();
		log.info("stop discover crawler");
	}

}
