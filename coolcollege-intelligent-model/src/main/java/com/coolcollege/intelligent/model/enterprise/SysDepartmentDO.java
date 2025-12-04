package com.coolcollege.intelligent.model.enterprise;

import com.coolcollege.intelligent.common.constant.Constants;
import com.google.common.base.Strings;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


/**
 * @ClassName SysDepartmentDO
 * @Description
 * @author 王春辉
 */
@Data
public class SysDepartmentDO {
    /**
     * 部门id
     */
    private String id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门id，根部门为1
     */
    private String parentId;

    /**
     * 在父部门中的次序值
     */
    private Integer departOrder;

    /**
     * 当群已经创建后，是否有新人加入部门会自动加入该群, true表示是, false表示不是
     */
    private Boolean autoAddUser;

    /**
     * 部门的主管列表,取值为由主管的userid组成的字符串，不同的userid使用|符号进行分割
     */
    private String deptManagerUseridList;

    /**
     * 定义的部门id
     */
    private String defineDepartmentId;

    /**
     * 是否需要同步到区域
     */
    private Boolean isSyncRegion;

    /**
     * 是否是门店
     */
    private Boolean isStore;

    /**
     * 是否是叶子节点
     */
    private Boolean isLeaf;


    private String parentIds;

    private String subIds;

    /**
     * 同步批次id
     */
    private Long syncId;


    public SysDepartmentDO(String id, String name, String parentId, Integer departOrder) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.departOrder = departOrder;
    }

    public SysDepartmentDO() {

    }

    /**
     * 构造函数
     *
     * @param nodeId 节点Id
     */
    public SysDepartmentDO(String nodeId) {
        this.id = nodeId;
    }

    /**
     * 构造函数
     *
     * @param nodeId   节点Id
     * @param parentId 父节点Id
     */

    public SysDepartmentDO(String nodeId, String parentId) {
        this.id = nodeId;
        this.parentId = parentId;
    }


    /**
     * 设置部门名称
     *
     * @param name 部门名称
     */
    public void setName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            this.name = name.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "");
            if (StringUtils.isBlank(this.name)) {
                this.name = "空";
            }
        }
    }

    /**
     * 获取父部门id，根部门为1
     *
     * @return parent_id - 父部门id，根部门为1
     */
    public String getParentId() {
        if (Constants.ROOT_DEPT_ID_STR.equals(id)) {
            return null;
        }
        return parentId;
    }

    @Override
    public String toString() {
        return "SysDepartmentDO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", departOrder=" + departOrder +
                ", isSyncRegion=" + isSyncRegion +
                ", isStore=" + isStore +
                ", isLeaf=" + isLeaf +
                ", parentIds='" + parentIds + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysDepartmentDO that = (SysDepartmentDO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
