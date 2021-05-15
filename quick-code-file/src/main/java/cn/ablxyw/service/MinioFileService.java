package cn.ablxyw.service;

import cn.ablxyw.vo.ResultEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;

public interface MinioFileService {

    /**
     * 上传文件
     *
     * @param file 文件集合
     * @param request  请求
     * @return ResultEntity
     */
    ResultEntity fileUpload(MultipartFile file, HttpServletRequest request);
}
