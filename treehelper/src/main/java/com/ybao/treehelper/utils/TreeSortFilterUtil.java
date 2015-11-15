
/**
 * Copyright 2015 Pengyuan-Jiang
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Author：Ybao on 2015/8/11 20:00
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.treehelper.utils;

import com.ybao.treehelper.annotation.NodeId;
import com.ybao.treehelper.annotation.NodeName;
import com.ybao.treehelper.annotation.NodePid;
import com.ybao.treehelper.annotation.NodeSortId;
import com.ybao.treehelper.model.Node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeSortFilterUtil {

    public static <T> List<Node> getInitNodeTree(List<T> datas, int defaultExpandLevel)

    {
        List<Node> nodes;
        try {
            // 将用户数据转化为List<Node>
            nodes = Objects2Tree(datas);
        } catch (Exception e) {
            nodes = new ArrayList<>();
        }

        return reSetNodeTree(nodes, defaultExpandLevel);
    }

    public static <T> List<Node> reSetNodeTree(List<Node> nodes, int defaultExpandLevel) {
        // 拿到根节点
        List<Node> rootNodes = getRootNodes(nodes);
        // 排序以及设置Node间关系
        List<Node> result = new ArrayList<Node>();
        reSetNode(result, rootNodes, defaultExpandLevel, 0);
        return result;
    }

    /**
     * 将用户数据转化为List<Node>
     */
    private static <T> List<Node> Objects2Tree(List<T> datas) throws IllegalArgumentException, IllegalAccessException

    {
        List<Node> nodes = new ArrayList<Node>();
        Node node = null;

        for (T t : datas) {
            int id = -1;
            int pId = -1;
            int sortId = -1;
            String label = null;
            Class<? extends Object> clazz = t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                if (f.getAnnotation(NodeId.class) != null) {
                    f.setAccessible(true);
                    id = f.getInt(t);
                }
                if (f.getAnnotation(NodePid.class) != null) {
                    f.setAccessible(true);
                    pId = f.getInt(t);
                }
                if (f.getAnnotation(NodeSortId.class) != null) {
                    f.setAccessible(true);
                    sortId = f.getInt(t);
                }
                if (f.getAnnotation(NodeName.class) != null) {
                    f.setAccessible(true);
                    label = (String) f.get(t);
                }
                if (id != -1 && pId != -1 && label != null) {
                    break;
                }
            }
            node = new Node(id, pId, sortId, label);
            node.setData(t);
            nodes.add(node);
        }

        //设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系

        Collections.sort(nodes, new NodeSort());
        /**
         * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
         */
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node m = nodes.get(j);
                if (m.getpId() == n.getId() && m.getpId() != 0) {
                    m.setParent(n);
                } else if (m.getId() == n.getpId() && n.getpId() != 0) {
                    n.setParent(m);
                }
            }
        }

        return nodes;
    }

    private static List<Node> getRootNodes(List<Node> nodes) {
        List<Node> root = new ArrayList<Node>();
        for (Node node : nodes) {
            if (node.isRoot()) {
                root.add(node);
            }
        }
        return root;
    }


    /**
     *
     */
    private static void reSetNode(List<Node> result, List<Node> rootNodes, int defaultExpandLeval, int currentLevel) {
        for (Node node : rootNodes) {
            result.add(node);
            if (node.isLeaf()) {
                continue;
            } else if (defaultExpandLeval >= currentLevel + 1) {
                node.setExpand(true);
                reSetNode(result, node.getChildren(), defaultExpandLeval, ++currentLevel);
            }
        }
    }

}
