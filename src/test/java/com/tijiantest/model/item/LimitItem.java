/**
 * 
 */
package com.tijiantest.model.item;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.tijiantest.util.PinYinUtil;
import com.tijiantest.util.key.PrimaryKeyGenerator;

/**
 * 受限项目及其占用个数
 * 
 * @author ren
 *
 */
public class LimitItem implements PrimaryKeyGenerator{
	
	public static Integer TOTAL_NUM_ITEM_ID = -1;
	
	public static Integer ITEM_IMPORT_ITEM_FOCUS = 1;//重要项
	
	/**
	 * 受限项目id
	 */
	private Integer itemId;
	
	/**
	 * 受限项目名称
	 */
	private String itemName;
	
	/**
	 * 受限项目占用个数
	 */
	private int count;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	public String getItemName() {
		return PinYinUtil.fullWidth2halfWidth(itemName);
	}

	public void setItemName(String itemName) {
		this.itemName = PinYinUtil.fullWidth2halfWidth(itemName);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public LimitItemKey getPrimaryKey() {
		return new LimitItemKey(itemId);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
