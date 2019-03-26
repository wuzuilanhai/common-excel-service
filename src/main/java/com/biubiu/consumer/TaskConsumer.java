package com.biubiu.consumer;

import com.biubiu.constants.DatabaseType;
import com.biubiu.datasource.DatabaseContextHolder;
import com.biubiu.dto.TaskAddRequest;
import com.biubiu.excel.ExcelExportParams;
import com.biubiu.excel.ExcelUtil;
import com.biubiu.mapper.TaskMapper;
import com.biubiu.po.Task;
import com.biubiu.util.ApplicationContextHolder;
import com.biubiu.util.CustomBeanUtil;
import com.biubiu.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by Haibiao.Zhang on 2019-03-26 13:37
 */
@Component
@Slf4j
public class TaskConsumer implements InitializingBean {

    private static final int QUEUE_SIZE = 0x00010000;

    private BlockingQueue<TaskAddRequest> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    @Autowired
    private TaskMapper taskMapper;

    public void addTask(TaskAddRequest request) {
        queue.add(request);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            while (true) {
                TaskAddRequest request;
                try {
                    request = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error(e.getLocalizedMessage());
                    continue;
                }

                updateTaskStartTime(request);

                if (request == null) continue;
                String fileName = request.getFileName();
                ExcelExportParams params;
                try {
                    params = ExcelExportParams.builder()
                            .taskId(request.getTaskId())
                            .fileName(fileName)
                            .params(CustomBeanUtil.bean2map(request, TaskAddRequest.class))
                            .assetHeadTemp(request.getAssetHeadTemp())
                            .assetNameTemp(request.getAssetNameTemp())
                            .clazz(request.getRespClazzName())
                            .exportService(ApplicationContextHolder.getBean(request.getServiceName()))
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getLocalizedMessage());
                    continue;
                }
                ThreadPoolUtil.addTask(() -> {
                    try {
                        ExcelUtil.excelExport(params, taskMapper);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getLocalizedMessage());
                    }
                });
            }
        }).start();
    }

    private void updateTaskStartTime(TaskAddRequest request) {
        String taskId = request.getTaskId();
        Task task = Task.builder().id(taskId).startTime(new Date()).build();
        try {
            DatabaseContextHolder.setDatabaseType(DatabaseType.SLAVE);
            if (taskMapper.updateByPrimaryKeySelective(task) == 0) {
                throw new RuntimeException("update task [taskId : " + taskId + "] start time error");
            }
        } finally {
            DatabaseContextHolder.clearDatabaseType();
        }
    }

}
