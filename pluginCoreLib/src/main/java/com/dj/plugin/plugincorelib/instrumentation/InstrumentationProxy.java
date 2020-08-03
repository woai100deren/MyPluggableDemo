package com.dj.plugin.plugincorelib.instrumentation;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.ContextThemeWrapper;

import com.dj.plugin.plugincorelib.manager.PluginResourceMergeManager;
import com.dj.plugin.plugincorelib.utils.RefInvokeUtils;

public class InstrumentationProxy extends Instrumentation {
    private Instrumentation mInstrumentation;

    public InstrumentationProxy(Instrumentation instrumentation) {
        this.mInstrumentation = instrumentation;
    }

    // 返回值改成true, 重写异常处理方法，在遇到异常时，程序不崩溃，
    @Override
    public boolean onException(Object obj, Throwable e){
        e.printStackTrace();
        return true;
    }

    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        if (className != null) {
            Activity activity = mInstrumentation.newActivity(cl, className, intent);
            activity.setIntent(intent);
            try {
                //替换插件的resources为合并后的resources（也可以在这里处理为每个插件自己的）
                RefInvokeUtils.setFieldObject(ContextThemeWrapper.class, activity, "mResources", PluginResourceMergeManager.mResources);//注意此处的代码
            } catch (Exception e2) {
                // ignored.
                e2.printStackTrace();
            }
            return activity;
        }
        return mInstrumentation.newActivity(cl, className, intent);
    }

}
