package cn.ablxyw.service;

import cn.ablxyw.vo.ResultEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件上传下载Service
 *
 * @author bfb
 * @date 2021-04-20 14:28:44
 */
public interface SysFileService {
    /**
     * 上传文件
     *
     * @param fileList 文件集合
     * @param request  请求
     * @return ResultEntity
     */
    ResultEntity fileUpload(List<MultipartFile> fileList, HttpServletRequest request);

    /**
     * 下载附件
     *
     * @param oriUrl 原始Url
     * @return ResponseEntity
     */
    ResponseEntity downloadFile(String oriUrl);
}
