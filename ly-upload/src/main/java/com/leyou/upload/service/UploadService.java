package com.leyou.upload.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jiang-gege
 * 2019/10/2218:02
 */
@Service
public class UploadService {

    private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg","image/png");
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型
            String contentType = file.getContentType();
            if(!ALLOW_TYPES.contains(contentType)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //准备目标路径
            File dest = new File("F:/upload/",file.getOriginalFilename());
            //保存文件到本地
            file.transferTo(dest);

            //返回路径
            return "http://image.leyou.com/"+file.getOriginalFilename();
        } catch (IOException e) {
            //上传失败
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }

    }
}