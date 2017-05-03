package com.yds.grouphelper;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.yds.grouphelper.common.database.GroupDB;
import com.yds.grouphelper.common.utils.MD5Util;
import com.yds.grouphelper.common.utils.Utils;

import java.util.ArrayList;

/**
 * 微信6.3.13
 */
@SuppressLint({ "NewApi", "Wakelock" })
public class AccessibilityReadGroup extends AccessibilityService {

    private static final String SP_NAME="group_record";

    private Session mSession;
    private Handler handler;

    private volatile int STEP=0;
    private volatile boolean scrollResult;
    private volatile boolean groupScroll;

    private String groupName;
    private String groupKey;
    private ArrayList<String> groupList=new ArrayList<>();
    private AccessibilityNodeInfo mNodeInfo;
    private String recordStr;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
		/*通过这个函数可以接收系统发送来的AccessibilityEvent，接收来的AccessibilityEvent是经过过滤的，过滤是在配置工作时设置的。*/
        final int eventType = event.getEventType();
        mNodeInfo= event.getSource();

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//0x00000020
            if (null != mNodeInfo) {
                //Utils.e("---------------"+nodeInfo.getClassName());
                Utils.e("STEP===="+STEP);
                switch (STEP){
                    case 0:
                        try{
                            if("com.tencent.mm.ui.mogic.WxViewPager".equals(mNodeInfo.getChild(0).getClassName())){
                                //Utils.e("=========这是主页面============");
                                performClick(mNodeInfo.getChild(3),0);
                                performClick(mNodeInfo.getChild(2),0);
                                return;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case 1:
                        try{
                            AccessibilityNodeInfo tagNode=mNodeInfo.getChild(0).getChild(1);
                            if(getNodeText(tagNode).startsWith("群聊")){
                                AccessibilityNodeInfo listview=mNodeInfo.getChild(0).getChild(4);
                                tagNode=listview.getChild(0).getChild(0);
                                if(getNodeText(tagNode).startsWith("群聊")){
                                    //Utils.e("=============这是群聊列表===============");
                                    if(!groupItem(listview)){
                                        groupScroll=true;
                                        groupScroll(listview);
                                    }
                                    return;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        try{
                            recordStr="";
                            AccessibilityNodeInfo listview=mNodeInfo.getChild(0).getChild(1);
                            if("android.widget.ListView".equals(listview.getClassName())){
                                traverseChat(listview);
                            }
                            return;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case 3:
                        groupList.clear();
                        try{
                            if("com.tencent.mm.ui.mogic.WxViewPager".equals(mNodeInfo.getChild(0).getClassName())){
                                STEP=0;
                                Utils.e("================开始循环遍历==============================");
                                performClick(mNodeInfo.getChild(1),0);
                                return;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
            return;
        }
        if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {//0x00001000
            //Utils.e("++++++++++++++++++++++++++++++++"+mNodeInfo.getClassName());
            if (null != mNodeInfo) {
                //logNodeInfo(nodeInfo);
                switch (STEP){
                    case 0:
                        try{
                            if("com.tencent.mm.ui.mogic.WxViewPager".equals(mNodeInfo.getClassName())){
                                AccessibilityNodeInfo clickNode=mNodeInfo.getChild(1).getChild(0).getChild(1);
                                STEP=1;
                                performClick(clickNode,100);
                                return;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case 1:
                        if(groupScroll){
                            try{
                                if("android.widget.ListView".equals(mNodeInfo.getClassName())){
                                    if(!groupItem(mNodeInfo)){
                                        groupScroll(mNodeInfo);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 2:
                        groupScroll=false;
                        try{
                           traverseChat(mNodeInfo);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case 3:
                        try{
                            if("com.tencent.mm.ui.mogic.WxViewPager".equals(mNodeInfo.getChild(0).getClassName())){
                                performClick(mNodeInfo.getChild(2),0);
                                return;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                }
            }
        }
    }

    private void traverseChat(final AccessibilityNodeInfo listview){
        if(listview.getChildCount()==0){
            logNodeInfo(listview);
        }
        for(int i=listview.getChildCount()-1;i>=0;--i){
            if(traverseNode(listview.getChild(i))){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishGroupDetail(listview);
                    }
                },2000);
                return;
            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollResult=listview.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                if(scrollResult){
                    if(null!=handler)handler.removeCallbacksAndMessages(null);
                }else{
                    if(null==handler) handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!scrollResult){
                                finishGroupDetail(listview);
                            }
                        }
                    },2000);

                }
            }
        },200);
    }

    private void groupScroll(final AccessibilityNodeInfo listview){
        if(groupName.endsWith("个群聊")){
            STEP=3;
            Utils.e("-------------遍历完了所有群组-------------------"+groupList.size());
            try{
                AccessibilityNodeInfo backNode=listview.getParent().getChild(0).getChild(0);
                performClick(backNode,100);
                performClick(listview.getParent().getChild(0),100);
            }catch (Exception e){
                Utils.e(""+e.toString());
            }
        }else{
            listview.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
    }

    private boolean groupItem(AccessibilityNodeInfo listview){
        if(listview!=null){
            int count=listview.getChildCount();
            for(int i=0;i<count;++i){
                String gName=getGroupName(listview.getChild(i));
                if(!groupList.contains(gName)){
                    groupName=gName;
                    if(!groupName.endsWith("个群聊")){
                        STEP=2;
                        Utils.e("------------------------------->"+groupName);
                        groupList.add(groupName);
                        groupKey=MD5Util.string2MD5(groupName);
                        performClick(listview.getChild(i),100);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getGroupName(AccessibilityNodeInfo item){
        int count=item.getChildCount();
        if(count==1){
            return getNodeText(item.getChild(0));
        }else{
            return getNodeText(item.getChild(count-1));
        }
    }

    private void finishGroupDetail(AccessibilityNodeInfo listview){
        try{
            if(!TextUtils.isEmpty(recordStr)){
                SharedPreferences sp=getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString(groupKey,recordStr);
                editor.commit();
            }

            STEP=0;
            //退出当前页面
            AccessibilityNodeInfo parent=listview.getParent().getParent();
            int count=parent.getChildCount();
            AccessibilityNodeInfo backNode=parent.getChild(count-3);
            performClick(backNode,100);

        }catch (Exception e){
            Utils.e("退出页面异常"+e.toString());
            try{
                AccessibilityNodeInfo parent=mNodeInfo.getChild(0);
                int count=parent.getChildCount();
                AccessibilityNodeInfo backNode=parent.getChild(count-3);
                performClick(backNode,100);
            }catch (Exception e1){}
        }
    }

    private boolean traverseNode(AccessibilityNodeInfo node) {
        if(null==node) return false;
        int count = node.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                if(null!=childNode){
                    if(traverseNode(childNode)){
                       return true;
                    }
                }
            }
        } else {
            CharSequence text = node.getText();
            if (null != text && text.length() > 0) {
                String str = text.toString();
                if(str.contains("邀请") && str.contains("加入了群聊")){
                    //Utils.e("====>"+str);
                    /*if(hasSave(str)){
                        //遍历过
                        Utils.e(str+"---->数据库已经有数据了");
                    }else{
                        Utils.e(str+"---->上传服务器");
                    }*/



                    /*str=str.split("加入了群聊")[0];
                    String key=str.split("邀请")[0];
                    String nameList=str.split("邀请")[1];
                    Utils.e(key+"====>"+nameList);

                    nameList=nameList.substring(1,nameList.length()-1);
                    String[] nameArray=nameList.split("、");*/
                    if(isRecord(str)) return true;
                }

                if(str.contains("月") && str.contains("日") && str.contains(" ") && str.contains(":")){
                    if(isRecord(str)) return true;
                }
            }
        }
        return false;
    }


    private boolean isRecord(String str){
        if(TextUtils.isEmpty(recordStr)){
            recordStr=MD5Util.string2MD5(str);
        }
        SharedPreferences sp=getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        String oldValue=sp.getString(groupKey,"");
        String newValue=MD5Util.string2MD5(str);
        if(newValue.equals(oldValue)){
            Utils.e("===================读到了上次位置=========================");
            return true;
        }
        return false;

    }

    private AccessibilityNodeInfo getNodeByText(AccessibilityNodeInfo node,String text){
        if(null==node) return null;
        int count = node.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                AccessibilityNodeInfo resultNode=getNodeByText(childNode,text);
                if(resultNode!=null){
                    return resultNode;
                }
            }
        } else {
            String nodeText=getNodeText(node);
            if(nodeText.equals(text)){
                return node;
            }
        }
        return null;
    }

    private String getNodeText(AccessibilityNodeInfo node){
        if(null!=node){
            int count = node.getChildCount();
            if(count<=0){
                CharSequence text = node.getText();
                if (null != text && text.length() > 0) {
                    return text.toString();
                }
            }
        }
        return "";
    }

    private void logNodeInfo(AccessibilityNodeInfo node) {
        if(null==node) return;
        int count = node.getChildCount();
        Utils.e(count + "=>>>" + node.getClassName());
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                logNodeInfo(childNode);
            }
        } else {
            String nodeText=getNodeText(node);
            if(!TextUtils.isEmpty(nodeText)){
                Utils.e("===>>>" + nodeText);
            }
        }
    }

    private void performClick(final AccessibilityNodeInfo clickNode, long delay){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result= clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if(!result){
                    performClick(clickNode,1000);
                }
            }
        },delay);
    }

    private void performScroll(final AccessibilityNodeInfo listview, long delay, final boolean forward){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(forward){
                    listview.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }else{
                    listview.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                }
            }
        },delay);
    }


    @Override
    public void onInterrupt() {
    	/*系统想要中断AccessibilityService返给的响应时会调用。在整个生命周期里会被调用多次。*/
        Utils.e("=======onInterrupt============");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Utils.e("======onServiceConnected==========");
		/*系统会在成功连接上你的服务的时候调用这个方法，在这个方法里你可以做一下初始化工作，例如设备的声音震动管理，也可以调用setServiceInfo()进行配置工作。
		 * 也可以在我们的xml里面配置我们的Service的信息,这里我也是在xml里面配置的信息
		 * */
        handler=new Handler();
        mSession = Session.getInstance(this);
        mSession.openService(true);
        createDB();
    }

    @Override
    public boolean onUnbind(Intent intent) {
		/*在系统将要关闭这个AccessibilityService会被调用。在这个方法中进行一些释放资源的工作。*/
        Utils.e("======onUnbind==========");
        handler.removeCallbacksAndMessages(null);
        mSession.openService(false);
        return super.onUnbind(intent);
    }


    private void createDB() {
        GroupDB dbHelper = new GroupDB(this);
        if (!dbHelper.tableIsExist(GroupDB.TABLE_INVITATION)) {
            dbHelper.creatTable(GroupDB.CREAT_TABLE_INVITATION);
        }
        dbHelper.close();
    }


    //===========================================================
    private boolean hasSave(String content){
        boolean result=false;
        String sql = "SELECT * FROM " + GroupDB.TABLE_INVITATION+" where state = 1";
        GroupDB dbHelper = new GroupDB(this);
        Cursor cur = dbHelper.getReadableDatabase().rawQuery(sql, null);
        if (cur.moveToFirst()) {
            do {
                if(content.equals(cur.getString(1))){
                    result=true;
                    break;
                }
            } while (cur.moveToNext());
            cur.close();
        }

        if(!result){
            //插入
            ContentValues cv = new ContentValues();
            cv.put("content",content);
            cv.put("state",new Integer(1));

            SQLiteDatabase sqlitedb = dbHelper.getWritableDatabase();
            sqlitedb.insert(GroupDB.TABLE_INVITATION,null, cv);
            sqlitedb.close();
        }
        dbHelper.close();
        return result;
    }

}