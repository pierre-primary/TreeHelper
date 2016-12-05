
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
 * Author：Ybao on 2015/8/11 19:23
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.treehelper.model;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private int id;
    // 根节点pId为0
    private int pId = 0;
    private int sortId = 0;
    private String name;

    //是否展开
    private boolean isExpand = false;

    // 下一级的子Node
    private List<Node> children = new ArrayList<Node>();

    // 父Node
    private Node parent;

    //原数据
    private Object data;
    private RecyclerView.ViewHolder holder;

    public Node() {
    }

    public Node(int id, int pId, int sortId, String name) {
        super();
        this.id = id;
        this.pId = pId;
        this.sortId = sortId;
        this.name = name;
    }

    public int getId() {
        return id;
    }


    public int getpId() {
        return pId;
    }


    public int getSortId() {
        return sortId;
    }


    public String getName() {
        return name;
    }


    public boolean isExpand() {
        return isExpand;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        parent.getChildren().add(this);
        this.parent = parent;
    }

    /**
     * 是否为跟节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 是否是叶子界点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 获取level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * 设置展开
     *
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {

            for (Node node : children) {
                node.setExpand(isExpand);
            }
        }
    }

    public List<Node> getVisibleChildrenNode() {
        List<Node> result = new ArrayList<Node>();
        if (isExpand()) {
            getVisibleChildrenNode(result, getChildren());
        }
        return result;
    }

    private static void getVisibleChildrenNode(List<Node> result, List<Node> rootNodes) {
        for (Node node : rootNodes) {
            result.add(node);
            if (node.isExpand()) {
                getVisibleChildrenNode(result, node.getChildren());
            }
        }
    }

    public void setHolder(RecyclerView.ViewHolder holder) {
        this.holder = holder;
    }

    public RecyclerView.ViewHolder getHolder() {
        return holder;
    }
}
