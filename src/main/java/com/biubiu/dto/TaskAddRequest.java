package com.biubiu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Haibiao.Zhang on 2019-03-26 12:34
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskAddRequest {

    private String taskId;

    private String username;

    private String fileName;

    private String serviceName;

    private Class respClazzName;

    private List<String> assetHeadTemp;

    private List<String> assetNameTemp;

}
