package cn.brt.beacon.map.ext.bean.data;

import java.util.List;

/**
 * 主页功能Item
 * Created by Walkud on 2018/3/12 0012.
 */

public class MainItem {
    private String name;
    private List<MainSubItem> subItems;

    public MainItem(String name, List<MainSubItem> subItems) {
        this.name = name;
        this.subItems = subItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MainSubItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<MainSubItem> subItems) {
        this.subItems = subItems;
    }
}
