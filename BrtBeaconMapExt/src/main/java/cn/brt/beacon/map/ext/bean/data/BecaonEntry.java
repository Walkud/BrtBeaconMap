package cn.brt.beacon.map.ext.bean.data;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Beacon实体类
 * Created by zhiqiang on 2016-10-10.
 */
public class BecaonEntry implements Serializable, Comparable<BecaonEntry> {

    private String geom;
    private String uuid;
    private short major;
    private short minor;
    private int floor;
    private String shop_id;
    private String tag;
    private double x;
    private double y;

    public BecaonEntry() {
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(short major) {
        this.major = major;
    }

    public short getMinor() {
        return minor;
    }

    public void setMinor(short minor) {
        this.minor = minor;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "BecaonEntry{" +
                "geom='" + geom + '\'' +
                ", uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", floor=" + floor +
                ", shop_id='" + shop_id + '\'' +
                ", tag='" + tag + '\'' +
                ", x=" + x +
                ", y='" + y + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BecaonEntry that = (BecaonEntry) o;
        if (major != that.major) {
            return false;
        }
        if (minor != that.minor) {
            return false;
        }
        return uuid.equals(that.uuid);

    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + major;
        result = 31 * result + minor;
        return result;
    }

    @Override
    public int compareTo(BecaonEntry another) {
        int result = 0;
        if (minor > another.getMinor()) {
            result = -1;
        } else if (minor < another.getMinor()) {
            result = 1;
        }
        return result;
    }

    /**
     * 升序
     */
    public static class MinorDescendingSort implements Comparator<BecaonEntry> {

        @Override
        public int compare(BecaonEntry lhs, BecaonEntry rhs) {
            int result = 0;
            if (lhs.getMinor() > rhs.getMinor()) {
                result = -1;
            } else if (lhs.getMinor() < rhs.getMinor()) {
                result = 1;
            }
            return result;
        }
    }

    /**
     * 降序
     */
    public static class MinorAscendingSort implements Comparator<BecaonEntry> {

        @Override
        public int compare(BecaonEntry lhs, BecaonEntry rhs) {
            int result = 0;
            if (lhs.getMinor() > rhs.getMinor()) {
                result = 1;
            } else if (lhs.getMinor() < rhs.getMinor()) {
                result = -1;
            }
            return result;
        }
    }

    /**
     * 升序
     */
    public static class MajorDescendingSort implements Comparator<BecaonEntry> {

        @Override
        public int compare(BecaonEntry lhs, BecaonEntry rhs) {
            int result = 0;
            if (lhs.getMajor() > rhs.getMajor()) {
                result = -1;
            } else if (lhs.getMajor() < rhs.getMajor()) {
                result = 1;
            }
            return result;
        }
    }

    /**
     * 降序
     */
    public static class MajorAscendingSort implements Comparator<BecaonEntry> {

        @Override
        public int compare(BecaonEntry lhs, BecaonEntry rhs) {
            int result = 0;
            if (lhs.getMajor() > rhs.getMajor()) {
                result = 1;
            } else if (lhs.getMajor() < rhs.getMajor()) {
                result = -1;
            }
            return result;
        }
    }

    /**
     * 升序
     */
    public static class DeployDescendingSort implements Comparator<BecaonEntry> {

        @Override
        public int compare(BecaonEntry lhs, BecaonEntry rhs) {
            int lDeploy = TextUtils.isEmpty(lhs.getGeom()) ? 0 : 1;
            int rDeploy = TextUtils.isEmpty(rhs.getGeom()) ? 0 : 1;
            int result = 0;
            if (lDeploy - rDeploy > 0) {
                result = -1;
            } else if (lDeploy - rDeploy < 0) {
                result = 1;
            }
            return result;
        }
    }

    /**
     * 降序
     */
    public static class DeployAscendingSort implements Comparator<BecaonEntry> {

        @Override
        public int compare(BecaonEntry lhs, BecaonEntry rhs) {
            int lDeploy = TextUtils.isEmpty(lhs.getGeom()) ? 0 : 1;
            int rDeploy = TextUtils.isEmpty(rhs.getGeom()) ? 0 : 1;
            int result = 0;
            if (lDeploy - rDeploy > 0) {
                result = 1;
            } else if (lDeploy - rDeploy < 0) {
                result = -1;
            }
            return result;
        }
    }
}
