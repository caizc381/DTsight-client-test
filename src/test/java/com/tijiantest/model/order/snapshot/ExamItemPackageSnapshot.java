package com.tijiantest.model.order.snapshot;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.resource.meal.UserPackagesInfo;

/**
 * 订单的加项包快照
 *
 * @create 2016年11月9日 上午10:45:29
 * @author tangyi
 * @version
 */
public class ExamItemPackageSnapshot extends UserPackagesInfo {

    private static final long serialVersionUID = -7931934184448907079L;

    public String buildSnapshotJSONString() {
        PropertyFilter filter = new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (source instanceof ExamItemPackageSnapshot) {
                    List<String> includeProperties = Arrays.asList("packages", "examItemInfos");
                    if (includeProperties.contains(name)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                if (source instanceof ExamItem) {
                    List<String> includeProperties = Arrays.asList("id", "name", "gender",
                            "hospitalId", "mealId", "price", "groupId", "discount", "hisItemId",
                            "itemType", "departmentId", "syncPrice", "tagName");
                    if (includeProperties.contains(name)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            }
        };
        return JSON.toJSONString(this, filter);
    }
}
