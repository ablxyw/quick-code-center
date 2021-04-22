package cn.ablxyw.service.impl;

import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.SysFileService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * 文件上传下载ServiceImpl
 *
 * @author bfb
 * @date 2021-04-20 14:28:44
 */
@Slf4j
@Service("sysFileService")
public class SysFileServiceImpl implements SysFileService {
    /**
     * 保存文件位置
     */
    @Value("${qFrame.file.filePath}")
    private String savePath;

    /**
     * 文件链接前缀
     */
    @Value("${qFrame.file.urlPrefix}")
    private String urlPrefix;

    /**
     * 上传文件
     *
     * @param fileList 文件集合
     * @param request  请求
     * @return ResultEntity
     */
    @Override
    public ResultEntity fileUpload(List<MultipartFile> fileList, HttpServletRequest request) {
        if (Objects.isNull(fileList)) {
            return ResultUtil.error(GlobalEnum.FileEmpty);
        }
        List<Map<String, String>> uploadFileList = Lists.newArrayList();
        String fileSavePath = savePath.endsWith(File.separator) ? savePath : GlobalUtils.appendString(savePath, File.separator);
        fileSavePath = GlobalUtils.appendString(fileSavePath, LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMATTER)));
        FileUtil.mkdir(fileSavePath);
        String finalFileSavePath = fileSavePath;
        fileList.stream().filter(saveFile -> saveFile.getSize() > 0).forEach(saveFile -> {
            String originalFilename = saveFile.getOriginalFilename();
            try {
                Files.copy(saveFile.getInputStream(), Paths.get(finalFileSavePath, originalFilename), StandardCopyOption.REPLACE_EXISTING);
                uploadFileList.add(new HashMap<String, String>(3) {{
                    put("fileName", originalFilename);
                    put("url", GlobalUtils.appendString(urlPrefix, urlPrefix.endsWith(SLASH_CODE) ? EMPTY_STRING : SLASH_CODE, originalFilename));
                    put("path", finalFileSavePath);
                }});
            } catch (IOException e) {
                log.error("上传文件失败:{}", e.getMessage());
            }
        });
        return ResultUtil.success(GlobalEnum.ImportSuccess, uploadFileList);
    }

    /**
     * 下载附件
     *
     * @param oriUrl 原始Url
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity downloadFile(String oriUrl) {
        HttpHeaders headers = new HttpHeaders();
        FileUrlResource fileUrlResource = null;
        try {
            URL fileUrl = new URL(oriUrl);
            fileUrlResource = new FileUrlResource(fileUrl);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileUrlResource.getFilename(), StandardCharsets.UTF_8.name()));
            headers.add(HttpHeaders.CONTENT_TYPE, "application/x-zip-compressed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(fileUrlResource);
    }
}
