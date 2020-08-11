package com.tuhu.store.saas.marketing.remote.wms;

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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 门店仓
 *
 * @author yuanwenjun
 * @date 2019/11/29 15:38
 */

@FeignClient(name = "${feign.application.saas.wms.store.name}", fallbackFactory = StoreWmsClientRemoteFactory.class)
public interface StoreWmsClient {

    /**
     * 生成入库单
     */
    @PostMapping(value = "/asn/asnImport")
    BizRsp<String> importAsn(@RequestBody AsnRequest request);


    /**
     * 取消入库单
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/asn/asnClose")
    BizRsp<String> asnClose(@RequestBody AsnRequest request);

    /**
     * 更新入库单
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/asn/asnUpdate")
    BizRsp<String> asnUpdate(@RequestBody AsnRequest request);


    /**
     * 门店收货
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/asn/asnReceive")
    BizRsp<ReceiveResponse> asnReceive(@RequestBody AsnRequest request);



    /**
     * 门店部分收货
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/asn/part/asnReceive")
    BizRsp<ReceiveResponse> partAsnReceive(@RequestBody AsnRequest request);

    /**
     * 预占库存
     * @param request
     * @return
     */
    @PostMapping(value = "/hold/holdStock")
    BizRsp holdStock(@RequestBody HoldRequest request);


    /**
     * 出库
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/deliverOrder/import")
    BizRsp importDo(@RequestBody DeliverOrderRequest request);


    /**
     * 取消出库单
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/deliverOrder/cancel")
    BizRsp canceltDo(@RequestBody DeliverOrderRequest request);


    /**
     * 根据仓库获取库位
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/location/list")
    BizRsp<List<LocationDto>> getLocationList(@RequestBody LocationRequest request);


    /**
     * 根据库位id批量查询库位列表接口
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/location/listByLocationIds")
    BizRsp<List<LocationDto>> listByLocationIds(@RequestBody LocationRequest request);

    /**
     * 回退预占出库单
     *
     *
     */
    @PostMapping(value = "/hold/cancelOpSeq")
    BizRsp cancelOpSeq(@RequestBody HoldRequest request);



    @PostMapping(value = "/purchaseOrder/cancelByCTD")
    BizRsp cancelByCTD(@RequestBody HoldCancelRequest request);


    @PostMapping(value = "/stk/listQty")
    BizRsp<List<StkQtyDto>> listQty(@RequestBody StkQtyRequest request);

    @PostMapping (value = "/lotStk/listQtyByPoAndSku")
    BizRsp<Map<String, BigDecimal>> listQtyByPoAndSku(LotStkQtyRequest request);

}
