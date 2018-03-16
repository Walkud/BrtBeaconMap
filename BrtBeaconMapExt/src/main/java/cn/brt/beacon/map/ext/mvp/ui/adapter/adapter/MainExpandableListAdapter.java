package cn.brt.beacon.map.ext.mvp.ui.adapter.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import cn.brt.beacon.map.ext.R;
import cn.brt.beacon.map.ext.bean.data.MainItem;
import cn.brt.beacon.map.ext.bean.data.MainSubItem;
import cn.brt.beacon.map.ext.common.utils.UIUtil;

/**
 * 主页功能选择扩展适配
 * Created by Walkud on 2018/3/12 0012.
 */

public class MainExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater layoutInflater;
    private List<MainItem> mainItems;

    public MainExpandableListAdapter(Context context, List<MainItem> mainItems) {
        this.mainItems = mainItems;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mainItems.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return getGroup(i).getSubItems().size();
    }

    @Override
    public MainItem getGroup(int i) {
        return mainItems.get(i);
    }

    @Override
    public MainSubItem getChild(int i, int i1) {
        return getGroup(i).getSubItems().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = (TextView) layoutInflater.inflate(R.layout.cell_main_item, null);
        } else {
            textView = (TextView) view;
        }

        setTextViewStyle(textView, 50);
        textView.setText(getGroup(i).getName());
        return textView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = (TextView) layoutInflater.inflate(R.layout.cell_main_sub_item, null);
        } else {
            textView = (TextView) view;
        }

        setTextViewStyle(textView, 40);
        textView.setText(getChild(i, i1).getName());

        return textView;
    }

    /**
     * 设置TextView 样式
     *
     * @param textView
     * @param heightDp
     */
    public void setTextViewStyle(TextView textView, int heightDp) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.dp2px(heightDp));
        textView.setLayoutParams(lp);
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 20, 0, 20);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
