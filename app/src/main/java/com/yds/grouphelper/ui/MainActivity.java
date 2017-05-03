package com.yds.grouphelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.yds.grouphelper.Constants;
import com.yds.grouphelper.R;
import com.yds.grouphelper.Session;
import com.yds.grouphelper.common.adapter.AdapterListener;
import com.yds.grouphelper.common.adapter.RecyclerAdapter;
import com.yds.grouphelper.common.adapter.RecyclerDivider;
import com.yds.grouphelper.common.entity.Group;
import com.yds.grouphelper.common.utils.MyAnimation;
import com.yds.grouphelper.common.utils.RootCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements View.OnClickListener,Observer,AdapterListener {

    private Session mSession;
    private ImageView switchView;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ArrayList<Group> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSession=Session.getInstance(this);
        mSession.addObserver(this);

        startService(new Intent(this,HeartbeatService.class));

        switchView=(ImageView) findViewById(R.id.main_checkbox);
        switchView.setOnClickListener(this);
        findViewById(R.id.topbar_menu).setOnClickListener(this);

        recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.addItemDecoration(new RecyclerDivider(this,R.drawable.divider));
        adapter=new RecyclerAdapter(this, list, R.layout.item_group);
        adapter.setAdapterListener(this,true,true,false);
        recyclerView.setAdapter(adapter);

        setSwitch();
        RootCmd.haveRoot();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_checkbox:
            {
                Intent intent =  new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
            break;

            case R.id.topbar_menu:
                MyAnimation.clickAnim(v, new MyAnimation.MyAnimListner() {
                    @Override
                    public void animEnd(View view) {
                        //添加群
                    }
                });
                break;

            default:break;
        }
    }

    private void setSwitch(){
        if(mSession.whetherOpen()){
            switchView.setImageResource(R.mipmap.switch_on);
        }else{
            switchView.setImageResource(R.mipmap.switch_off);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        try{
            HashMap<String, Object> map=(HashMap<String, Object>) data;
            if(map.containsKey(Constants.NOTIFY_ONOFF)){
                setSwitch();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void adapterListener(int actionType, int position, View view, Object object) {

    }
}
