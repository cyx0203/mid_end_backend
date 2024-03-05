package com.gg.midend.utils;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Service
public class CopyFile {

    // 复制改文件名
    public static void copy(String srcPathStr, String desPathStr, String newFileName) {
        // 获取源文件的名称
        System.out.println("源文件:" + newFileName);
        File file = new File(desPathStr);
        if (!file.exists()) {
            file.mkdirs();
        }
        desPathStr = desPathStr + File.separator + newFileName; // 源文件地址
        System.out.println("目标文件地址:" + desPathStr);
        try {
            FileInputStream fis = new FileInputStream(srcPathStr);// 创建输入流对象
            FileOutputStream fos = new FileOutputStream(desPathStr); // 创建输出流对象
            byte datas[] = new byte[1024 * 8];// 创建搬运工具
            int len = 0;// 创建长度
            while ((len = fis.read(datas)) != -1)// 循环读取数据
            {
                fos.write(datas, 0, len);
            }
            fis.close();// 释放资源
            fos.close();// 释放资源
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 移动
    public static void move(String srcPathStr, String desPathStr) {
        try {
            File file = new File(srcPathStr); // 源文件
            if (file.renameTo(new File(desPathStr + "\\" + file.getName()))) // 源文件移动至目标文件目录
            {
                System.out.println("File is moved successful!");// 输出移动成功
            } else {
                System.out.println("File is failed to move !");// 输出移动失败
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
