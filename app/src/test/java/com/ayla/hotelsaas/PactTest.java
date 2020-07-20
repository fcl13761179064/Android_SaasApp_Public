package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.ayla.hotelsaas.mvp.model.RequestModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

public class PactTest {

    static final Map<String, String> HEADERS;

    static {

        HEADERS = new HashMap<>();
        HEADERS.put("Content-Type", "application/json");
    }

    @Before
    public void setUp() {
        Constance.BASE_URL = "http://localhost:9292";
    }

    @Rule
    public PactProviderRule mockProvider = new PactProviderRule("construction_backend", "localhost", 9292, PactSpecVersion.V1, this);

    @Pact(provider = "construction_backend", consumer = "construction_app")
    public RequestResponsePact createFragment(PactDslWithProvider builder) throws UnsupportedEncodingException {
        return builder
                //正常用户登录
                .given("")
                .uponReceiving("正确的账号密码登录")
                .path("/login")
                .method("POST")
                .body("username=111&password=222", "application/x-www-form-urlencoded")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "")
                        .object("data", new PactDslJsonBody().stringType("token")))
                //用户密码错误
                .given("")
                .uponReceiving("错误的账号密码登录")
                .path("/login")
                .method("POST")
                .body("username=111&password=333", "application/x-www-form-urlencoded")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody().numberValue("code", 1001)
                        .stringType("error", "用户名或密码错误"))
                //获取产品配网二级菜单列表
                .given("")
                .uponReceiving("获取产品配网二级菜单列表")
                .path("/device_add_category")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .numberType("id", 1)
                                .stringType("name", "电工")
                                .object("sub", new PactDslJsonArray()
                                        .object()
                                        .stringType("name", "节点")
                                        .numberType("cuid", 1)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                        //添加第二个
                                        .object()
                                        .stringType("name", "网关")
                                        .numberType("cuid", 0)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                )
                                .closeObject()
                                //添加第二个
                                .object()
                                .numberType("id", 1)
                                .stringType("name", "照明")
                                .object("sub", new PactDslJsonArray()
                                        .object()
                                        .stringType("name", "节点")
                                        .numberType("cuid", 1)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                        //添加第二个
                                        .object()
                                        .stringType("name", "网关")
                                        .numberType("cuid", 0)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                        //添加第二个
                                        .object()
                                        .stringType("name", "网关")
                                        .numberType("cuid", 0)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                        //添加第二个
                                        .object()
                                        .stringType("name", "网关")
                                        .numberType("cuid", 0)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                        //添加第二个
                                        .object()
                                        .stringType("name", "网关")
                                        .numberType("cuid", 0)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                        //添加第二个
                                        .object()
                                        .stringType("name", "网关")
                                        .numberType("cuid", 0)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                        //添加第二个
                                        .object()
                                        .stringType("name", "网关")
                                        .numberType("cuid", 0)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                )
                                .closeObject()
                        ))
                //项目列表的数据
                .given("获取工单列表的数据")
                .uponReceiving("获取工单列表/api/v1/construction/constructbill").path("/api/v1/construction/constructbill")
                .method("GET").willRespondWith().status(200).body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .stringType("currentPage", "1")
                                .stringType("pageSize", "10")
                                .object()
                                .object("workOrderContent", new PactDslJsonArray()
                                        .object()
                                        .stringType("businessId", "111111111")
                                        .stringType("projectName", "成都酒店111111")
                                        .stringType("startDate", "2018-2-5")
                                        .stringType("endDate", "2019-5-9")
                                        .stringType("progressStatus", "待施工111111")
                                        .closeObject()
                                        .object()
                                        .stringType("businessId", "22222222222")
                                        .stringType("projectName", "成都酒店222222")
                                        .stringType("startDate", "2018-2-5")
                                        .stringType("endDate", "2019-5-9")
                                        .stringType("progressStatus", "待施工222")
                                        .closeObject()
                                        .object()
                                        .stringType("businessId", "3333333333")
                                        .stringType("projectName", "成都酒店3333333")
                                        .stringType("startDate", "2018-2-5")
                                        .stringType("endDate", "2019-5-9")
                                        .stringType("progressStatus", "待施工33333")
                                        .closeObject()
                                        .object()
                                        .stringType("businessId", "44444444")
                                        .stringType("projectName", "成都酒店44444")
                                        .stringType("startDate", "2018-2-5")
                                        .stringType("endDate", "2019-5-9")
                                        .stringType("progressStatus", "待施工44444")
                                        .closeObject()
                                ).closeObject()))

                //获取房间数据
                .given("获取房间号数据")
                .uponReceiving("获取正确的房间号数据")
                .path("/room_order")
                .method("POST")
                .body(new PactDslJsonBody()
                        .stringType("businessId", "444444")
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "1001")
                        .stringType("currentPage", "1")
                        .stringType("pageSize", "10")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .object("roomOrderContent", new PactDslJsonArray()
                                        .object()
                                        .stringType("resourceRoomId", "1")
                                        .stringType("resourceRoomNum", "101")
                                        .closeObject()
                                        .object()
                                        .stringType("resourceRoomId", "2")
                                        .stringType("resourceRoomNum", "102")
                                        .closeObject()
                                ).closeObject()))
                //获取设备列表
                .given("获取设备列表")
                .uponReceiving("获取图片，以及设备信息")
                .path("/device_list")
                .method("POST")
                .body(new PactDslJsonBody()
                        .stringType("resourceRoomId", "444444")
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "1001")
                        .stringType("currentPage", "1")
                        .stringType("pageSize", "10")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .object("deviceListInfo", new PactDslJsonArray()
                                        .object()
                                        .stringType("deviceIconUrl", "http://192.168.0.2/icon/123.imag.png")
                                        .stringType("deviceName", "二路开关")
                                        .stringType("deviceStatus", "online")
                                        .closeObject()
                                        .object()
                                        .stringType("iconUrl", "http://192.168.0.2/icon/4566.imag.png")
                                        .stringType("deviceName", "三路开关")
                                        .stringType("deviceStatus", "offline")
                                        .closeObject()
                                ).closeObject()))
                //DSN绑定设备,绑定成功
                .given("绑定成功")
                .uponReceiving("DSN绑定设备")
                .path("/bind_device")
                .method("POST")
                .body(new PactDslJsonBody().stringType("device_id", "123")
                        .stringType("cuid", "1111")
                        .stringType("scope_id", "123")
                        .stringType("scope_type", "123")
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))
                //DSN解绑设备,绑定成功
                .given("解绑成功")
                .uponReceiving("DSN解绑设备")
                .path("/unbind_device")
                .method("POST")
                .body(new PactDslJsonBody().stringType("device_id", "123")
                        .stringType("scope_id", "123")
                        .stringType("scope_type", "123")
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))
                //通知网关进入配网模式
                .given("通知成功")
                .uponReceiving("通知网关进入配网模式")
                .path("/notify_gateway_config")
                .method("POST")
                .body(new PactDslJsonBody()
                        .stringType("device_id", "123")
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))
                //获取RuleEngines
                .given("获取成功")
                .uponReceiving("获取RuleEngines")
                .path("/fetch_rule_engines")
                .matchQuery("scope_id", ".*", "123")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .numberType("ruleId", 111)
                                .stringType("scopeId", "111")
                                .stringType("ruleName", "222")
                                .numberType("ruleType", 2)
                                .object("action", new PactDslJsonBody()
                                        .stringType("expression", "1111")
                                        .array("items")
                                        .object()
                                        .numberValue("targetDeviceType", 2)
                                        .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                        .stringType("leftValue", "StatusLightSwitch")
                                        .stringType("operator", "==")
                                        .numberValue("rightValue", 1)
                                        .numberValue("rightValueType", 1)
                                        .closeObject()
                                        .closeArray()
                                )
                                .closeObject()
                        ))
                //保存RuleEngine
                .given("保存成功")
                .uponReceiving("保存RuleEngine")
                .path("/save_rule_engine")
                .body(new PactDslJsonBody()
                        .numberType("ruleId", 111)
                        .stringType("scopeId", "111")
                        .stringType("ruleName", "222")
                        .stringType("ruleDescription", "222")
                        .numberType("ruleType", 2)
                        .object("condition", new PactDslJsonBody()
                                .stringType("expression", "")
                                .array("items")
                                .closeArray()
                        )
                        .object("action", new PactDslJsonBody()
                                .stringType("expression", "1111")
                                .array("items")
                                .object()
                                .numberValue("targetDeviceType", 2)
                                .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                .stringType("leftValue", "StatusLightSwitch")
                                .stringType("operator", "==")
                                .numberValue("rightValue", 1)
                                .numberValue("rightValueType", 1)
                                .closeObject()
                                .closeArray()
                        )
                )
                .method("POST")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))

                .toPact();
    }

    @Test
    @PactVerification("construction_backend")
    public void testLogin() {
        RetrofitHelper.getInstance()
                .getApiService()
                .login("111", "222")
                .test().assertNoErrors();

        RetrofitHelper.getInstance()
                .getApiService()
                .login("111", "333")
                .test().assertNoErrors();

        RetrofitHelper.getInstance()
                .getApiService()
                .fetchDeviceCategory()
                .test().assertNoErrors();
        //获取工单集
        RetrofitHelper.getInstance()
                .getApiService()
                .getWorkOrders()
                .test().assertNoErrors();

        //获取房间号
        RequestModel.getInstance()
                .getRoomOrderList("44444444")
                .test().assertNoErrors();

        //获取设备列表信息
        RequestModel.getInstance()
                .getDeviceList("44444444")
                .test().assertNoErrors();



        {//绑定设备
            RequestModel.getInstance()
                    .bindDeviceWithDSN("111", "222", "333", "444").test().assertNoErrors();
        }

        {//解绑设备
            RequestModel.getInstance()
                    .unbindDeviceWithDSN("111", "222", "333").test().assertNoErrors();
        }
        {//通知网关进入配网模式
            RequestModel.getInstance()
                    .notifyGatewayBeginConfig("11111").test().assertNoErrors();
        }
        {//通过房间号获取下属的RuleEngines
            RequestModel.getInstance()
                    .fetchRuleEngines("11111").test().assertNoErrors();
        }
        {//保存RuleEngine
            RuleEngineBean ruleEngineBean = new RuleEngineBean();
            ruleEngineBean.setRuleId(111);
            ruleEngineBean.setScopeId("1111");
            ruleEngineBean.setRuleName("ruleengine");
            ruleEngineBean.setRuleDescription("ruleengine");
            ruleEngineBean.setRuleType(2);
            RuleEngineBean.Condition condition = new RuleEngineBean.Condition();
            condition.setExpression("");
            condition.setItems(new ArrayList<>());
            ruleEngineBean.setCondition(condition);
            RuleEngineBean.Action action = new RuleEngineBean.Action();
            action.setExpression("11111");
            List<RuleEngineBean.Action.ActionItem> actionItems = new ArrayList<>();
            for (int j = 0; j < 1; j++) {
                RuleEngineBean.Action.ActionItem actionItem = new RuleEngineBean.Action.ActionItem();
                actionItem.setLeftValue("StatusLightSwitch");
                actionItem.setRightValue(1);
                actionItem.setOperator("==");
                actionItem.setRightValueType(1);
                actionItem.setTargetDeviceId("GADw3NnUI4Xa54nsr5tYz20000");
                actionItem.setTargetDeviceType(2);
                actionItems.add(actionItem);
            }
            action.setItems(actionItems);
            ruleEngineBean.setAction(action);

            RequestModel.getInstance()
                    .saveRuleEngines(ruleEngineBean).test().assertNoErrors();
        }
    }
}
