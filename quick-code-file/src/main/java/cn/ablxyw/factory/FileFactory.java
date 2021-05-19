package cn.ablxyw.factory;

import cn.ablxyw.service.MinioFileService;
import cn.ablxyw.service.SysFileService;
import cn.ablxyw.vo.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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




}
