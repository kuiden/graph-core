package com.tuhu.store.saas.marketing.controller.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.constant.MiniNotifyConstant;
import com.tuhu.store.saas.marketing.controller.mini.BaseEndUserApi;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.EndUserVistiedStoreRequest;
import com.tuhu.store.saas.marketing.response.EndUserVisitedStoreResp;
import com.tuhu.store.saas.marketing.service.IEndUserVisitedStoreService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.ClientStoreVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/client/mini/store")
@Slf4j
public class MiniStoreInfoApi extends BaseEndUserApi {

    @Autowired
    private StoreInfoClient storeInfoClient;


    @Autowired
    private IEndUserVisitedStoreService iEndUserVisitedStoreService;

    @Value("${saas.marketing.default.storeImagePath}")
    private String defaultStoreImagePath;

    @GetMapping("/detail")
    public BizBaseResponse getStoreInfoForClient(ClientStoreVO req) {
        ClientStoreDTO clientStoreDTO = new ClientStoreDTO();
        log.info("C端小程序获取门店信息：{}", JSONObject.toJSONString(req));
        try {
            BizBaseResponse<ClientStoreDTO> resultData = storeInfoClient.getStoreInfoForClient(req);
            log.info("C端小程序获取门店信息返回：{}", JSON.toJSONString(resultData));
            if (Objects.nonNull(resultData) && Objects.nonNull(resultData.getData())) {
                clientStoreDTO = resultData.getData();
                if(Objects.isNull(clientStoreDTO.getOpeningEffectiveDate())){
                    clientStoreDTO.setOpeningEffectiveDate(DateUtils.parseDate(MiniNotifyConstant.OPENINGEFFECTIVEDATE));
                }
                if(Objects.isNull(clientStoreDTO.getOpeningExpiryDate())){
                    clientStoreDTO.setOpeningExpiryDate(DateUtils.parseDate(MiniNotifyConstant.OPENINGEXPIRYDATE));
                }
                if(Objects.isNull(clientStoreDTO.getImagePath())){
                    clientStoreDTO.setImagePath(defaultStoreImagePath);
                }
                if(CollectionUtils.isEmpty(clientStoreDTO.getImgUrlList())){
                    List<String> imgUrlList=Lists.newArrayList();
                    imgUrlList.add(defaultStoreImagePath);
                    clientStoreDTO.setImgUrlList(imgUrlList);
                }
            }
        } catch (Exception e) {
            log.error("C端小程序获取门店信息返回异常,{}", e);
        }
        return new BizBaseResponse(clientStoreDTO);
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