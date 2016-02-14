
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
 * Author：Ybao on 2015/8/11 22:00
 * <p/>
 * QQ: 392579823
 * <p/>
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
    protected boolean changeGroup = false;
    protected boolean singleBranch = true;

    int lsatExpandParentId = -1;
    int defaultExpandLevel = 0;

    private TreeRecyclerViewAdapter.OnCollapseListener onCollapseListener;
    private TreeRecyclerViewAdapter.OnExpandListener onExpandListener;
    private TreeRecyclerViewAdapter.OnParentNodeClickListener onParentNodeClickListener;
    private TreeRecyclerViewAdapter.OnLeafNodeClickListener onLeafNodeClickListener;


    public TreeRecyclerViewAdapter() {
    }

    public TreeRecyclerViewAdapter(List<T> datas, int defaultExpandLevel) {
        setData(datas, defaultExpandLevel);
    }


    public TreeRecyclerViewAdapter(List<T> datas) {
        setData(datas);
    }

    public void setData(List<T> datas) {
        nodeTree = TreeSortFilterUtil.getInitNodeTree(datas, defaultExpandLevel);
    }

    public void setData(List<T> datas, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        this.nodeTree = TreeSortFilterUtil.getInitNodeTree(datas, defaultExpandLevel);
    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     */
    public int expand(View v, Node n, int position) {
        n.setExpand(true);

        List<Node> nodes = n.getVisibleChildrenNode();
        int count = nodes.size();

        nodeTree.addAll(position + 1, nodes);
        if (changeGroup) {
            notifyItemChanged(position);
        }
        notifyItemRangeInserted(position + 1, count);
        this.onExpand(v, n, position);
        return count;
    }

    public int collapse(View v, Node n, int position) {

        List<Node> nodes = n.getVisibleChildrenNode();
        int count = nodes.size();

        n.setExpand(false);
        nodeTree.removeAll(nodes);
        if (changeGroup) {
            notifyItemChanged(position);
        }
        notifyItemRangeRemoved(position + 1, count);
        this.onCollapse(v, n, position);
        return count;
    }

    protected void onExpand(View v, Node n, int position) {
        if (this.onExpandListener != null) {
            this.onExpandListener.onExpand(v, n, position);
        }

    }

    protected void onCollapse(View v, Node n, int position) {
        if (this.onCollapseListener != null) {
            this.onCollapseListener.onCollapse(v, n, position);
        }

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
        if (nodeTree == null) {
            return 0;
        }
        return nodeTree.size();
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Node node = nodeTree.get(position);
        node.setHolder(holder);
        onBindViewHolder(node, holder, position);
        holder.setOnViewHolderListener(this);
    }

    public abstract void onBindViewHolder(Node node, VH holder, int position);

    public interface OnCollapseListener {
        void onCollapse(View v, Node node, int groupPosition);
    }


    public void setOnCollapseListener(OnCollapseListener onCollapseListener) {
        this.onCollapseListener = onCollapseListener;
    }

    public interface OnExpandListener {
        void onExpand(View v, Node node, int groupPosition);
    }


    public void setOnExpandListener(OnExpandListener onExpandListener) {
        this.onExpandListener = onExpandListener;
    }

    public interface OnParentNodeClickListener {
        boolean onParentNodeClick(View v, Node node, int groupPosition);
    }


    public void setOnParentClickListener(OnParentNodeClickListener onParentNodeClickListener) {
        this.onParentNodeClickListener = onParentNodeClickListener;
    }

    /**
     * 点击的回调接口
     */

    public interface OnLeafNodeClickListener {
        void onLeafNodeClick(View v, Node node, int position);
    }


    public void setOnLeafNodeClickListener(OnLeafNodeClickListener onLeafNodeClickListener) {
        this.onLeafNodeClickListener = onLeafNodeClickListener;
    }

    public void setSingleBranch(boolean singleBranch) {
        if (this.singleBranch != singleBranch) {
            this.singleBranch = singleBranch;
            if (this.nodeTree != null) {
                TreeSortFilterUtil.reSetNodeTree(this.nodeTree, this.defaultExpandLevel);
                this.notifyDataSetChanged();
            }

            this.lsatExpandParentId = -1;
        }

    }

    public Node getItemNode(int position) {
        if (nodeTree == null || nodeTree.size() <= position || position < 0) {
            return null;
        }
        return nodeTree.get(position);
    }

    public void setChangeGroup(boolean changeGroup) {
        this.changeGroup = changeGroup;
    }
}
