package com.kk.api;

import com.kk.common.R;
import com.kk.service.OssService;

import com.kk.util.InputStreamUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


/**
 * @Description: file upload& download
 * @Author:Spike Wong
 * @Date:2022/7/2
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonAPI {


    private final String saveFilePath = System.getProperty("user.dir") + File.separator + "files";

    //alibaba's Oss service
    @Autowired
    OssService ossService;

    /**
     * file upload by aliyun oss client
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 1024 * 1) {
            return R.error("Size of file can exceed 1 MB");
        }
        String originalFilename = file.getOriginalFilename();
        //get suffix behind ".".
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
            return R.error("please choose correct format!");
        }
        String url = ossService.uploadFile(file);
        return R.success(url);
    }

    //another way
    //        // it  is a temporary file, Finder of mac : command+shift+G，enter“/private”
    //        log.info("file：{}", file.toString());
    //        String originalFilename = file.getOriginalFilename();
    //        //less than 1MB
    //        if (file.getSize() > 1024 * 1024 * 1) {
    //            return R.error("size of image should be less than 1MB");
    //        }
    //get suffix behind ".".
//            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
//            if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
//                return R.error("please choose correct format");
    //        }      //get current date
    //        String currentDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
    //        if (!Files.exists(Paths.get(saveFilePath))) {
    //
    //            Files.createDirectory(Paths.get(saveFilePath));
    //        }
    //        String attachmentSource = saveFilePath + File.separator + currentDate + "_" + originalFilename;
    //        String fileName = currentDate + "_" + originalFilename;
    //        final Path saveAttachment = Paths.get(saveFilePath + File.separator + currentDate + "_" + originalFilename);
    //        file.transferTo(saveAttachment);
    //        return R.success(fileName);


    /**
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws Exception {
        log.info("{}", name);
        try {
            //make an image inputStream
            InputStream imageStream = InputStreamUtils.getImageStream(name);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = imageStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            imageStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
