package com.stock.calculator.app;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;


/**
 * Created by Yehuijifeng
 * on 2015/10/27.
 * activity的收藏夹
 */
public class ActivityCollector {

    //保存整个app所有活动的activity
    private static LinkedHashMap<String, Activity> activityMap = new LinkedHashMap<>();

    private static String getKey(Activity activity) {
        return getKey(activity.getClass());
    }

    private static String getKey(Class<?> clazz) {
        if (clazz == null) return "";
        return clazz.getSimpleName();
    }

    //获得最上层的activity
    public static Activity getActivityTop() {
        if (activityMap.size() == 0) return null;
        Collection<Activity> collection = activityMap.values();
        Object[] activities = collection.toArray();
        return (Activity) activities[activities.length - 1];
    }

    //添加activity
    public static void addActivity(Activity activity) {
        if (activity == null) return;
        activityMap.put(getKey(activity), activity);
    }

    //从集合中移除activity
    public static void removeActivity(Activity activity) {
        if (activity != null)
            activityMap.remove(getKey(activity));
    }

    //当前activity是否在栈顶
    public static boolean checkActivityTop(Activity activity) {
        if (activity == null) return false;
        Activity topActivity = getActivityTop();
        if (topActivity == null) return false;
        return activity == topActivity;
    }

    //该操作用于清空当前app栈中所有activity，用于退出程序所用
    public static synchronized void finishAll() {
        ArrayList<Activity> activities = new ArrayList<>(activityMap.values());
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    //该操作用于清空当前app栈中所有activity，保留目标activity
    public static void finishAllByKeep(Activity activity) {
        if (activity == null) return;
        ArrayList<Activity> activities = new ArrayList<>(activityMap.values());
        for (Activity activity1 : activities)
            if (activity != activity1)
                activity1.finish();
    }

    //该操作用于清空当前app栈中所有activity，保留目标activity
    public static void finishAllByKeep(Class<?> clazz) {
        ArrayList<Activity> activities = new ArrayList<>(activityMap.values());
        String keepClassName = getKey(clazz);
        for (Activity activity : activities)
            if (!keepClassName.equalsIgnoreCase(getKey(activity)))
                activity.finish();
    }

    //finish 目标activity头上的所有activity
    public static void finishByTarget(Activity activity) {
        if (activity == null) return;
        ArrayList<Activity> activities = new ArrayList<>(activityMap.values());
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity1 = activities.get(i);
            if (activity1 == activity)
                return;
            else
                activity1.finish();
        }
    }

    //finish 目标activity头上的所有activity
    public static void finishByTarget(Class<?> clazz) {
        if (clazz == null) return;
        ArrayList<Activity> activities = new ArrayList<>(activityMap.values());
        String key = getKey(clazz);
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity1 = activities.get(i);
            String key1 = getKey(activity1);
            if (key1.equals(key))
                return;
            else
                activity1.finish();
        }
    }

    //finish多个activity
    public static void finishActivitys(Class<?>... classs) {
        if (classs == null) return;
        for (Class<?> clazz : classs) {
            if (clazz == null) continue;
            Activity activity = activityMap.get(getKey(clazz));
            if (activity != null) activity.finish();
        }
    }

    //当前栈中是否包含目标activity
    public static boolean isContain(Activity activity) {
        if (activity == null) return false;
        return activityMap.containsValue(activity);
    }

    //当前栈中是否包含目标activity
    public static boolean isContain(Class<?> clazz) {
        if (clazz == null) return false;
        return activityMap.containsKey(getKey(clazz));
    }

    /**
     * 杀死该应用进程
     */
    public static void killProcess() {
        finishAll();
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
