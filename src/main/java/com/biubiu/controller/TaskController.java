package com.biubiu.controller;

import com.alibaba.fastjson.JSON;
import com.biubiu.annotation.Slave;
import com.biubiu.consumer.TaskConsumer;
import com.biubiu.dto.TaskAddRequest;
import com.biubiu.mapper.TaskMapper;
import com.biubiu.po.Task;
import com.biubiu.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Created by Haibiao.Zhang on 2019-03-26 11:30
 */
@Slf4j
@RestController
@RequestMapping("/rest")
public class TaskController {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskConsumer taskConsumer;

    @GetMapping("/test")
    @Slave
    public String test() {
        TaskAddRequest request = TaskAddRequest.builder()
                .username(null)
                .fileName("user")
                .serviceName("userExport")
                .respClazzName(User.class)
                .assetHeadTemp(
                        Arrays.asList("用户ID", "用户姓名", "用户密码", "盐值",
                                "创建人", "创建时间", "修改人", "修改时间", "是否删除"))
                .assetNameTemp(
                        Arrays.asList("id", "username", "password", "salt",
                                "creator", "createTime", "editor", "editTime", "isDelete"))
                .build();
        return this.task(request);
    }

    @GetMapping("/test2")
    @Slave
    public String test2() {
        TaskAddRequest request = TaskAddRequest.builder()
                .username("张海彪")
                .fileName("user")
                .serviceName("userExport")
                .respClazzName(User.class)
                .assetHeadTemp(
                        Arrays.asList("用户ID", "用户姓名", "用户密码", "盐值",
                                "创建人", "修改人"))
                .assetNameTemp(
                        Arrays.asList("id", "username", "password", "salt",
                                "creator", "editor"))
                .build();
        return this.task(request);
    }

    /**
     * 添加导出任务
     */
    @PostMapping("/task")
    @Transactional
    @Slave
    public String task(@RequestBody TaskAddRequest request) {
        Task task = Task.builder()
                .fileName(request.getFileName())
                .params(JSON.toJSONString(request))
                .build();
        if (taskMapper.insertSelective(task) == 0) throw new RuntimeException("add task error!");
        request.setTaskId(task.getId());
        taskConsumer.addTask(request);
        return "add task success!";
    }

}
