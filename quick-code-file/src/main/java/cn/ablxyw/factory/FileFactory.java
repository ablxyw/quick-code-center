package cn.ablxyw.factory;

import cn.ablxyw.service.MinioFileService;
import cn.ablxyw.service.SysFileService;
import cn.ablxyw.vo.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: AiYongFeng
 * @Date: 2021/5/19 10:04
 */
@Component("FileFactory")
public class FileFactory {
    /**
     * local模式附件信息Service
     */
    @Autowired
    private SysFileService sysFileService;

    /**
     * minio模式附件信息Service
     */
    @Autowired
    private MinioFileService minioFileService;
    /**
     * 存储类型:local、minio、s3、oss、七牛、hdfs
     */
    @Value("${qFrame.mode}")
    private String mode;

    /**
     * 文件上传服务
     * @param fileList  文件集合（一个或多个文件）
     * @param request   请求
     * @return
     */
    public ResultEntity fileUpload(List<MultipartFile> fileList, HttpServletRequest request){
        ResultEntity resultEntity;
        switch(mode){
            case "local":
                resultEntity = sysFileService.fileUpload(fileList, request);
                break;
            case "minio":
                resultEntity = minioFileService.fileUpload(fileList,request);
                break;
            default:
                resultEntity = sysFileService.fileUpload(fileList, request);
                break;
        }
        return resultEntity;
    }


    /**
     * 文件下载服务
     * @param oriUrl
     * @param response
     * @return
     */
     public ResponseEntity downloadFile(String oriUrl,HttpServletResponse response){
         ResponseEntity responseEntity;
         switch(mode){
             case "local":
                 responseEntity = sysFileService.downloadFile(oriUrl);
                 break;
             case "minio":
                 responseEntity = minioFileService.downloadFile(oriUrl,response);
                 break;
             default:
                 responseEntity = sysFileService.downloadFile(oriUrl);
                 break;
         }
         return responseEntity;
     }




}
