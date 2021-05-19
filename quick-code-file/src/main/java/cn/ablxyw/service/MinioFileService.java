package cn.ablxyw.service;

import cn.ablxyw.vo.ResultEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: AiYongFeng
 * @Date: 2021/5/15 10:04
 */
public interface MinioFileService {

    /**
     * 上传文件
     *
     * @param fileList 文件集合
     * @param request  请求
     * @return ResultEntity
     */
    ResultEntity fileUpload(List<MultipartFile> fileList, HttpServletRequest request);
}
