package com.slj.crawler.dao;

import com.piaoniu.annotations.DaoGen;
import com.slj.dao.EntityDao;
import com.slj.crawler.entity.Tag;

/**
 * Created by slj on 17/2/11.
 */
@DaoGen(tablePrefix = "N_",tableName = "Tag")
public interface TagDao extends EntityDao<Tag> {
}
