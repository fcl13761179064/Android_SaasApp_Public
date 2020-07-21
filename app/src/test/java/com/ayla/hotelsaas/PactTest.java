package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.User;
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
import io.reactivex.Observable;

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
                .path("/api/v2/sso/login")
                .method("POST")
                .body(new PactDslJsonBody()
                        .stringType("account", "111")
                        .stringType("password", "222")
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "")
                        .object("data", new PactDslJsonBody()
                                .stringType("authToken", "eijsaidf")
                                .stringType("refreshToken", "ewwwrwr")
                                .stringType("expireTime", "324324324324")))
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
                                        .minArrayLike(1)
                                        .numberType("id", 1)
                                        .stringType("name", "电工")
                                        .object("sub", new PactDslJsonArray()
                                                .minArrayLike(1)
                                                .stringType("name", "网关")
                                                .numberType("cuid", 0)
                                                .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                                .closeObject()
                                        )
                                        .closeObject()
//                                //添加第二个
                                        .object()
                                        .numberType("id", 1)
                                        .stringType("name", "照明")
                                        .object("sub", new PactDslJsonArray()
                                                .minArrayLike(1)
                                                .stringType("name", "节点")
                                                .numberType("cuid", 1)
                                                .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                                .closeObject()
                                        )
                                        .closeObject()
                        ))
                //获取工单列表列表的数据
                .given("获取工单列表的数据")
                .uponReceiving("获取工单列表info,展示数据")
                .path("/api/v1/construction/constructbill")
                .matchQuery("pageNo", ".*", "1")
                .matchQuery("pageSize", ".*", "10")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .object("pageInfo", new PactDslJsonBody(
                                ).stringType("pageNo", "1").stringType("pageSize", "10"))
                                .object("resultList", new PactDslJsonArray()
                                        .object()
                                        .stringType("businessId", "111111111")
                                        .stringType("title", "成都酒店111111")
                                        .stringType("startDate", "2018-2-5")
                                        .stringType("endDate", "2019-5-9")
                                        .stringType("constructionStatus", "待施工111111")
                                        .closeObject()
                                        .object()
                                        .stringType("businessId", "2222222")
                                        .stringType("title", "成都酒店22222")
                                        .stringType("startDate", "2018-2-5")
                                        .stringType("endDate", "2019-5-9")
                                        .stringType("constructionStatus", "待施工222222")
                                        .closeObject()
                                ).closeObject()))

                //获取房间数据
                .given("获取工单列表下房间号列表")
                .uponReceiving("展示施工单下的房间号列表页面")
                .path("/api/v1/construction/billrooms")
                .matchQuery("pageNo", ".*", "1")
                .matchQuery("pageSize", ".*", "10")
                .matchQuery("billId", ".*", "444444")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "1001")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .object("pageInfo", new PactDslJsonBody(
                                ).stringType("pageNo", "1").stringType("pageSize", "10"))
                                .object("resultList", new PactDslJsonArray()
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
                .given("获取工单下房间号下的设备列表数据")
                .uponReceiving("展示设备类型图片，以及设备信息")
                .path("/device_list")
                .matchQuery("pageNo", ".*", "1")
                .matchQuery("pageSize", ".*", "10")
                .matchQuery("roomId", ".*", "444444")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "1001")
                        .object("data", new PactDslJsonArray()
                                .object()
                                .object("pageInfo", new PactDslJsonBody(
                                ).stringType("pageNo", "1").stringType("pageSize", "10"))
                                .object("resultList", new PactDslJsonArray()
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
                .body(new PactDslJsonBody()
                        .stringType("device_id", "123")
                        .numberType("scope_id", 1)
                        .numberType("cuid", 2)
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
                .body(new PactDslJsonBody()
                        .stringType("device_id", "123")
                        .numberType("scope_id", 2)
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
                .path("/notify_gateway_config_enter")
                .method("POST")
                .body(new PactDslJsonBody()
                        .stringType("device_id", "123")
                        .numberType("cuid", 1)
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))
                //通知网关退出配网模式
                .given("通知成功")
                .uponReceiving("通知网关退出配网模式")
                .path("/notify_gateway_config_exit")
                .method("POST")
                .body(new PactDslJsonBody()
                        .stringType("device_id", "123")
                        .numberType("cuid", 1)
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))
                //获取候选节点
                .given("获取成功")
                .uponReceiving("获取候选节点")
                .path("/fetch_gateway_candidates")
                .matchQuery("device_id", ".*", "123")
                .matchQuery("cuid", ".*", "1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .object("data", new PactDslJsonArray()
                                .maxArrayLike(1)
                                .stringType("id", "123")
                                .numberType("cuid", 1)
                                .closeObject()
                        ))
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
                                        .minArrayLike("items", 1)
                                        .numberType("targetDeviceType", 2)
                                        .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                        .stringType("leftValue", "StatusLightSwitch")
                                        .stringType("operator", "==")
                                        .numberType("rightValue", 1)
                                        .numberType("rightValueType", 1)
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
                        .numberType("scopeId", 1111)
                        .stringType("ruleName", "222")
                        .stringType("ruleDescription", "222")
                        .numberType("ruleType", 2)
                        .object("action", new PactDslJsonBody()
                                .stringType("expression", "1111")
                                .minArrayLike("items", 1)
                                .numberType("targetDeviceType", 2)
                                .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                .stringType("leftValue", "StatusLightSwitch")
                                .stringType("operator", "==")
                                .numberType("rightValue", 1)
                                .numberType("rightValueType", 1)
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
                //更新RuleEngine
                .given("更新成功")
                .uponReceiving("更新RuleEngine")
                .path("/update_rule_engine")
                .body(new PactDslJsonBody()
                        .numberType("ruleId", 123)
                        .numberType("scopeId", 1111)
                        .stringType("ruleName", "222")
                        .stringType("ruleDescription", "222")
                        .numberType("ruleType", 2)
                        .object("action", new PactDslJsonBody()
                                .stringType("expression", "1111")
                                .minArrayLike("items", 1)
                                .numberType("targetDeviceType", 2)
                                .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                .stringType("leftValue", "StatusLightSwitch")
                                .stringType("operator", "==")
                                .numberType("rightValue", 1)
                                .numberType("rightValueType", 1)
                                .closeObject()
                                .closeArray()
                        )
                )
                .method("PUT")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))
                //执行一个场景
                .given("执行成功")
                .uponReceiving("执行一个场景")
                .path("/run_rule_engine")
                .body(new PactDslJsonBody()
                        .numberType("ruleId", 111)
                )
                .method("POST")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .booleanType("data", true))
                //删除一个场景
                .given("删除成功")
                .uponReceiving("删除一个场景")
                .path("/delete_rule_engine")
                .body(new PactDslJsonBody()
                        .numberType("ruleId", 111)
                )
                .method("DELETE")
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
        RequestModel.getInstance()
                .login("111", "222")
                .test().assertNoErrors();

        RetrofitHelper.getInstance()
                .getApiService()
                .fetchDeviceCategory()
                .test().assertNoErrors();

        //获取工单集
        RequestModel.getInstance()
                .getWorkOrderList(1, "10")
                .test().assertNoErrors();

        //获取房间号
        RequestModel.getInstance()
                .getRoomOrderList("444444", 1, "10")
                .test().assertNoErrors();

        //获取设备列表信息
        RequestModel.getInstance()
                .getDeviceList("444444", 1, "10")
                .test().assertNoErrors();


        {//绑定设备
            RequestModel.getInstance()
                    .bindDeviceWithDSN("111", 1, 2).test().assertNoErrors();
        }

        {//解绑设备
            RequestModel.getInstance()
                    .unbindDeviceWithDSN("111", 2).test().assertNoErrors();
        }
        {//通知网关进入配网模式
            RequestModel.getInstance()
                    .notifyGatewayBeginConfig("11111", 1).test().assertNoErrors();
        }
        {//通知网关退出配网模式
            RequestModel.getInstance()
                    .notifyGatewayFinishConfig("11111", 1).test().assertNoErrors();
        }
        {//获取网关的候选节点
            RequestModel.getInstance()
                    .fetchCandidateNodes("11111", 1).test().assertNoErrors();
        }
        {//通过房间号获取下属的RuleEngines
            RequestModel.getInstance()
                    .fetchRuleEngines("11111").test().assertNoErrors();
        }
        {//保存RuleEngine
            RuleEngineBean ruleEngineBean = new RuleEngineBean();
            ruleEngineBean.setScopeId(1111);
            ruleEngineBean.setRuleType(2);
            ruleEngineBean.setRuleName("ruleengine");
            ruleEngineBean.setRuleDescription("ruleengine");
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
                    .saveRuleEngine(ruleEngineBean).test().assertNoErrors();
        }
        {//更新RuleEngine
            RuleEngineBean ruleEngineBean = new RuleEngineBean();
            ruleEngineBean.setRuleId(123);
            ruleEngineBean.setScopeId(1111);
            ruleEngineBean.setRuleType(2);
            ruleEngineBean.setRuleName("ruleengine");
            ruleEngineBean.setRuleDescription("ruleengine");
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
                    .updateRuleEngine(ruleEngineBean).test().assertNoErrors();
        }
        {//执行一个场景
            RequestModel.getInstance()
                    .runRuleEngine(1111).test().assertNoErrors();
        }
        {//删除一个场景
            RequestModel.getInstance()
                    .deleteRuleEngine(1111).test().assertNoErrors();
        }
    }
}
