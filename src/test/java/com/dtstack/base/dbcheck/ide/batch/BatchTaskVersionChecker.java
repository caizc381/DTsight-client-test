package com.dtstack.base.dbcheck.ide.batch;

import com.dtstack.base.BaseTest;
import com.dtstack.model.vo.ide.batch.BatchTaskVersionVO;
import com.dtstack.util.StringUtil;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BatchTaskVersionChecker extends BaseTest {

    public static List<BatchTaskVersionVO> getLastestTaskVersionByTaskIds(List<Long> taskIds) throws SqlException {
        String taskIdStr = "";
        for (int i = 0; i < taskIds.size(); i++) {
            taskIdStr += taskIds.get(i) + ",";
        }

        if (!taskIdStr.equals("")) {
            taskIdStr = StringUtil.removeCommaAtEnd(taskIdStr);
        }

        String sql = "select * from ("
                + " select * from rdos_batch_task_version where task_id in (" + taskIdStr + ") and is_deleted=0 order by id desc)"
                + " t group by t.task_id";
        List<Map<String, Object>> list = DBMapper.query(sql);

        List<BatchTaskVersionVO> batchTaskVersionVOS = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BatchTaskVersionVO vo = map2BatchTaskVersionVO(list.get(i));
            batchTaskVersionVOS.add(vo);
        }
        return batchTaskVersionVOS;
    }

    public static BatchTaskVersionVO map2BatchTaskVersionVO(Map<String, Object> map) {
        BatchTaskVersionVO batchTaskVersionVO = new BatchTaskVersionVO();
        batchTaskVersionVO.setId(Long.valueOf(map.get("id").toString()));
        batchTaskVersionVO.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        batchTaskVersionVO.setProjectId(Long.valueOf(map.get("project_id").toString()));
        batchTaskVersionVO.setTaskId(Long.valueOf(map.get("task_id").toString()));
        batchTaskVersionVO.setSqlText(map.get("sql_text").toString());
        batchTaskVersionVO.setPublishDesc(map.get("publish_desc").toString());
        batchTaskVersionVO.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
        batchTaskVersionVO.setVersion(Integer.valueOf(map.get("version").toString()));
        batchTaskVersionVO.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        batchTaskVersionVO.setTaskParams(map.get("task_params").toString());
        batchTaskVersionVO.setScheduleConf(map.get("schedule_conf").toString());
        batchTaskVersionVO.setScheduleStatus(Integer.valueOf(map.get("schedule_status").toString()));
        String dependencyTaskIds = map.get("dependency_task_ids").toString();
        String[] ids = dependencyTaskIds.split(",");
        batchTaskVersionVO.setDependencyTaskNames(Arrays.asList(ids));

        return batchTaskVersionVO;
    }
}
