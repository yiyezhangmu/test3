package com.coolcollege.intelligent.util;

import cn.hutool.core.collection.CollUtil;
import com.coolcollege.intelligent.model.department.dto.SyncTreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/2 16:17
 */
public class TreeUtil {

    public static List<SyncTreeNode> parseTree(SyncTreeNode node) {
        List<SyncTreeNode> result = new ArrayList<>();
        if (node != null) {
            List<SyncTreeNode> child = node.getChild();
            if (CollUtil.isNotEmpty(child)) {
                node.setChild(null);
            }
            result.add(node);
            parseTreeList(child, result);
        }
        return result;
    }

    public static void parseTreeList(List<SyncTreeNode> nodes, List<SyncTreeNode> result) {
        for (SyncTreeNode node : nodes) {
            List<SyncTreeNode> child = node.getChild();
            if (CollUtil.isNotEmpty(child)) {
                node.setChild(null);
            }
            result.add(node);
            parseTreeList(child, result);
        }
    }

}
