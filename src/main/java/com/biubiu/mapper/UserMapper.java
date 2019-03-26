package com.biubiu.mapper;

import com.biubiu.po.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Haibiao.Zhang on 2019-03-26 11:11
 */
@Repository
public interface UserMapper extends Mapper<User> {

    int queryCountByMap(Map map);

    List queryResultByMap(Map map);

}
