package com.tuhu.store.saas.marketing.enums;

public enum SrvReservationStatusEnum {
    UNCONFIRMED("UNCONFIRMED","待确认"),
    CONFIRMED("CONFIRMED","已确认"),
//    ORDERED("ORDERED","已预约"),
    OVER("OVER","已结束"),
    CANCEL("CANCEL","已取消")  ;
    private String enumCode;

    private String description;

    SrvReservationStatusEnum(String enumCode, String description) {
        this.enumCode = enumCode;
        this.description = description;
    }

    public static SrvReservationStatusEnum getSrvReservationStatusEnum(String enumCode){
        SrvReservationStatusEnum[] arr$=values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            SrvReservationStatusEnum status = arr$[i$];
            if (status.getEnumCode().equals(enumCode)) {
                return status;
            }
        }

        return null;
    }


    public String getEnumCode() {
        return enumCode;
    }

    public void setEnumCode(String enumCode) {
        this.enumCode = enumCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
