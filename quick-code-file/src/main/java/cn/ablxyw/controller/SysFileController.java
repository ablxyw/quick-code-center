package cn.ablxyw.controller;

import cn.ablxyw.factory.FileFactory;
import cn.ablxyw.vo.ResultEntity;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件上传下载Controller
 *
 * @author bfb
 * @email bao_fubin@163.com
 * @date 2021-04-20 14:28:44
 */
@Api(value = "文件Api", tags = "文件上传下载")
@RestController
@RequestMapping("/sysFile")
public class SysFileController {

    @Autowired
    private FileFactory fileFactory;

    /**
     * 批量上传文件
     *
     * @param fileList 文件集合
     * @param request  请求
     * @return ResultEntity
     */
    @ApiOperation("批量上传文件")
    @PostMapping(value = "filesUpload")
    public ResultEntity filesUpload(@RequestParam(name = "fileList") List<MultipartFile> fileList, HttpServletRequest request) {
        return fileFactory.fileUpload(fileList, request);
    }

    /**
     * 单个上传文件
     *
     * @param file    文件
     * @param request 请求
     * @return ResultEntity
     */
    @ApiOperation("单个上传文件")
    @PostMapping(value = "fileUpload")
    public ResultEntity fileUpload(@RequestParam(name = "file") MultipartFile file, HttpServletRequest request) {
        return fileFactory.fileUpload(Lists.newArrayList(file), request);
    }

    /**
     * 下载文件
     *
     * @param oriUrl 原始Url
     * @return ResponseEntity
     */
    @ApiOperation("下载文件")
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public ResponseEntity downloadFile(@RequestParam(name = "oriUrl") String oriUrl, HttpServletResponse response) {
        return fileFactory.downloadFile(oriUrl,response);
    }
}
