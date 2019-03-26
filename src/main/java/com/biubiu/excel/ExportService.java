package com.biubiu.excel;

import java.util.List;
import java.util.Map;

/**
 * Created by Haibiao.Zhang on 2019-03-26 11:35
 */
public interface ExportService {

    int queryCountByMap(Map map);

    List queryResultByMap(Map map);

}
