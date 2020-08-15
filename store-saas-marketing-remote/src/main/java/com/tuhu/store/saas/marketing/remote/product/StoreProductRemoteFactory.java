package com.tuhu.store.saas.marketing.remote.product;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.dto.product.GoodsData;
import com.tuhu.store.saas.dto.product.QueryGoodsListDTO;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.vo.product.GoodsListVO;
import com.tuhu.store.saas.vo.product.IssuedVO;
import com.tuhu.store.saas.vo.product.QueryGoodsListVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @time 2020-08-04
 * @auther kudeng
 */
@Component
@Slf4j
public class StoreProductRemoteFactory implements FallbackFactory<StoreProductClient>{


    @Override
    public StoreProductClient create(Throwable throwable) {
        return new StoreProductClient() {
            @Override
            public BizBaseResponse issuedGoodOrServiceSpu(IssuedVO issuedVO) {
                log.error("issuedGoodOrServiceSpu error,request={},error={}", issuedVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse queryServiceGoodListBySpuCodes(@RequestBody List<String> codeList, @RequestParam("storeId") Long storeId, @RequestParam("tenantId") Long tenantId) {
                log.error("queryServiceGoodListBySpuCodes error,codeList={}, storeId={},error={}", codeList, storeId, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<GoodsData>> queryServiceGoodListByCodesAndStoreId(@RequestBody List<String> codeList, @RequestParam("storeId") Long storeId) {
                log.error("queryServiceGoodListByCodesAndStoreId error,codeList={},storeId={},error={}", codeList,storeId, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<QueryGoodsListDTO>> queryGoodsListV2(QueryGoodsListVO queryGoodsListVO) {
                log.error("queryGoodsListV2 error,request={}", queryGoodsListVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse queryBatchGoods(List<String> codeList, Long storeId, Long tenantId, String carPosition) {
                log.error("queryBatchGoods error,codeList={},storeId={},tenantId={},carPosition={},error={}", codeList,storeId,tenantId,carPosition, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }
        };
    }
}
