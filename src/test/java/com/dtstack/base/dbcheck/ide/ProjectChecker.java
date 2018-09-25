package com.dtstack.base.dbcheck.ide;

import com.dtstack.base.BaseTest;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.model.domain.ide.Project;
import com.dtstack.model.domain.ide.RoleUser;
import com.dtstack.model.dto.ide.ProjectDTO;
import com.dtstack.util.StringUtil;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectChecker extends BaseTest {
    public static List<Project> getAllProjectByTenantId(Long tenantId) throws SqlException {
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

    //获取该userId下的projectIds
    public static Set<Long> getUsefulProjectIds(Long userId, Long tenantId, Boolean isAdmin) throws SqlException {
        List<RoleUser> roleUsers = null;

        //isAdmin:true,所管理的项目，即roleValue>3
        if (BooleanUtils.isTrue(isAdmin)) {
            roleUsers = RoleUserChecker.getRoleUserIsAdmin(userId, tenantId);
        } else {
            roleUsers = RoleUserChecker.getRoleUserByUserId(userId, tenantId);
        }

        if (CollectionUtils.isEmpty(roleUsers)) {
            return null;
        }

        Set<Long> projectIds = roleUsers.stream().map(RoleUser::getProjectId).collect(Collectors.toSet());
        return projectIds;
    }

    public static Integer countByIdsAndFuzzyName(List<Long> projectIds, String fuzzyName, Long tenantId) throws SqlException {
        String projectIdStr = "";
        for (int i = 0; i < projectIds.size(); i++) {
            projectIdStr += projectIds.get(i) + ",";
        }
        if (!projectIdStr.equals("")) {
            projectIdStr = StringUtil.removeCommaAtEnd(projectIdStr);
        }
        String sql = "select count(1) from rdos_project rp where rp.tenant_id=? and rp.is_deleted=0 and rp.id in (" + projectIdStr + ")";
        List<Map<String, Object>> list = DBMapper.query(sql, tenantId);

        return Integer.valueOf(list.get(0).get("COUNT(1)").toString());
    }

    public static List<ProjectDTO> listJobSumByIdsAndFuzzyName(List<Long> projectIds, String fuzzyName, List<Integer> jobStatus, PageQuery pageQuery, Long tenantId) throws SqlException {
        String projectIdStr = "";
        for (int i = 0; i < projectIds.size(); i++) {
            projectIdStr += projectIds.get(i) + ",";
        }
        if (!projectIdStr.equals("")) {
            projectIdStr = StringUtil.removeCommaAtEnd(projectIdStr);
        }

        String sql = "select rp.id,rp.tenant_id,rp.project_name,rp.project_alias,rp.project_Identifier,rp.project_desc,rp.status,"
                + " rp.create_user_id,rp.gmt_create,rp.gmt_modified,rp.is_deleted,count(if(rebj.status in (8,9,21) and rbj.cyc_time>=#{time}) "
                + " and rbj.type=0,TRUE,NULL)) as jobSum,rps.stick from rdos_project rp "
                + " left join rdos_batch_job rbj on rbj.project_id=rp.id "
                + " left join rdos_engine_batch_job rebj on rebj.job_is=rbj.job_id "
                + " left join rdos_project_stick rps on rps.project_id = rp.id "
                + " where rp.tenant_id = ? and rp.is_deleted=0 "
                + " and rps.user_id=? and rp.id in (" + projectIdStr + ")";
        if (fuzzyName != null && !fuzzyName.equals("")) {
            sql += " and (rp.project_alias like '%" + fuzzyName + "%') or rp.project_name list '%" + fuzzyName + "%'";
        }
        sql += " Group by rp.id order by ";
        if (pageQuery.getOrderBy() != null) {
            sql += pageQuery.getOrderBy() + " " + pageQuery.getSort();
        } else {
            sql += " rps.stick " + pageQuery.getSort();
        }
        sql += ",rp.gmt_modifier desc";
        if (pageQuery.getStart() != 0 && pageQuery.getPageSize() != 0) {
            sql += " limit " + pageQuery.getStart() + "," + pageQuery.getPageSize();
        }

        if (pageQuery.getPage() == 0 && pageQuery.getPageSize() == 0) {
            sql += " limit 20";
        }

        List<Map<String, Object>> list = DBMapper.query(sql, tenantId);
        List<ProjectDTO> projectDTOS = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            ProjectDTO projectDTO = map2ProjectDTO(list.get(i));
            projectDTOS.add(projectDTO);
        }
        return projectDTOS;
    }

    public static ProjectDTO map2ProjectDTO(Map<String, Object> map) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(Long.valueOf(map.get("id").toString()));
        projectDTO.setTenantId(Long.valueOf(map.get("tenant_id").toString()));
        projectDTO.setProjectName(map.get("project_name").toString());
        projectDTO.setProjectAlias(map.get("project_alias").toString());
        projectDTO.setProjectIdentifier(map.get("project_Identifier").toString());
        projectDTO.setProjectDesc(map.get("project_desc").toString());
        projectDTO.setStatus(Integer.valueOf(map.get("status").toString()));
        projectDTO.setCreateUserId(Long.valueOf(map.get("create_user_id").toString()));
        projectDTO.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        projectDTO.setJobSum(Integer.valueOf(map.get("jobSum").toString()));
        projectDTO.setStick(Timestamp.valueOf(map.get("stick").toString()));
        return projectDTO;
    }
}
