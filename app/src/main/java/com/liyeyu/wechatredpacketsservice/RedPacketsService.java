package com.liyeyu.wechatredpacketsservice;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import java.util.List;

public class RedPacketsService extends AccessibilityService {

    public static final String KEY_RED_PACKET_1 = "[微信红包]";
    public static final String KEY_RED_PACKET_2 = "领取红包";
    AccessibilityNodeInfo mNodeInfo;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String className = event.getClassName().toString();
        Log.i("className",className);
        switch (eventType){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if(className.equals(TextView.class.getName())){
                    getPacket();
                }
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    openPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    close();
                }
                break;
        }
    }

    /**
     * 关闭红包详情界面,实现自动返回聊天窗口
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void close() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//        if (nodeInfo != null) {
//            //为了演示,直接查看了关闭按钮的id
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("@id/ft");
//            nodeInfo.recycle();
//            for (AccessibilityNodeInfo item : list) {
//                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//        }
    }

    /**
     * 模拟点击,拆开红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            //为了演示,直接查看了红包控件的id
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId("@id/bdg");
            rootNode.recycle();
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 模拟点击,打开抢红包界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//        parseNode(rootNode);
//        if(mNodeInfo==null){
//            mNodeInfo = rootNode;
//        }
//        nodeClick(mNodeInfo);
//        mNodeInfo = null;
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(KEY_RED_PACKET_2);
        for (AccessibilityNodeInfo node:list) {
            nodeClick(node);
        }
    }

    private void nodeClick(AccessibilityNodeInfo node){
        //模拟点击，注意这个节点只是包含“领取红包”关键词而已，并不一定是红包触发节点
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        //触发父节点的点击事件
        AccessibilityNodeInfo parent = node.getParent();
        while (parent!=null){
            if(parent.isClickable()){
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            parent = parent.getParent();
        }
    }

    /**
     * 从根节点解析出红包节点
     * @param rootNode
     * @return
     */
    private AccessibilityNodeInfo parseNode(@NonNull AccessibilityNodeInfo rootNode) {
        //当前是不含子节点的节点
        CharSequence text = rootNode.getText();
        if(rootNode.getChildCount() == 0){
            if(text!=null && KEY_RED_PACKET_2.equals(text.toString())){
                Log.i("parseNode",text.toString());
                mNodeInfo = rootNode;
                return rootNode;
            }
        }else{
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                AccessibilityNodeInfo child = rootNode.getChild(i);
                parseNode(child);
            }
        }
        return rootNode;
    }

    private void handleNotification(AccessibilityEvent event) {
        //获取事件的文本信息，检查是否含[微信红包]
        List<CharSequence> sequences = event.getText();
        if(sequences!=null && !sequences.isEmpty()){
            for (CharSequence content:sequences) {
                if(content.toString().contains(KEY_RED_PACKET_1)){
                    //是否是通知消息
                    Parcelable parcelableData = event.getParcelableData();
                    if(parcelableData!=null && parcelableData instanceof Notification){
                        Notification notification = (Notification) parcelableData;
                        PendingIntent pendingIntent = notification.contentIntent;
                        //模拟点击
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }




    @Override
    protected void onServiceConnected() {

/*        //4.0之后提供meta-data 节点配置
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;//接收所有事件通知
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;//通用的反馈方式
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 100;//接受事件的时间间隔,通常将其设置为100即可.
        info.packageNames = new String[]{"com.tencent.mm"};//接收事件的app包名
        //CanRetrieveWindowContent 表示该服务能否访问活动窗口中的内容.也就是如果你希望在服务中获取窗体内容的化,则需要设置其值为true.
        setServiceInfo(info);*/
    }

    @Override
    public void onInterrupt() {

    }

}
