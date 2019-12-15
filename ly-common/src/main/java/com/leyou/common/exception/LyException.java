package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Jiang-gege
 * 2019/9/2322:54
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LyException extends RuntimeException {

    private ExceptionEnum exceptionEnum;

}