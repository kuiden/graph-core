package com.tuhu.store.saas.marketing.config;


import com.tuhu.store.saas.marketing.exception.OpenIdException;
import com.tuhu.store.saas.marketing.remote.ResultObject;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.xiangyun.versionhelper.VersionPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ApiExceptionHandlerAdvice {

    /**
     * 验证数据拦截
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultObject methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BeanPropertyBindingResult beanPropertyBindingResult = (BeanPropertyBindingResult) exception.getBindingResult();
        ResultObject errorResult = new ResultObject( beanPropertyBindingResult.getAllErrors());
        errorResult.setCode(6000);
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

}
