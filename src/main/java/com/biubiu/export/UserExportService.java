package com.biubiu.export;

import com.biubiu.excel.ExportService;
import com.biubiu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Haibiao.Zhang on 2019-03-26 12:25
 */
@Service("userExport")
public class UserExportService implements ExportService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public int queryCountByMap(Map map) {
        return userMapper.queryCountByMap(map);
    }

    @Override
    public List queryResultByMap(Map map) {
        return userMapper.queryResultByMap(map);
    }

}
