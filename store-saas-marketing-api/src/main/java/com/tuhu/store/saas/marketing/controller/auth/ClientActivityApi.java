package com.tuhu.store.saas.marketing.controller.auth;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.VerificationCodeUtils;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.remote.CustomerAuthDto;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.remote.auth.StoreAuthClient;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import com.tuhu.store.saas.marketing.request.ActivityApplyReq;
import com.tuhu.store.saas.marketing.request.GetValidCodeReq;
import com.tuhu.store.saas.marketing.request.MiniProgramNotifyReq;
import com.tuhu.store.saas.marketing.response.ActivityApplyResp;
import com.tuhu.store.saas.marketing.response.ActivityCustomerResp;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.service.IClientActivityService;
import com.tuhu.store.saas.marketing.service.MiniAppService;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @time 2020-08-10
 * @auther kudeng
 */
@RestController
@RequestMapping("/client/h5")
@Api(tags = "C端H5营销活动服务")
@Slf4j
public class ClientActivityApi {

    private static final String verificationCodeKey = "STORE_SAAS_VERI_CODE";


    @Autowired
    private VerificationCodeUtils verificationCodeUtils;


    @Autowired
    StoreRedisUtils storeRedisUtils;

    @Autowired
    StoreUserClient storeUserClient;

    @Autowired
    StoreAuthClient storeAuthClient;

    @Autowired
    IClientActivityService iClientActivityService;

    @Value("${add.reservation.verificationCode.expireTime}")
    private Integer expireTime = 5;

    @Autowired
    MiniAppService miniAppService;

    @Autowired
    private CustomerClient iCustomerService;


    @GetMapping("/activity/detail")
    @ApiOperation("C端H5营销活动详情")
    public BizBaseResponse detail(@RequestParam String encryptedCode,HttpServletRequest request) {
        ActivityResp resp = null;
        checkLogged(request);
        //不做用户信息强校验
        try {
            resp = iClientActivityService.getActivityDetailByEncryptedCode(encryptedCode);
            EndUserContextHolder.remove();
            if(resp ==null) {
               return BizBaseResponse.operationFailed("查不到此活动，请检查编码！");
            }
        }catch (MarketingException e){
            log.error("活动详情请求失败, 入参{}",e.getMessage());
        }catch (Exception e){
            log.error("活动详情，服务异常",e);
        }
        return BizBaseResponse.success(resp);
    }

    @PostMapping("/getValidCode")
    @ApiOperation("营销服务获取验证码")
    public BizBaseResponse getValidCode(@RequestBody GetValidCodeReq req) {
        String sendResult  = verificationCodeUtils.send(SMSTypeEnum.SAAS_MINI_ORDER_CREATE_CODE.templateCode(),req.getPhone(),expireTime,TimeUnit.MINUTES);
        return BizBaseResponse.success(sendResult);
    }

    @PostMapping("/activity/apply")
    @ApiOperation("C端H5营销活动报名")
    public BizBaseResponse<ActivityApplyResp> apply(@Validated @RequestBody ActivityApplyReq applyReq) {
        if(applyReq == null){
            return BizBaseResponse.operationFailed("参数错误！");
        }
        String applyPhoneNumber=applyReq.getTelephone();
        if(StringUtils.isBlank(applyPhoneNumber)){
            return BizBaseResponse.operationFailed("请输入正确的手机号");
        }
        if(StringUtils.isBlank(applyReq.getEncryptedCode() )){
            return BizBaseResponse.operationFailed("请输入活动编码");
        }
        if(StringUtils.isBlank(applyReq.getVerificationCode())){
            return BizBaseResponse.operationFailed("请输入验证码");
        }
        //校验验证码
        String code = storeRedisUtils.redisGet(verificationCodeKey+applyReq.getTelephone());
        if(code == null){
            return BizBaseResponse.operationFailed("验证码已过期");
        }
        if(!code.equals(applyReq.getVerificationCode())){
            return BizBaseResponse.operationFailed("验证码错误");
        }
        //验证码需要重新获取
        storeRedisUtils.redisDelete(verificationCodeKey+applyReq.getTelephone());
        try {
            return new BizBaseResponse(iClientActivityService.clientActivityApply(applyReq));
        } catch (MarketingException e){
            log.error("营销活动报名失败",e.getMessage());
            return BizBaseResponse.operationFailed("报名失败，"+e.getMessage());
        } catch(Exception e) {
            log.error("营销活动报名服务异常，入参：{}", applyReq, e);
            return BizBaseResponse.operationFailed("服务异常");
        }
    }

    @GetMapping("/activityOrderDetail")
    @ApiOperation("C端H5获取活动订单详情")
    public BizBaseResponse orderDetail(@RequestParam String  encryptedCode,HttpServletRequest request){
        checkLogged(request);
        if(EndUserContextHolder.getUser()==null){
            return new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "请传递Authorization信息");
        }
        if(StringUtils.isBlank(encryptedCode)){
            return new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "请传递活动信息");
        }
        try {
            ActivityCustomerResp activityCustomerResp =iClientActivityService.getActivityCustomerDetail(encryptedCode,EndUserContextHolder.getTelephone());
            //车主用户登录
            CustomerVO customerVO = new CustomerVO();
            customerVO.setPhone(EndUserContextHolder.getTelephone());
            customerVO.setStoreId(EndUserContextHolder.getStoreId());
            customerVO.setTenantId(EndUserContextHolder.getTenantId());
            List<CustomerDTO> customerDTOList = iCustomerService.getCustomer(customerVO).getData();
            if(customerDTOList.size()<1){
                throw new MarketingException("还没有您的注册记录！");
            }
            CustomerDTO customerDTO = customerDTOList.get(0);
            if (!customerDTO.getId().equals(activityCustomerResp.getCustomerId())){
                //用户账号不同不允许看
                log.warn("活动详情过滤机制：客户用户Id:{},source{}",customerDTO.getId(),activityCustomerResp.getCustomerId());
                throw new MarketingException("没有报名记录，请先报名！");
            }
            //移除当前登录信息
            EndUserContextHolder.remove();
            return new BizBaseResponse(activityCustomerResp);
        }catch (MarketingException e){
            log.error("营销活动报名失败",e.getMessage());
            return BizBaseResponse.operationFailed("获取订单详情失败，"+e.getMessage());
        } catch(Exception e) {
            log.error("营销活动报名服务异常，入参：{}", e);
            return BizBaseResponse.operationFailed("服务异常");
        }

    }

    /**
     * 根据token 获取当前登录信息
     * @param request
     */
    private void checkLogged(HttpServletRequest request){
        //放入登录信息
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("登录信息请求：authorization:{}",authorization);
        if (authorization==null){
            return ;
        }
        try {
            BizBaseResponse<EndUser> endUserResult = storeAuthClient.getUserByToken();
            log.info("==storeAuthClient.getUserByToken=={}", JSONObject.toJSONString(endUserResult));
            if (null != endUserResult && endUserResult.isSuccess() && null != endUserResult.getData()) {
                EndUserContextHolder.setUser(endUserResult.getData());
            }
        } catch (Exception e) {
            log.error("获取登录用户信息异常", e);
        }
    }

    /**
     * 发送预约成功小程序通知
     *
     * @param miniProgramNotifyReq
     * @return
     */
    @RequestMapping(value = "/user/mini/notify", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public BizBaseResponse miniProgramNotify(@RequestBody @NotNull @Validated MiniProgramNotifyReq miniProgramNotifyReq) {
        Object result = miniAppService.miniProgramNotify(miniProgramNotifyReq);
        return new BizBaseResponse(result);
    }


    @GetMapping(value = "/activityOrder/getQrCode",produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation("获取活动订单二维码")
    @ResponseBody
    public byte[] getQrCode(@RequestParam String code,HttpServletRequest request){
        checkLogged(request);
        if(EndUserContextHolder.getUser()==null){
            return null;
        }
        byte[] codeStream =iClientActivityService.getQrCodeOfActivityCustomer(code);
        EndUserContextHolder.remove();
        return codeStream;
    }



}
