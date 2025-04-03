package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.FileManagementMapper;
import cn.zhangchuangla.system.model.dto.SaveFileInfoDto;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.model.request.file.FileManagementListRequest;
import cn.zhangchuangla.system.service.FileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 文件管理服务实现类
 *
 * @author zhangchuang
 */
@Service
@Slf4j
public class FileManagementServiceImpl extends ServiceImpl<FileManagementMapper, FileManagement>
        implements FileManagementService {

    private final FileManagementMapper fileManagementMapper;

    @Autowired
    public FileManagementServiceImpl(FileManagementMapper fileManagementMapper) {
        this.fileManagementMapper = fileManagementMapper;
    }


    /**
     * 文件列表
     *
     * @param request 文件列表请求参数
     * @return 文件列表分页结果
     */
    @Override
    public Page<FileManagement> fileList(FileManagementListRequest request) {
        Page<FileManagement> page = page(new Page<>(request.getPageNum(), request.getPageSize()));
        return fileManagementMapper.fileList(page, request);
    }

    /**
     * 删除文件
     *
     * @param ids 文件ID集合
     */
    @Override
    public void deleteFile(List<Long> ids) {
        ids.forEach(id -> {
            FileManagement file = getById(id);
            if (file == null) {
                throw new ServiceException(ResponseCode.RESULT_IS_NULL, "ID:" + id + "的文件不存在！");
            }
        });
    }


    /**
     * 保存文件记录
     * <p>
     * 本方法负责将文件信息保存到系统中，包括获取用户信息、处理文件路径、获取存储桶信息、创建并保存文件记录
     * 在保存过程中，任何异常都会被捕获并记录，以确保上传流程不受影响
     *
     * @param saveFileInfoDto 包含文件保存所需信息的数据传输对象
     */
    @Override
    public void saveFileRecord(SaveFileInfoDto saveFileInfoDto) {
        try {
            // 1. 获取用户信息
            Long userId = SecurityUtils.getUserId();
            String userName = SecurityUtils.getUsername();


            // 2. 获取存储桶信息
            String bucketName = getBucketName(saveFileInfoDto.getStorageType());

            // 3. 创建并填充文件记录
            FileManagement record = createFileRecord(saveFileInfoDto, userId, userName, bucketName);

            // 4. 保存记录
            save(record);
        } catch (Exception e) {
            log.error("保存文件记录失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响上传流程
        }
    }

    /**
     * 根据文件ID获取文件管理对象
     * <p>
     * 此方法用于通过文件ID查询并返回对应的文件管理对象如果未找到对应的文件，
     * 则抛出服务异常此方法主要用于文件管理功能中，当需要根据文件ID获取详细文件信息时调用
     *
     * @param id 文件ID，用于查询文件管理对象
     * @return FileManagement 返回查询到的文件管理对象
     * @throws ServiceException 当文件ID不存在时，抛出服务异常
     */
    @Override
    public FileManagement getFileById(Long id) {
        // 根据文件ID查询文件管理对象
        FileManagement management = getById(id);
        // 检查查询结果是否为空，如果为空则抛出异常
        if (management == null) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "文件ID:" + id + "的文件不存在！");
        }
        // 返回查询到的文件管理对象
        return management;
    }


    /**
     * 根据存储类型获取对应的Bucket名称
     *
     * @param storageType 存储类型，用于区分不同的对象存储服务
     * @return 对应存储类型的Bucket名称，如果存储类型不匹配则返回null
     */
    private String getBucketName(String storageType) {

        return null;
    }

    /**
     * 创建文件记录
     * 根据提供的文件信息和用户信息，构建并返回一个FileManagement对象
     * 该方法用于在文件上传或处理时，生成对应的文件管理记录
     *
     * @param saveFileInfoDto 包含文件信息的数据传输对象
     * @param userId          文件上传者的用户ID
     * @param userName        文件上传者的用户名
     * @param bucketName      文件存储的桶名称
     * @return 返回构建好的FileManagement对象
     */
    private FileManagement createFileRecord(SaveFileInfoDto saveFileInfoDto, Long userId, String userName, String bucketName) {
        FileManagement record = new FileManagement();
        // 设置文件基本信息
        record.setFileName(saveFileInfoDto.getFileInfo().getOriginalFilename());
        record.setOriginalRelativeFileLocation(saveFileInfoDto.getOriginalRelativeFileLocation());
        record.setOriginalFileName(saveFileInfoDto.getFileInfo().getOriginalFilename());
        record.setPreviewRelativeFileLocation(saveFileInfoDto.getPreviewRelativeFileLocation());
        record.setFileUrl(saveFileInfoDto.getFileUrl());
        record.setFileSize(saveFileInfoDto.getFileInfo().getSize());
        record.setFileType(saveFileInfoDto.getFileInfo().getContentType());
        record.setFileExtension(saveFileInfoDto.getFileInfo().getFileExtension());

        // 设置存储信息
        record.setStorageType(saveFileInfoDto.getStorageType());
        record.setBucketName(bucketName);

        // 设置上传者信息
        record.setUploaderId(userId);
        record.setUploaderName(userName);

        // 设置其他元数据
        record.setMd5(saveFileInfoDto.getFileInfo().getMd5());
        record.setCreateBy(Constants.SYSTEM_CREATE);
        record.setUploadTime(new Date());
        record.setPreviewImage(saveFileInfoDto.getCompressedUrl());

        return record;
    }


}




