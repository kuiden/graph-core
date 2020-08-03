package com.tuhu.store.saas.marketing.response;

import com.tuhu.store.saas.marketing.po.Activity;
import com.tuhu.store.saas.marketing.po.ActivityCustomer;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/4
 * Time: 13:49
 * Description:
 */
@Data
public class ActivityCustomerItem extends ActivityCustomer {
    private Activity activity;
}