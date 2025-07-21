package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.vo.ImageVo;
import cn.zhangchuangla.storage.model.vo.SimpleFileVO;
import cn.zhangchuangla.storage.service.StorageFileService;
import cn.zhangchuangla.storage.utils.StorageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件相关接口
 * 提供文件上传等功能
 *
 * @author Chuang
 * Created on 2025/4/3 21:23
 */
@RequestMapping("/common/file")
@RestController
@Tag(name = "文件相关接口", description = "提供多种文件和资源上传相关接口")
@Slf4j
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final StorageFileService storageFileService;


    /**
     * 普通文件上传
     *
     * @param file 文件
     * @return 文件信息
     */
    @Operation(summary = "普通文件上传")
    @PostMapping
    public AjaxResult<SimpleFileVO> upload(@RequestParam("file") MultipartFile file) {
        UploadedFileInfo upload = storageFileService.upload(file);
        SimpleFileVO simpleFileVO = SimpleFileVO.builder()
                .fileName(upload.getFileOriginalName())
                .fileSize(StorageUtils.formatFileSize(upload.getFileSize()))
                .fileType(upload.getFileType())
                .fileUrl(upload.getFileUrl())
                .build();
        return success(simpleFileVO);
    }

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 上传结果
     */

    @PostMapping("/image")
    @Operation(summary = "上传图片")
    public AjaxResult<ImageVo> uploadImage(@RequestParam("file") MultipartFile file) {
        UploadedFileInfo upload = storageFileService.uploadImage(file);
        ImageVo imageVo = new ImageVo(upload.getFileUrl(), upload.getPreviewImage());
        return success(imageVo);
    }

}
