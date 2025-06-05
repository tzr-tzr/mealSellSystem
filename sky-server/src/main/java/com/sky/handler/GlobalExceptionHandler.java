package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    /**
     * 处理SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message=ex.getMessage();//获得异常信息
        if(message.contains("Duplicate entry")){//是否包含异常里的字
            String []split= message.split(" ");//根据空格分隔获得数组对象
            String username=split[2];//动态获得用户名
            //拼接”用户名已存在提示信息“
            String msg=username+ MessageConstant.ALREADY_EXISTS;//已经存在
            return Result.error(msg);

        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);//未知错误
        }

    }

}
