package com.biubiu.mapper;

import com.biubiu.po.Task;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created by Haibiao.Zhang on 2019-03-26 11:12
 */
@Repository
public interface TaskMapper extends Mapper<Task> {
}
