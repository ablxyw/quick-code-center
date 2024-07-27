package cn.ablxyw.util;

import cn.ablxyw.enums.GlobalEnum;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:aiyongfeng
 * @Time:2021/5/15
 * @Desc: 文件上传和下载
 */
@Component
public class MinioUtil {

    private static MinioClient minioClient;

    private static String minioBucketName;
    private static String minioAccessUrl;
    private static String minioAccessKey;
    private static String minioSecretKey;

    @Value("${qFrame.minio.defaultBucket}")
    private String bucketName;
    @Value("${qFrame.minio.url}")
    private String accessUrl;
    @Value("${qFrame.minio.accessKey}")
    private String accessKey;
    @Value("${qFrame.minio.secretKey}")
    private String secretKey;

    /**
     *
     * @param file
     * @param path
     * @return
     */
    public static Map<String,Object> upload(MultipartFile file, String path) {
        Map<String,Object> resultMap = new HashMap<>(3);
        String url = "";
        InputStream inputStream = null;
        try {
            createMinio();
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                inputStream.close();
            }
            if (file.isEmpty()) {
                throw new RuntimeException(GlobalEnum.FileEmpty.getMessage());
            }
            String fileName = file.getOriginalFilename();
            String name = path + System.currentTimeMillis() + "_" + fileName;
            PutObjectOptions options = new PutObjectOptions(inputStream.available(), -1);
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            options.setContentType("image/" + fileType);
            minioClient.putObject(minioBucketName, name, inputStream, options);
            inputStream.close();
            url = minioClient.getObjectUrl(minioBucketName, name);
            resultMap.put("fileName",fileName);
            resultMap.put("url",url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public static void createMinio() throws Exception {
        //1. 创建链接
        minioClient = new MinioClient(minioAccessUrl, minioAccessKey, minioSecretKey);
        // 检查bucketName是否存在
        boolean found = minioClient.bucketExists(minioBucketName);
        if (!found) {
            // 创建一个名为bucketName的存储桶
            minioClient.makeBucket(minioBucketName);
        }
    }

    public static void deleteUploadFile(String url) {
        try {
            createMinio();
            minioClient.removeObject(minioBucketName, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void getMinioField() {
        minioBucketName = this.bucketName;
        minioAccessUrl = this.accessUrl;
        minioAccessKey = this.accessKey;
        minioSecretKey = this.secretKey;
    }


    /**
     * 下载
     * @return
     */
    public static void downloadMinio(String fileUrl, HttpServletResponse response){
        InputStream in = null;
        OutputStream out = null;
        try {
            createMinio();
            // 拿到文件路径
            String url = fileUrl.split(minioBucketName + "/")[1];
            // 获取文件对象
            String substring = url.substring(url.indexOf("/") + 1);

            in = minioClient.getObject(minioBucketName, url);
            byte buf[] = new byte[1024];
            int length = 0;
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(substring, "UTF-8"));
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            out = response.getOutputStream();
            // 输出文件
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
