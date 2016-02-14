package com.ybao.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<SimpleNode> simpleNodes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            SimpleNode simpleNode = new SimpleNode();
            simpleNode.id = i;
            simpleNode.pid = 0;
            simpleNode.name = i + "";
            simpleNodes.add(simpleNode);
        }

        for (int i = 11; i <= 50; i++) {
            SimpleNode simpleNode = new SimpleNode();
            simpleNode.id = i;
            simpleNode.pid = i / 10;
            simpleNode.name = simpleNode.pid + "-" + i;
            simpleNodes.add(simpleNode);
        }
        for (int i = 51; i <= 300; i++) {
            SimpleNode simpleNode = new SimpleNode();
            simpleNode.id = i;
            simpleNode.pid = i / 10;
            simpleNode.name = simpleNode.pid + "-" + i;
            simpleNodes.add(simpleNode);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_tree);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SimpleTreeAdapter mAdapter = new SimpleTreeAdapter();
        mAdapter.setSingleBranch(true);
        mAdapter.setChangeGroup(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setData(simpleNodes);
    }
}
