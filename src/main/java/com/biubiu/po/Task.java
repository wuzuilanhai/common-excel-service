package com.biubiu.po;

import com.biubiu.util.KeyGenUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Haibiao.Zhang on 2019-03-26 11:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_task")
public class Task {

    @Id
    @KeySql(genId = KeyGenUtil.class)
    private String id;

    private String fileName;

    private String filePath;

    private String params;

    private Date startTime;

    private Date endTime;

    private String creator;

    private Date createTime;

    private String editor;

    private Date editTime;

    private Integer isDelete;

}
