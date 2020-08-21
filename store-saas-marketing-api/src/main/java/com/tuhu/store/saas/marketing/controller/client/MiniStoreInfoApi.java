package com.tuhu.store.saas.marketing.controller.client;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.mini.BaseEndUserApi;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.EndUserVistiedStoreRequest;
import com.tuhu.store.saas.marketing.response.EndUserVisitedStoreResp;
import com.tuhu.store.saas.marketing.service.IEndUserVisitedStoreService;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.vo.ClientStoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping("/client/mini/store")
public class MiniStoreInfoApi extends BaseEndUserApi {

    @Autowired
    private StoreInfoClient storeInfoClient;


    @Autowired
    private IEndUserVisitedStoreService iEndUserVisitedStoreService;

    @GetMapping("/detail")
    public BizBaseResponse getStoreInfoForClient(ClientStoreVO req) {
        BizBaseResponse<ClientStoreDTO> resultData = storeInfoClient.getStoreInfoForClient(req);
        return resultData;
    }


    /**
     * 客户访问的门店列表
     *
     * @param endUserVistiedStoreRequest
     * @return
     */
    @RequestMapping(value = "/query", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public BizBaseResponse<List<EndUserVisitedStoreResp>> findAllVisitedStoresByOpenIdCode(@NotNull EndUserVistiedStoreRequest endUserVistiedStoreRequest) {
        List<EndUserVisitedStoreResp> endUserVisitedStores = iEndUserVisitedStoreService.findAllVisitedStoresByOpenIdCode(endUserVistiedStoreRequest);
        return new BizBaseResponse(endUserVisitedStores);
    }

}