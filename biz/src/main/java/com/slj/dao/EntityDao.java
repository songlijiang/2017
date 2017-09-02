package com.slj.dao;



import com.slj.entity.Entity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.Collection;
import java.util.List;

public interface EntityDao<T extends Entity> {

    int insert(T t);
    T findById(int id);
    int update(T t);
    List<T> queryInIds(@Param("ids") Collection<Integer> ids);

    List<T> queryAll(RowBounds rowBounds);

    int batchInsert(List<T> items);

}
