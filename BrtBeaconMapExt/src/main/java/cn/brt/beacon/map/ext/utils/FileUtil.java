package cn.brt.beacon.map.ext.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件操作工具类
 */
public class FileUtil {

    /**
     * 解压zip
     *
     * @param filePath
     * @return 是否解压成功
     */
    public static boolean unZipFile(String filePath) {
        boolean result = false;
        if (TextUtils.isEmpty(filePath)) {
            return result;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return result;
        }

        ZipFile zip = null;
        try {
            zip = new ZipFile(file);
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName().replace("\\", "/");
                String outPath = (file.getParent() + File.separator + zipEntryName);
                // 输出文件路径信息
                File outFile = new File(outPath);
                if (entry.isDirectory()) {
                    if (!outFile.exists()) {
                        outFile.mkdirs();
                    }
                } else {
                    File parentPath = new File(outFile.getParent());
                    if (!parentPath.exists()) {
                        parentPath.mkdirs();
                    }
                    InputStream in = zip.getInputStream(entry);
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf1 = new byte[1024];
                    int len;
                    while ((len = in.read(buf1)) > 0) {
                        out.write(buf1, 0, len);
                    }
                    out.flush();
                    out.close();
                    in.close();
                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static boolean makeDir(String path) {
        boolean result = false;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            result = true;
        }
        return result;
    }

    /**
     * 删除文件目录
     *
     * @param dir
     * @param exceptFileName
     */
    public static void clearCacheDir(String dir, String exceptFileName) {
        if (TextUtils.isEmpty(dir)) {
            return;
        }

        File file = new File(dir);
        if (!file.exists()) {
            return;
        }

        clearCacheDir(file, exceptFileName);
    }

    /**
     * 递归删除目录文件
     *
     * @param file
     * @param exceptFileName
     */
    private static void clearCacheDir(File file, String exceptFileName) {
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            for (File chile : childFile) {
                clearCacheDir(chile, exceptFileName);
            }
            file.delete();
        } else {
            if (!TextUtils.isEmpty(exceptFileName) && file.getName().lastIndexOf(exceptFileName) != -1) {
                //TODO NOTHING
            } else {
                file.delete();
            }
        }
    }

    /**
     * 读取文件字符串
     *
     * @param filePath
     * @return
     */
    public static String readFileToString(String filePath) {
        StringBuilder result = new StringBuilder();
        if (TextUtils.isEmpty(filePath)) {
            return result.toString();
        }
        if (!fileExists(filePath)) {
            return result.toString();
        }

        BufferedReader sr = null;
        try {
            sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            String line = "";
            while (!TextUtils.isEmpty(line = sr.readLine())) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sr != null) {
                try {
                    sr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }
}
