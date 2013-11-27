package org.jostein.testassistant.workers;

import android.os.Environment;
import android.os.StatFs;


import java.io.File;

import org.jostein.testassistant.HomeActivity;

public class FillSystemStorage extends BaseTaskWorker {

    public FillSystemStorage(HomeActivity context, int count) {
        super(context, count);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Integer doInBackground(Void... params) {
        // TODO Auto-generated method stub
        return null;
    }

    /**

     * ����ʣ��ռ�

     * @param path

     * @return

     */ 

    private static long getAvailableSize(String path) 

    { 

        StatFs fileStats = new StatFs(path); 

        fileStats.restat(path); 

        return (long) fileStats.getAvailableBlocks() * fileStats.getBlockSize(); // ע����fileStats.getFreeBlocks()����� 

    } 

    /**

     * �����ܿռ�

     * @param path

     * @return

     */ 

    private static long getTotalSize(String path) 

    { 

        StatFs fileStats = new StatFs(path); 

        fileStats.restat(path); 

        return (long) fileStats.getBlockCount() * fileStats.getBlockSize(); 

    } 

    /**
     * ����SD����ʣ��ռ�
     * @return ʣ��ռ�
     */ 
    public static long getSDAvailableSize() { 
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
            return getAvailableSize(Environment.getExternalStorageDirectory().toString()); 
        } 
        return 0; 
    } 

    /**

     * ����ϵͳ��ʣ��ռ�

     * @return ʣ��ռ�

     */ 

    public static long getSystemAvailableSize() 

    { 

        // context.getFilesDir().getAbsolutePath(); 

        return getAvailableSize("/data"); 

    } 

    /**

     * �Ƿ����㹻�Ŀռ�

     * @param filePath �ļ�·��������Ŀ¼��·��

     * @return

     */ 

    public static boolean hasEnoughMemory(String filePath) 

    { 

        File file = new File(filePath); 

        long length = file.length(); 

        if (filePath.startsWith("/sdcard") || filePath.startsWith("/mnt/sdcard")) 

        { 

            return getSDAvailableSize() > length; 

        } 

        else 

        { 

            return getSystemAvailableSize() > length; 

        } 

    } 

    /**

     * ��ȡSD�����ܿռ�

     * @return

     */ 

    public static long getSDTotalSize() 

    { 

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 

        { 

            return getTotalSize(Environment.getExternalStorageDirectory().toString()); 

        } 

        return 0; 

    } 

    /**

     * ��ȡϵͳ�ɶ�д���ܿռ�

     * @return

     */ 

    public static long getSysTotalSize() 

    { 

        return getTotalSize("/data"); 

    } 

} 
