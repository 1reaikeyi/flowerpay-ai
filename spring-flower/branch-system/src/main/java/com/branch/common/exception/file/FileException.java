package com.branch.common.exception.file;


import com.branch.common.exception.BaseException;

/**
 * 文件信息异常类
 * 
 * @author ruoyi
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("common.exception.BaseException", code, args, null);
    }
    public FileException(int defaultFileNameLength) {
        super("common.exception.BaseException", new Object[] { defaultFileNameLength });
    }
    public FileException(long defaultMaxSize) {
        super("common.exception.BaseException", new Object[] { defaultMaxSize });
    }
}
