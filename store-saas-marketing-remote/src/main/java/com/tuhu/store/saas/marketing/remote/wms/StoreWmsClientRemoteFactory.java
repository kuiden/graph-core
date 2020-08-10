package com.tuhu.store.saas.marketing.remote.wms;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.scmc.arch.model.facade.rsp.BizRsp;
import org.scmc.store.bd.dto.LocationDto;
import org.scmc.store.bd.request.LocationRequest;
import org.scmc.store.inbound.request.AsnRequest;
import org.scmc.store.inbound.response.ReceiveResponse;
import org.scmc.store.out.request.DeliverOrderRequest;
import org.scmc.store.out.request.HoldCancelRequest;
import org.scmc.store.out.request.HoldRequest;
import org.scmc.store.stk.qty.dto.StkQtyDto;
import org.scmc.store.stk.qty.request.LotStkQtyRequest;
import org.scmc.store.stk.qty.request.StkQtyRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author yuanwenjun
 * @date 2019/11/29 15:39
 */

@Component
@Slf4j
public class StoreWmsClientRemoteFactory implements FallbackFactory<StoreWmsClient> {
    @Override
    public StoreWmsClient create(Throwable cause) {
        return new StoreWmsClient() {
            @Override
            public BizRsp<String> importAsn(AsnRequest request) {
                log.error("importAsn error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp<String> asnClose(AsnRequest request) {
                log.error("asnClose error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp<String> asnUpdate(AsnRequest request) {
                log.error("asnUpdate error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp<ReceiveResponse> asnReceive(AsnRequest request) {
                log.error("asnReceive error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp<ReceiveResponse> partAsnReceive(AsnRequest request) {
                log.error("partAsnReceive error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);

            }

            @Override
            public BizRsp holdStock(HoldRequest request) {
                log.error("payConfirm error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp importDo(DeliverOrderRequest request) {
                log.error("payConfirm error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }


            @Override
            public BizRsp canceltDo(DeliverOrderRequest request) {
                log.error("canceltDo error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp<List<LocationDto>> getLocationList(LocationRequest request) {
                log.error("getLocationList error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp<List<LocationDto>> listByLocationIds(LocationRequest request) {
                log.error("listByLocationIds error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp cancelOpSeq(HoldRequest request) {
                log.error("cancelOpSeq error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }

            @Override
            public BizRsp cancelByCTD(HoldCancelRequest request) {
                return null;
            }

            @Override
            public BizRsp<List<StkQtyDto>> listQty(StkQtyRequest request) {
                log.error("cancelOpSeq error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);

            }

            @Override
            public BizRsp<Map<String, BigDecimal>> listQtyByPoAndSku(LotStkQtyRequest request) {
                log.error("listQtyByPoAndSku error,request={},error={}", JSONObject.toJSONString(request), ExceptionUtils.getStackTrace(cause));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, cause.getMessage(), cause);
            }


        };
    }
}
