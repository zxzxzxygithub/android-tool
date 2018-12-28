
package com.pkmg.tt;

import com.example.showpanda.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class ShareFloatView {

    private final static int BUTTON_DEFAULT = 0;

    public static final String FLOAT_AD_CLOSE = "float ad close";

    private static final int MSG_BACK_LOAD_DATA = 1;

    private final int REFRESH_TIME = 300;// 单面是 s

    private Activity mContext;

    // private AdListener mAdListener;

    private static WindowManager wm = null;

    private static WindowManager.LayoutParams wmParams = null;

    private int mFloatBtnBackgroundId;

    private int mScreenWidth;

    private int mScreenHeight;

    private int x;

    private int y;

    private int mTouchStartX;

    private int mTouchStartY;

    private int mX1, mX2;

    private int mY1, mY2;

    // 上次获取广告的时间
    private long mPreviousTime;

    // 用来标记是否发送traking的
    private boolean isTraking = true;

    boolean isShowingPop = false;

    private View view;

    private TextView tv_pkTextView;

    private TextView tv_activity;

    private View ll_text;

    private TextView tv_refresh;

    public ShareFloatView(Activity context) {

        this(context, BUTTON_DEFAULT);
        init(context, 0);

    }

    public ShareFloatView(Activity context, int btnResId) {
        init(context, btnResId);
    }

    /**
     * @Title:init
     * @Description:TODO
     * @param context
     * @param btnResId
     * @Return:void
     */
    private void init(Activity context, int btnResId) {
        this.mContext = context;
        this.mFloatBtnBackgroundId = btnResId;

        initScreenSize();

        initFloatButton(btnResId);
    }

    private void initScreenSize() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }

    private void initFloatButton(int btnResId) {
        if (view != null && wm != null) {
            wm.removeView(view);
            wm = null;
            view = null;
        }
        if (view == null) {

            view = LayoutInflater.from(mContext).inflate(R.layout.float_tewline, null);
        }
        View totalView = view.findViewById(R.id.FrameLayout1);
        totalView.setOnTouchListener(new DragOnTouchListener());
        tv_refresh = (TextView) view.findViewById(R.id.tv_refresh);
        tv_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PackageManager packageManager = mContext.getPackageManager();
                ActivityManager aManager = (ActivityManager) mContext
                        .getSystemService(mContext.ACTIVITY_SERVICE);
                List<RunningTaskInfo> runningTasks = aManager.getRunningTasks(1);
                RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                ComponentName topActivity = runningTaskInfo.topActivity;
                String packageName = topActivity.getPackageName();
                String activityNameString = topActivity.getShortClassName();
                tv_pkTextView.setText(packageName);
                tv_activity.setText(activityNameString);
            }
        });
        ll_text = view.findViewById(R.id.ll_text);

        View iv_cancel = view.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                wm.removeView(view);
            }
        });
        tv_pkTextView = (TextView) view.findViewById(R.id.tv_pkg);
        tv_activity = (TextView) view.findViewById(R.id.tv_activity);
        if (wm == null) {

            wm = (WindowManager) mContext.getApplicationContext().getSystemService(
                    Context.WINDOW_SERVICE);
        }
        if (wmParams == null) {

            wmParams = new WindowManager.LayoutParams();
        }

        // 设置window type
        // wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        /*
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
         * 即拉下通知栏不可见
         */
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 设置悬浮窗的长得宽
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        wmParams.x = 0;
        // wmParams.y = (mScreenHeight - 70);
        wmParams.y = (30);

        wm.addView(view, wmParams);
    }

    public void onPause() {
        // mBtn.setVisibility(View.GONE);
    }

    public void onResume() {
        // if (!isShowingPop)
        // mBtn.setVisibility(View.VISIBLE);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // if (isShowingPop) {
        // initScreenSize();
        // }

    };

    public void onDestory() {
        // wm.removeView(mBtn);
    }

    private void updateViewPosition() {

        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        wm.updateViewLayout(view, wmParams);
    }

    private void setBestPosition() {

        initScreenSize();

        int top = 100;
        int bottom = mScreenHeight - top;

        if (y < top) {
            wmParams.y = 0;
            wmParams.x = (int) (x - mTouchStartX);

            wm.updateViewLayout(view, wmParams);
            return;
        } else if (y > bottom) {
            wmParams.y = mScreenHeight;
            wmParams.x = (int) (x - mTouchStartX);

            wm.updateViewLayout(view, wmParams);
            return;
        }

        int leftPoisition = ((mScreenWidth / 2) - wmParams.x);
        if (leftPoisition > 0) {
            wmParams.x = 0;
            wmParams.y = (int) (y - mTouchStartY);
        } else {
            wmParams.x = mScreenWidth;
            wmParams.y = (int) (y - mTouchStartY);
        }
        wm.updateViewLayout(view, wmParams);

    }

    // ///////////////////////////////////// classes
    // ////////////////////////////////////

    /**
     * onTouchEvent的监听事件， 拖动的主要实现方法
     */
    class DragOnTouchListener implements OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {

            // 获取相对屏幕的坐标，即以屏幕左上角为原点
            x = (int) event.getRawX();
            y = (int) (event.getRawY() - 25); // 25是系统状态栏的高度

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    // 获取相对View的坐标，即以此View左上角为原点
                    mTouchStartX = (int) event.getX();
                    mTouchStartY = (int) event.getY();

                    mX1 = (int) event.getRawX();// 得到相对应屏幕左上角的坐标
                    mY1 = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_MOVE:

                    updateViewPosition();
                    break;
                case MotionEvent.ACTION_UP:

                    setBestPosition();

                    mX2 = (int) event.getRawX();
                    mY2 = (int) event.getRawY();
                    double distance = Math.sqrt(Math.abs(mX1 - mX2) * Math.abs(mX1 - mX2)
                            + Math.abs(mY1 - mY2) * Math.abs(mY1 - mY2));// 两点之间的距离
                    // 距离较小，当作click事件来处理
                    if (distance < 15)
                        return false;
                    else
                        return true;
            }
            return false;
        }

    }

}
