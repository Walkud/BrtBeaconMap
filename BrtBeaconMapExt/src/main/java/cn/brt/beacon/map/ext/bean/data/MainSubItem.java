package cn.brt.beacon.map.ext.bean.data;

/**
 * 子功能项
 * Created by Walkud on 2018/3/12 0012.
 */

public class MainSubItem {
    private String name;
    private Class cls;

    public MainSubItem(String name, Class cls) {
        this.name = name;
        this.cls = cls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }
}
