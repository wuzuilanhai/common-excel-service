create table t_task(
  id VARCHAR(64) PRIMARY KEY COMMENT '主健',
  file_name VARCHAR(64) COMMENT '导出文件名称',
  file_path VARCHAR(128) COMMENT '导出文件路径',
  params VARCHAR(1280) COMMENT '查询参数的json字符串',
  start_time datetime COMMENT '任务开始时间',
  end_time datetime COMMENT '任务结束时间',
  creator VARCHAR(64) COMMENT '创建者',
  create_time TIMESTAMP DEFAULT NOW() COMMENT '创建时间',
  editor VARCHAR(64) COMMENT '修改者',
  edit_time TIMESTAMP DEFAULT NOW() COMMENT '修改时间',
  is_delete INT DEFAULT 0 COMMENT '删除标志'
) COMMENT '导出任务表' CHARSET utf8mb4;