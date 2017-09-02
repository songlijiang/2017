package com.slj.zk;

import com.google.common.primitives.Longs;
import com.slj.config.AllConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author code4crafter@gmail.com
 *         Date: 16/6/28
 *         Time: 下午4:56
 */
@Slf4j
public class ZkConfig {

	public static final String CHARSET_NAME = "utf-8";
	private static Map<String,NodeCache> nodeCaches = new HashMap<>();

	private static CuratorFramework client;

	static {
		init();
	}

	public static void register(String path, Runnable runnable) {
		NodeCache nodeCache = getNodeCache(path);
		nodeCache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				runnable.run();
			}
		});
	}

	public static String getCacheServer() {
		return get("/cache/servers");
	}

	public static String getFilterPatterns(){
		return ZkConfig.get("/front/error/filterPatterns");
	}

	public static String get(String path) {
		try {
			return getValue0(path);
		} catch (Exception e) {
			log.error("get zk value error {}", path, e);
			return null;
		}
	}

	public static boolean lock(String path, Runnable runnable, long timeout, TimeUnit timeUnit){
		InterProcessSemaphoreMutex mutex = new InterProcessSemaphoreMutex(client,path);
		try{
			mutex.acquire(timeout,timeUnit);
			runnable.run();
			return true;
		} catch (Exception ignored) {
			return false;
		}
		finally {
				try {
					if (mutex.isAcquiredInThisProcess()){
						mutex.release();
					}
				} catch (Exception e) {
					//there is nothing i can do...
				}
		}
	}

	public static void createOrUpdate(String path, String value) {
		try {
			if (client.checkExists().forPath(path) == null) {
				client.createContainers(path);
			}
			client.setData().forPath(path, value.getBytes(CHARSET_NAME));
		} catch (Exception e) {
			log.error("create error {} {}", new Object[] { path, value }, e);
			throw new RuntimeException("保存失败");
		}
	}

	private
	static String getValue0(String path) throws Exception {
		byte[] bytes = client.getData().forPath(path);
		if (bytes == null) {
			log.warn("get null value for path {}", path);
			return null;
		}
		return new String(bytes, CHARSET_NAME);
	}

	private static NodeCache getNodeCache(String path) {
		ensure(path);
		NodeCache nodeCache = nodeCaches.get(path);
		if (nodeCache == null) {
			synchronized (ZkConfig.class) {
				nodeCache = new NodeCache(client, path);
				nodeCaches.put(path, nodeCache);
				try {
					nodeCache.start(true);
				} catch (Exception e) {
					log.error("start node cache fail", nodeCache);
				}
			}
		}
		return nodeCache;
	}

	private static void ensure(String path) {
		try {
			if (client.checkExists().creatingParentContainersIfNeeded().forPath(path) == null)
                client.create().creatingParentContainersIfNeeded().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void emit(String path) {
		try {
			client.setData().forPath(path, Longs.toByteArray(System.currentTimeMillis()));
		} catch (Exception e) {
			log.error("emit error {}", path, e);
		}
	}

	public static void init() {
		//AllConfig.init();
		connect();

	}



	@PreDestroy
	public static void closeCurrent() {
		nodeCaches.forEach((k, v) -> {
			try {
				v.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		if (client != null) {
			CloseableUtils.closeQuietly(client);
		}
	}

	public static void connect() {
		try {
			client = CuratorFrameworkFactory
					.newClient(AllConfig.getZkServers(), new RetryNTimes(10, 3000));
			client.start();
		} catch (Exception e) {
			log.error("init zk client error", e);
		}
	}

	public static String getDBConfig() {
		return get("/config/db");
	}

	public static String getSolrUrl() {
		return get("/search/solr");
	}

	public static String getStatDBConfig() {
		return get("/datasource/pns");
	}
}
