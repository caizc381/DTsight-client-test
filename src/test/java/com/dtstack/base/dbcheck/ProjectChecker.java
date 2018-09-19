package com.dtstack.base.dbcheck;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.Project;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectChecker extends BaseTest {
    public static List<Project> getAllProjectByTenantId(int tenantId) throws SqlException {
        List<Project> projects = new ArrayList<>();
        String sql = "select rp.id,rp.tenant_id,rp.project_name,rp.project_alias,rp.project_Identifier,"
                + " rp.project_desc,rp.status,rp.create_user_id,rp.gmt_create,rp.gmt_modified,rp.is_deleted,"
                + " rp.project_type,rp.produce_project_id,rp.schedule_status"
                + " from rdos_project rp left join rdos_project_stick rps on rps.project_id = rp.id"
                + " where rp.is_deleted=0 and rp.tenant_id=? order by rps.stick desc, rp.gmt_modified desc";
        List<Map<String, Object>> list = DBMapper.query(sql, tenantId);
        for (Map<String, Object> map : list) {
            Project project = new Project();
            project.setId(Long.valueOf(map.get("id").toString()));
            project.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
            project.setProjectName(map.get("project_name").toString());
            project.setProjectAlias(map.get("project_alias").toString());
            project.setProjectIdentifier(map.get("project_Identifier").toString());
            if (map.get("project_desc") != null) {
                project.setProjectDesc(map.get("project_desc").toString());
            }

            project.setStatus(Integer.valueOf(map.get("status").toString()));
            project.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
            project.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
            project.setProjectType(Integer.valueOf(map.get("project_type").toString()));
            if (map.get("produce_project_id") != null) {
                project.setProduceProjectId(Long.valueOf(map.get("produce_project_id").toString()));
            }

            project.setScheduleStatus(Integer.valueOf(map.get("schedule_status").toString()));
            projects.add(project);
        }
        return projects;
    }
}
