package com.ayla.hotelsaas.application;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @描述 常量类
 * @作者 丁军伟
 * @时间 2017/9/6
 */
public class Constance {
    /**
     * 是否是网络测试环境
     */
    public static boolean isNetworkDebug = false;

    /**
     * 测试环境地址
     */
    public static final String BASE_URL_BATA = "http://divider_bottom_bg_white.gewala.net/openapi2/router/";
    /**
     * 正式环境地址
     */
    public static final String BASE_URL = "https://openapi2.gewara.com/router/";


    public static class API {

    }

    /**
     * sp文件中的key
     */
    public static class Shared_Key {
        public static final String LOCKNO = "lockNo";
    }

    /**
     * 座位的状态
     */
    public class SEAT_STATUS {
        /**
         * 空闲
         */
        public static final int STATUS_IDLE = 1;
        /**
         * 锁定
         */
        public static final int STATUS_LOCKED = 2;
        /**
         * 预留
         */
        public static final int STATUS_RESERVED = 3;
        /**
         * 售出
         */
        public static final int STATUS_SOLD = 4;
        /**
         * 预购
         */
        public static final int STATUS_ORDERED = 5;
        /**
         * 作废
         */
        public static final int STATUS_DEAD = 6;

        /**
         * 自己定义 当座位为预留预购时候,在被选中进行区分
         */
        public static final int STATUS_UNLOCK = 10;
        /**
         * 自己定义 代表默认的状态
         */
        public static final int STATUS_DEFAULT = -10;

        /**
         * 自己定义 黑色不可操作状态
         */
        public static final int NO_OPERABLE_STATE = -1;
    }

    /**
     * 凭证到有做和无座的动作预购
     */
    public class VOUCHER_ACTION {
        /**
         * sale线上出票
         */
        public static final String SALE = "sale";
        /**
         * refund申请退票
         */
        public static final String REFUND = "refund";
        /**
         * again申请重打
         */
        public static final String AGAIN = "again";
        /**
         * cancel取消预留
         */
        public static final String CANCEL = "cancel";
    }

    //用户是否登录
    public static boolean UserIsLogin = false;
    //电子票
    public static final int ELECTRONIC = 1;
    //快递
    public static final int EXPRESS = 2;
    //电子票和快递
    public static final int ELECTRONIC_AND_EXPRESS = 3;
    //无纸化电子票
    public static final int PAPERLESS_TICKET = 4;


    //退票
    public static final String VOUCHER_TYPE_TP = "TYPE_TP";
    //重打
    public static final String VOUCHER_TYPE_CD = "TYPE_CD";
    //作废退票
    public static final String VOUCHER_TYPE_TPD = "TYPE_TPD";
    //批准退票
    public static final String VOUCHER_TYPE_TPY = "TYPE_TPY";
    //批准重打
    public static final String VOUCHER_TYPE_CDY = "TYPE_CDY";
    //驳回退票
    public static final String VOUCHER_TYPE_TPB = "TYPE_TPB";
    //驳回重打
    public static final String VOUCHER_TYPE_CDB = "TYPE_CDB";
    //预购凭证
    public static final String VOUCHER_TYPE_YG = "TYPE_YG";
    //预留凭证
    public static final String VOUCHER_TYPE_YL = "TYPE_YL";
    //出票凭证
    public static final String VOUCHER_TYPE_CP = "TYPE_CP";
    //其他凭证
    public static final String VOUCHER_TYPE_OTHER = "TYPE_OTHER";

    //出票类型
    public static final String CHUPIAO = "1";    //出票
    public static final String ZENGPIAO = "2";    //赠票
    public static final String TIPIAO = "3";    //提票
    public static final String TUIPIAO = "4";    //退票

    //凭证类型
    public static final String TYPE_CP = "S";    //出票凭证：S
    public static final String TYPE_YL = "L";    //预留凭证：L
    public static final String TYPE_YG = "Y";    //预购凭证：Y
    public static final String TYPE_CD = "C";    //重打凭证：C
    public static final String TYPE_TP = "T";    //退票凭证：T

    //状态
    public static final String V_STATUS_N = "N";    //初始状态
    public static final String V_STATUS_D = "D";    //未批准
    public static final String V_STATUS_Y = "Y";    //已经批准
    public static final String VD_STATUS_N = "N";    //初始状态
    public static final String VD_STATUS_T = "T";    //申请退票，取消重打
    public static final String VD_STATUS_Y = "Y";    //票已经退票或者重打

    //销售方式  目前暂定7种
    //普通售票
    public static final int COMMONE = 0;
    //保留票
    public static final int RESERVED = 1;
    //工作票
    public static final int WORK = 2;
    //大客户
    public static final int LCUSTOMER = 3;
    //主办方
    public static final int ORGANIZER = 4;
    //乙方购票
    public static final int PARTNER = 5;
    //合同票
    public static final int CONTRACT = 6;
    /**
     * 销售方式列表
     */
    private static List<String> SaleTypeList = null;

    /**
     * 获取销售方式
     * 目前暂定7种
     *
     * @return
     */
    public static List<String> getSaleTypeList() {
        if (SaleTypeList == null) {
            SaleTypeList = new LinkedList<>();
            SaleTypeList.add("普通售票");
            SaleTypeList.add("普通售票");
            SaleTypeList.add("保留票");
            SaleTypeList.add("工作票");
            SaleTypeList.add("大客户");
            SaleTypeList.add("主办方");
            SaleTypeList.add("乙方购票");
            SaleTypeList.add("合同票");
        }
        return SaleTypeList;
    }

    /**
     * 支付方式
     */
    private static List<String> PayTypeList;

    /**
     * 支付方式 1：网上银联 2：支付宝 3：在线网银 4：现金 5：其他信用卡 6银行卡 7会员卡 8：学生证 9：兑换券 10：支票 11：转账
     * 获取对应key的时候 +1
     *
     * @return
     */
    public static List<String> getPayTypeList() {
        if (PayTypeList == null) {
            PayTypeList = new LinkedList<>();
            PayTypeList.add("网上银联");
            PayTypeList.add("支付宝");
            PayTypeList.add("在线网银");
            PayTypeList.add("现金");
            PayTypeList.add("其他信用卡");
            PayTypeList.add("银行卡");
            PayTypeList.add("会员卡");
            PayTypeList.add("学生证");
            PayTypeList.add("兑换券");
            PayTypeList.add("支票");
            PayTypeList.add("转账");
        }
        return PayTypeList;
    }

    //颜色对应Map
    private static Map<Long, Integer> ColorMap = null;
    private static List<Integer> ColorArray = null;

    /**
     * 获取对应的color值
     *
     * @return
     */
    public static int getMatchColor(long colorIndex) {
        if (ColorArray == null) {
            ColorArray = new ArrayList<>();
            ColorArray.add(Color.rgb(255, 168, 0));
            ColorArray.add(Color.rgb(255, 0, 0));
            ColorArray.add(Color.rgb(234, 237, 7));
            ColorArray.add(Color.rgb(10, 239, 5));
            ColorArray.add(Color.rgb(0, 108, 255));
            ColorArray.add(Color.rgb(20, 180, 233));
            ColorArray.add(Color.rgb(20, 220, 233));
            ColorArray.add(Color.rgb(115, 0, 233));
            ColorArray.add(Color.rgb(233, 20, 220));
            ColorArray.add(Color.rgb(233, 20, 110));
            ColorArray.add(Color.rgb(171, 144, 97));
            ColorArray.add(Color.rgb(172, 57, 6));
            ColorArray.add(Color.rgb(180, 119, 6));
            ColorArray.add(Color.rgb(102, 146, 7));
            ColorArray.add(Color.rgb(19, 115, 19));
            ColorArray.add(Color.rgb(31, 122, 115));
            ColorArray.add(Color.rgb(32, 57, 115));
            ColorArray.add(Color.rgb(160, 132, 251));
            ColorArray.add(Color.rgb(113, 45, 132));
            ColorArray.add(Color.rgb(255, 110, 124));
            ColorArray.add(Color.rgb(196, 255, 244));
            ColorArray.add(Color.rgb(186, 215, 111));
            ColorArray.add(Color.rgb(162, 163, 37));
            ColorArray.add(Color.rgb(107, 96, 55));
            ColorArray.add(Color.rgb(106, 54, 58));
            ColorArray.add(Color.rgb(219, 112, 147));
            ColorArray.add(Color.rgb(119, 116, 153));
            ColorArray.add(Color.rgb(0, 0, 255));
            ColorArray.add(Color.rgb(173, 255, 47));
            ColorArray.add(Color.rgb(255, 215, 0));
            ColorArray.add(Color.rgb(255, 228, 181));
            ColorArray.add(Color.rgb(210, 105, 30));
            ColorArray.add(Color.rgb(199, 21, 133));
            ColorArray.add(Color.rgb(176, 196, 222));
            ColorArray.add(Color.rgb(95, 158, 160));
        }
        if (ColorArray.size() > colorIndex) {
            return ColorArray.get((int) colorIndex);
        } else {
            return Color.BLACK;
        }
    }

}
