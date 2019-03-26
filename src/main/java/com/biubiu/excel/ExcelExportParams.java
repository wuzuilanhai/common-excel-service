package com.biubiu.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by Haibiao.Zhang on 2019-03-26 11:34
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelExportParams {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 导出文件名称
     */
    private String fileName;

    /**
     * 查询参数
     */
    private Map<String, Object> params;

    /**
     * 查询service
     */
    private ExportService exportService;

    /**
     * 返回实体
     */
    private Class clazz;

    /**
     * excel头部数组
     */
    private List<String> assetHeadTemp;

    /**
     * excel字段数据
     */
    private List<String> assetNameTemp;

}
