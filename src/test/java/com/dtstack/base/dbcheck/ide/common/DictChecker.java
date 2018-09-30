package com.dtstack.base.dbcheck.ide.common;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.common.Dict;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictChecker extends BaseTest {

    public static String selectContentFragment = "id,type,dict_name,dict_value,dict_name_zh,dict'_name_en,dict_sort,gmt_create,gmt_modified,is_deleted";

    public static List<Dict> listByType(Integer type) throws SqlException {
        String sql = "select " + selectContentFragment + " from rdos_dict where type=? and is_deleted=0 order by dict_sort ASC";
        List<Map<String, Object>> list = DBMapper.query(sql, type);

        List<Dict> dicts = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Dict dict = new Dict();
            dicts.add(dict);
        }
        return dicts;
    }

    public static Dict map2Dict(Map<String, Object> map) {
        Dict dict = new Dict();
        dict.setId(Long.valueOf(map.get("id").toString()));
        dict.setType(Integer.valueOf(map.get("type").toString()));
        dict.setDictName(map.get("dict_name").toString());
        dict.setDictValue(Integer.valueOf(map.get("dict_value").toString()));
        dict.setDictNameZH(map.get("dict_name_zh").toString());
        dict.setDictNameEN(map.get("dict_name_en").toString());
        dict.setDictSort(Integer.valueOf(map.get("dict_sort").toString()));
        dict.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        return dict;
    }
}
