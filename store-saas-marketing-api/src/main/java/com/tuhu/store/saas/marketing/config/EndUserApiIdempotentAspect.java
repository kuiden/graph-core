package com.tuhu.store.saas.marketing.config;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.annotation.EndUserApiIdempotent;
import com.tuhu.store.saas.marketing.constant.EndUserConstant;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.exception.NoneBizException;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;


/**
 * @author wangxiang2
 */
@Aspect
@Component
@Slf4j
public class EndUserApiIdempotentAspect implements Ordered {

    @Pointcut("@annotation(endUserApiIdempotent)")
    public void pointCut(EndUserApiIdempotent endUserApiIdempotent) {
    }

    @Autowired
    private StoreRedisUtils storeRedisUtils;

    @Around("pointCut(endUserApiIdempotent)")
    public Object around(ProceedingJoinPoint pjp, EndUserApiIdempotent endUserApiIdempotent) throws Throwable {
        int lockSeconds = endUserApiIdempotent.lockTime();

        EndUser endUser = EndUserContextHolder.getUser();
        if (endUser == null) {
            throw new BizException(BizErrorCodeEnum.INVALID_ACCESS_TOKEN, "您的身份已过期，请重新登录");
        }
        // 此处可以用token或者JSessionId
        String userId = endUser.getUserId();
        String key = EndUserConstant.REDIS_PREFIX_KEYS + "RepeatSubmitAspect_tryLock_" + userId;
        Object obj = storeRedisUtils.getAtomLock(key, lockSeconds);
        log.info("tryLock key = [{}]", key);

        if (obj != null) {
            log.info("tryLock success, key = [{}]", key);
            // 获取锁成功
            Object result;
            try {
                // 执行进程
                result = pjp.proceed();
            } catch (BizException e){
                BizBaseResponse response = new BizBaseResponse();
                response.setCode(e.getErrorCode().getCode());
                response.setMessage(e.getErrorMessage());
                result = response;
            } catch (Exception e) {
                //如果发生异常后 放上释放锁
                storeRedisUtils.releaseLock(key, obj.toString());
                log.error("RepeatSubmitAspect error key: {}", key, e);
                throw new Exception(e.getMessage());
            }
            return result;
        } else {
            // 获取锁失败，认为是重复提交的请求
            log.info("tryLock fail, key = [{}]", key);
            return new BizBaseResponse<>(BizErrorCodeEnum.TOO_MANY_REQUEST);
        }

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
