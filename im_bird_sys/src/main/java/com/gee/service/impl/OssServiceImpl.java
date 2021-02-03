package com.gee.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.gee.service.OssService;
import com.gee.utils.ConstPropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    //上传头像到OSS
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        //获取配置信息
        String endpoint = ConstPropertiesUtils.END_POINT;
        String accessKeyId = ConstPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstPropertiesUtils.BUCKET_NAME;
        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //获取上传文件输入流
            InputStream inputStream = file.getInputStream();
            //获取文件名称拓展名
            String ext = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            //文件名加入UUID,
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");

            String filename = "webChat/" + uuid + "." + ext;
            //调用oss方法实现上传，
            ossClient.putObject(bucketName, filename, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            //实现上传到OOS的路径拼接
            return "https://" + bucketName + "." + endpoint + "/" + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
