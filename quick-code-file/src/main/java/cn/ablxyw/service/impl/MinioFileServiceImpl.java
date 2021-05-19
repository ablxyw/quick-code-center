package cn.ablxyw.service.impl;

import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.MinioFileService;
import cn.ablxyw.util.MinioUtil;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * @Author: AiYongFeng
 * @Date: 2021/5/15 10:04
 */
@Service
public class MinioFileServiceImpl implements MinioFileService {

    @Override
    public ResultEntity fileUpload(List<MultipartFile> fileList, HttpServletRequest request) {
        List<Map<String, Object>> uploadFileList = Lists.newArrayList();
        for (MultipartFile file:fileList) {
            Map<String, Object> upload = MinioUtil.upload(file, "image/");
            uploadFileList.add(upload);
        }
        return ResultUtil.success(GlobalEnum.ImportSuccess,uploadFileList);
    }
}
