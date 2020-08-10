package com.tuhu.store.saas.marketing.controller.client;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.mini.BaseEndUserApi;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.vo.ClientStoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/client/mini/store")
public class MiniStoreInfoApi extends BaseEndUserApi {

    @Autowired
    private StoreInfoClient storeInfoClient;

    @GetMapping("/detail")
    public BizBaseResponse getStoreInfoForClient(ClientStoreVO req) {
        BizBaseResponse<ClientStoreDTO> resultData = storeInfoClient.getStoreInfoForClient(req);
        return resultData;
    }

}