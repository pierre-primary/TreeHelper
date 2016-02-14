package com.ybao.simple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ybao.treehelper.adapter.TreeRecyclerViewAdapter;
import com.ybao.treehelper.model.Node;

import java.util.List;

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
 * Author：Ybao on 2015/11/15 0015 17:20
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：32579823@qq.com
 */
public class SimpleTreeAdapter extends TreeRecyclerViewAdapter<SimpleItemHolder, SimpleNode> {


    public void setData(List<SimpleNode> datas) {
        super.setData(datas);
    }

    @Override
    public void onBindViewHolder(Node node, SimpleItemHolder holder, int position) {
        if (node.isLeaf()) {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (node.isExpand()) {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.indicator_tree_item_open, 0);
        } else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.indicator_tree_item_close, 0);
        }
        holder.textView.setPadding(30 + node.getLevel() * 40, 30, 30, 30);
        holder.textView.setText(node.getName());
    }

    @Override
    public SimpleItemHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new SimpleItemHolder(textView);
    }

}

