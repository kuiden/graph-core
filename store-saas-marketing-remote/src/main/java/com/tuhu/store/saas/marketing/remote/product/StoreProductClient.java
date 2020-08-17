package com.tuhu.store.saas.marketing.remote.product;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.dto.product.GoodsData;
import com.tuhu.store.saas.dto.product.IssuedDTO;
import com.tuhu.store.saas.dto.product.QueryGoodsListDTO;
import com.tuhu.store.saas.dto.product.ServiceGoodDTO;
import com.tuhu.store.saas.marketing.remote.crm.CustomerRemoteFactory;
import com.tuhu.store.saas.request.product.GoodsForMarketReq;
import com.tuhu.store.saas.response.product.ServiceGoodsListForMarketResp;
import com.tuhu.store.saas.vo.product.GoodsListVO;
import com.tuhu.store.saas.vo.product.IssuedVO;
import com.tuhu.store.saas.vo.product.QueryGoodsListVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @time 2020-08-04
 * @auther kudeng
 */
@FeignClient(name = "${feign.application.store.saas.product.name:store-saas-product}", fallbackFactory = StoreProductRemoteFactory.class)
public interface StoreProductClient {

    @PostMapping("/feign/product/IssuedSpu/issuedGoodOrServiceSpu")
    BizBaseResponse<IssuedDTO> issuedGoodOrServiceSpu(@RequestBody IssuedVO issuedVO);

    @PostMapping(value = "/feign/product/Goods/queryServiceGoodListBySpuCodes")
    BizBaseResponse<List<ServiceGoodDTO>> queryServiceGoodListBySpuCodes(@RequestBody List<String> codeList, @RequestParam("storeId") Long storeId, @RequestParam("tenantId") Long tenantId);

    @PostMapping("/feign/product/Goods/queryServiceGoodListByCodesAndStoreId")
    BizBaseResponse<List<GoodsData>> queryServiceGoodListByCodesAndStoreId(@RequestBody List<String> codeList, @RequestParam("storeId") Long storeId);

    @PostMapping(value = "/feign/product/Goods/queryGoodsListV2")
    BizBaseResponse<List<QueryGoodsListDTO>> queryGoodsListV2(@RequestBody QueryGoodsListVO queryGoodsListVO);

    @PostMapping("/feign/product/Goods/queryBatchGoods")
    BizBaseResponse<List<ServiceGoodDTO>> queryBatchGoods(@RequestBody List<String> codeList, @RequestParam("storeId") Long storeId, @RequestParam("tenantId") Long tenantId, @RequestParam("carPosition") String carPosition) ;

    @PostMapping(value = "/feign/product/Goods/market/serviceGoodsForFeign")
    BizBaseResponse<PageInfo<ServiceGoodsListForMarketResp>> serviceGoodsForFeign(@RequestBody @Validated GoodsForMarketReq goodsForMarketReq);
}
