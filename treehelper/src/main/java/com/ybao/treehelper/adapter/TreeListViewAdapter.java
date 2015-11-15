
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
 * Author：Ybao on 2015/8/3 17:47
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.treehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ybao.treehelper.model.Node;
import com.ybao.treehelper.utils.TreeSortFilterUtil;

import java.util.List;

public abstract class TreeListViewAdapter<T> extends BaseAdapter {

    protected Context mContext;
    /**
     * 存储所有可见的Node
     */
    protected List<Node> nodeTree;
    protected LayoutInflater mInflater;


    /**
     * 点击的回调接口
     */
    private OnTreeNodeClickListener onTreeNodeClickListener;

    public interface OnTreeNodeClickListener {
        void onClick(Node node, int position);
    }

    public void setOnTreeNodeClickListener(OnTreeNodeClickListener onTreeNodeClickListener) {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }

    /**
     * @param mTree
     * @param context
     * @param datas
     * @param defaultExpandLevel 默认展开几级树
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public TreeListViewAdapter(ListView mTree, Context context, List<T> datas, int defaultExpandLevel) {
        mContext = context;
        /**
         * 获取排序后的树
         */
        nodeTree = TreeSortFilterUtil.getInitNodeTree(datas, defaultExpandLevel);
        mInflater = LayoutInflater.from(context);

        /**
         * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
         */
        mTree.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Node node = nodeTree.get(position);
                if (node == null) {
                    return;
                }
                if (node.isLeaf()) {
                    if (onTreeNodeClickListener != null) {
                        onTreeNodeClickListener.onClick(nodeTree.get(position), position);
                    }
                } else {
                    if (nodeTree.get(position).isExpand()) {
                        collapse(node, position);
                    } else {
                        expand(node, position);
                    }
                }
            }

        });

    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     *
     * @param position
     */
    public void expand(Node n, int position) {
        nodeTree.addAll(position + 1, n.getVisibleChildrenNode());
        n.setExpand(true);
        notifyDataSetChanged();// 刷新视图
    }

    public void collapse(Node n, int position) {
        nodeTree.removeAll(n.getVisibleChildrenNode());
        n.setExpand(false);
        notifyDataSetChanged();// 刷新视图
    }

    @Override
    public int getCount() {
        return nodeTree.size();
    }

    @Override
    public Object getItem(int position) {
        return nodeTree.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Node node = nodeTree.get(position);
        convertView = getConvertView(node, position, convertView, parent);
        return convertView;
    }

    public abstract View getConvertView(Node node, int position, View convertView, ViewGroup parent);

}