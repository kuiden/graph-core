package com.tuhu.store.saas.marketing.config;


import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.exception.OpenIdException;
import com.tuhu.store.saas.marketing.remote.ResultObject;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.order.base.FieldError;
import com.xiangyun.versionhelper.VersionPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ApiExceptionHandlerAdvice {

    /**
     * 验证数据拦截
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public BizBaseResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = extractError(exception.getBindingResult());
        BizBaseResponse errorResult = new BizBaseResponse(fieldErrors);
        errorResult.setCode(BizErrorCodeEnum.PARAM_ERROR.getCode());
        List<String> messages = fieldErrors.stream().map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());
        errorResult.setMessage(StringUtils.join(messages,","));
        return errorResult;
    }

    /**
     * 验证数据拦截(Get)
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResultObject missingServletRequestParameterException(MissingServletRequestParameterException exception) {
        String parameterName= exception.getParameterName();
        return new ResultObject(4000,parameterName+"不能为空");
    }

    /**
     * Handle exceptions thrown by handlers.
     */
    /**
     * Handle exceptions thrown by handlers.
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultObject exception(Exception exception) {
        ResultObject errorResult = new ResultObject();
        if (exception.getCause() instanceof VersionPersistenceException) {
            errorResult.setMessage(exception.getCause().getMessage());
            errorResult.setCode(4000);
            return errorResult;
        }
        log.error("异常信息", exception);
        errorResult.setCode(5000);
        errorResult.setMessage(exception.getMessage());
        return errorResult;
    }

    @ExceptionHandler(value = StoreSaasMarketingException.class)
    @ResponseBody
    public ResultObject itemException(StoreSaasMarketingException exception) {
        ResultObject errorResult = new ResultObject();
        errorResult.setMessage(exception.getMessage());
        errorResult.setCode(4000);
        return errorResult;
    }

    @ExceptionHandler(value = OpenIdException.class)
    @ResponseBody
    public ResultObject openIdException(OpenIdException exception) {
        ResultObject errorResult = new ResultObject();
        errorResult.setMessage(exception.getMessage());
        errorResult.setCode(4000);
        return errorResult;
    }

    /**
     * 从绑定结果中提出错误字段
     */
    private List<FieldError> extractError(BindingResult bindingResult) {
        List<FieldError> fieldErrors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            FieldError error = new FieldError();
            error.setField(fieldError.getField());
            error.setRejectedValue(fieldError.getRejectedValue());
            error.setDefaultMessage(fieldError.getDefaultMessage());
            fieldErrors.add(error);
        });
        return fieldErrors;
    }

}
