package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j

public class CommonController {//同用controller,文件上传，菜品，套餐
@Autowired
private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){//上传阿里云网址，绝对路径，返回string
        log.info("文件上传{}",file);
        try {
        //原始文件名
      String originalFilename = file.getOriginalFilename();
      //截取原始文件名的后缀
     String extension= originalFilename.substring(originalFilename.lastIndexOf("."));
           String objectName= UUID.randomUUID().toString() + extension;
           //文件的请求路径
      String filePath= aliOssUtil.upload(file.getBytes(), objectName);//防止文件重名导致覆盖，对原始文件重命名UUID
      return Result.success(filePath);
        } catch (IOException e) {
        log.error("文件上传失败:{}",e);
//            e.printStackTrace();
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
