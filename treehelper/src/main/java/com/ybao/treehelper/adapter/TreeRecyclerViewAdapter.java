
/**
 * Copyright 2015 Pengyuan-Jiang
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Author：Ybao on 2015/8/11 22:00
 * <p>
 * QQ: 392579823
 * <p>
 * Email：392579823@qq.com
 */
package com.ybao.treehelper.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;

import com.ybao.treehelper.adapter.holder.TreeViewHolder;
import com.ybao.treehelper.adapter.holder.TreeViewHolder.OnViewHolderListener;
import com.ybao.treehelper.model.Node;
import com.ybao.treehelper.utils.TreeSortFilterUtil;

import java.util.List;

public abstract class TreeRecyclerViewAdapter<VH extends TreeViewHolder, T> extends Adapter<VH> implements
        OnViewHolderListener {
    /**
     * 存储所有可见的Node
     */
    protected List<Node> nodeTree;
    protected boolean singleBranch = false;

    protected int lsatExpandParentId = -1;

    protected int defaultExpandLevel = 0;


    public TreeRecyclerViewAdapter(List<T> datas, int defaultExpandLevel) {
        setData(datas, defaultExpandLevel);
    }

    public TreeRecyclerViewAdapter(List<T> datas) {
        setData(datas);
    }

    public TreeRecyclerViewAdapter() {
    }

    public void setData(List<T> datas) {
        nodeTree = TreeSortFilterUtil.getInitNodeTree(datas, defaultExpandLevel);
    }

    public void setData(List<T> datas, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        nodeTree = TreeSortFilterUtil.getInitNodeTree(datas, defaultExpandLevel);
    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     */
    private int expand(View v, Node n, int position) {
        if (onExpandListener != null) {
            onExpandListener.onExpand(v, n, position);
        }
        return expand(n, position);
    }

    public int expand(Node n, int position) {
        n.setExpand(true);

        List<Node> nodes = n.getVisibleChildrenNode();
        int count = nodes.size();

        nodeTree.addAll(position + 1, nodes);
        notifyItemRangeInserted(position + 1, count);
        return count;
    }

    private int collapse(View v, Node n, int position) {
        if (onCollapseListener != null) {
            onCollapseListener.onCollapse(v, n, position);
        }
        return collapse(n, position);
    }

    public int collapse(Node n, int position) {

        List<Node> nodes = n.getVisibleChildrenNode();
        int count = nodes.size();

        n.setExpand(false);

        nodeTree.removeAll(nodes);
        notifyItemRangeRemoved(position + 1, count);
        return count;
    }

    @Override
    public void onItemClick(View v, int position) {
        if (position < 0) {
            return;
        }
        Node node = nodeTree.get(position);
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            if (onLeafNodeClickListener != null) {
                onLeafNodeClickListener.onLeafNodeClick(v, node, position);
            }
        } else {
            if (node.isExpand()) {
                collapse(v, node, position);
                lsatExpandParentId = -1;
            } else {
                if (lsatExpandParentId >= 0 && singleBranch) {
                    Node lsatExpandNoed = findRightLsatExpandNoed(node.getParent(), nodeTree.get(lsatExpandParentId));
                    if (lsatExpandNoed.getLevel() == node.getLevel()) {
                        lsatExpandParentId = nodeTree.indexOf(lsatExpandNoed);
                        int count = collapse(v, lsatExpandNoed, lsatExpandParentId);
                        if (position > lsatExpandParentId) {
                            position -= count;
                        }
                    }
                }
                expand(v, nodeTree.get(position), position);
                lsatExpandParentId = position;
            }
            if (onParentNodeClickListener != null && onParentNodeClickListener.onParentNodeClick(v, node, position)) {
                return;
            }
        }
    }


    private Node findRightLsatExpandNoed(Node parent, Node lsatExpandNoed) {
        while (true) {
            if (lsatExpandNoed.getParent() == null || parent == lsatExpandNoed.getParent()) {
                break;
            }
            lsatExpandNoed = lsatExpandNoed.getParent();
        }
        return lsatExpandNoed;
    }


    @Override
    public int getItemCount() {
        if (nodeTree != null) {
            return nodeTree.size();
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Node node = nodeTree.get(position);
        onBindViewHolder(node, holder, position);
        holder.setOnViewHolderListener(this);
    }

    public abstract void onBindViewHolder(Node node, VH holder, int position);

    public interface OnCollapseListener {
        void onCollapse(View v, Node node, int groupPosition);
    }

    private OnCollapseListener onCollapseListener;

    public void setOnCollapseListener(OnCollapseListener onCollapseListener) {
        this.onCollapseListener = onCollapseListener;
    }

    public interface OnExpandListener {
        void onExpand(View v, Node node, int groupPosition);
    }

    private OnExpandListener onExpandListener;

    public void setOnExpandListener(OnExpandListener onExpandListener) {
        this.onExpandListener = onExpandListener;
    }

    public interface OnParentNodeClickListener {
        boolean onParentNodeClick(View v, Node node, int groupPosition);
    }

    private OnParentNodeClickListener onParentNodeClickListener;

    public void setOnParentClickListener(OnParentNodeClickListener onParentNodeClickListener) {
        this.onParentNodeClickListener = onParentNodeClickListener;
    }

    /**
     * 点击的回调接口
     */

    public interface OnLeafNodeClickListener {
        void onLeafNodeClick(View v, Node node, int position);
    }

    private OnLeafNodeClickListener onLeafNodeClickListener;

    public void setOnLeafNodeClickListener(OnLeafNodeClickListener onLeafNodeClickListener) {
        this.onLeafNodeClickListener = onLeafNodeClickListener;
    }

    public void setSingleBranch(boolean singleBranch) {
        if (this.singleBranch != singleBranch) {
            this.singleBranch = singleBranch;
            if (nodeTree != null) {
                TreeSortFilterUtil.reSetNodeTree(nodeTree, defaultExpandLevel);
                notifyDataSetChanged();
            }
            lsatExpandParentId = -1;
        }
    }

    public Node getNodeItem(int position) {
        if (position >= 0 && position < nodeTree.size()) {
            return nodeTree.get(position);
        }
        return null;
    }

    public List<Node> getNodeTree() {
        return nodeTree;
    }

    public int getDefaultExpandLevel() {
        return defaultExpandLevel;
    }

    public boolean isSingleBranch() {
        return singleBranch;
    }
}
