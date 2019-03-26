package com.biubiu.test;

import com.biubiu.ExcelApplication;
import com.biubiu.controller.TaskController;
import com.biubiu.dto.TaskAddRequest;
import com.biubiu.po.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * Created by Haibiao.Zhang on 2019-03-26 14:18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ExcelApplication.class})// 指定启动类
public class TaskTest {

    @Autowired
    private TaskController taskController;

    @Test
    public void addTask() {
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
        taskController.task(request);
    }

}
