package com.kk.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.kk.service.OssService;
import com.kk.util.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class OssServiceImpl implements OssService {


    @Override
    public String uploadFile(MultipartFile file) {
        String url = null;
        String endpoint = OssUtil.END_POINT;
        String accessKeyId = OssUtil.ACCESS_KEY_ID;
        String accessKeySecret = OssUtil.ACCESS_KEY_SECRET;
        String bucketName = OssUtil.BUCKET_NAME;
        //build Oss connection
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            //need 2 pcs of stream，one for MD5，the other for upload to Oss。https://blog.csdn.net/xueyijin/article/details/121526772
            InputStream inputStream = file.getInputStream();
            //
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            String md5 = DigestUtils.md5DigestAsHex(baos.toByteArray());
            log.info("md5:{}", md5);
            InputStream inputStreamForMD5 = new ByteArrayInputStream(baos.toByteArray());
            InputStream inputStreamForOss = new ByteArrayInputStream(baos.toByteArray());
            /*其实用下面这个也是一样的，但是哪个更高效和通用？？？
            InputStream inputStreamForMD5 = file.getInputStream();
            InputStream inputStreamForOss = file.getInputStream();
             */
            //get original name
            String filename = file.getOriginalFilename();
            //get suffix
            String fileSuffix = filename.substring(file.getOriginalFilename().lastIndexOf("."));
            //获取一个md加密
            String md5OfFileName = DigestUtils.md5DigestAsHex(inputStreamForMD5);
            //这里是为了按上传时间分配目录。精确到月,这里用到一个第三方的jar包，记得笔记2022/5/18
            String dateMark = DateTime.now().toString("yyyyMMdd/");
            //拼接成完整的文件名。
            final String uploadKey = dateMark + md5OfFileName + fileSuffix;
            log.info("uploadKey:{}", uploadKey);
            //upload them
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, uploadKey, inputStreamForOss);
            //阿里云返回eTag为全大写的md5值
            String eTag = putObjectResult.getETag();
            log.info("eTag:{}", eTag);
            //拼接url，这个就是地址
            url = "https://" + bucketName + "." + endpoint + "/" + uploadKey;
            log.info("Image URL：{}", url);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return url;
    }
}

