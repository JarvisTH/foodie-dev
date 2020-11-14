package com.imooc.exception;

import com.imooc.utils.IMOOCJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author Jarvis(Tang Hui)
 * @version 1.0
 * @date 2020/4/13 13:04
 */

@RestControllerAdvice
public class CustomExceptionHandler {

    //上传文件超过 500 k 捕获异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public IMOOCJSONResult handlerMaxUploadFile(MaxUploadSizeExceededException ex){

        return IMOOCJSONResult.errorMsg("文件上传大小不能超过500k,请压缩图片");
    }

}
