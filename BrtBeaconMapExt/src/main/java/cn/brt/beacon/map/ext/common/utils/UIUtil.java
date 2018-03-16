/**
 * Copyright 2016 bingoogolapple
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
 */

package cn.brt.beacon.map.ext.common.utils;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * UI工具类
 */
public class UIUtil {
    private UIUtil() {
    }

    public static int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, AppContextUtil.getResources().getDisplayMetrics());
    }

    public static int sp2px(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, AppContextUtil.getResources().getDisplayMetrics());
    }

    public static int getColor(@ColorRes int id) {
        return AppContextUtil.getResources().getColor(id);
    }

    /**
     * 检查ItemView是否铺满整屏
     *
     * @param recyclerView
     * @return
     */
    public static boolean isFullItemView(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        //获取最后一个childView
        View lastChildView = recyclerView.getChildAt(childCount - 1);
//获取第一个childView
        View firstChildView = recyclerView.getChildAt(0);
        int top = firstChildView.getTop();
        int bottom = lastChildView.getBottom();
        //recycleView显示itemView的有效区域的bottom坐标Y
        int bottomEdge = recyclerView.getHeight() - recyclerView.getPaddingBottom();
        //recycleView显示itemView的有效区域的top坐标Y
        int topEdge = recyclerView.getPaddingTop();
        //第一个view的顶部小于top边界值,说明第一个view已经部分或者完全移出了界面
        //最后一个view的底部小于bottom边界值,说明最后一个view已经完全显示在界面
        //若满足这两个条件,说明所有子view已经填充满了recycleView,recycleView可以"真正地"滑动
        // true为满屏的recyceView
        return bottom <= bottomEdge && top < topEdge;
    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}