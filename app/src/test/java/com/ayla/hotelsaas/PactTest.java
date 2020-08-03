package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
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
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class PactTest {

    static final Map<String, String> HEADERS;

    static {

        HEADERS = new HashMap<>();
        HEADERS.put("Content-Type", "application/json");
    }

    @Before
    public void setUp() {
        Constance.BASE_URL = "http://localhost:9292/";
    }

    @Rule
    public PactProviderRule mockProvider = new PactProviderRule("construction_backend",
            "localhost", 9292, PactSpecVersion.V2, this);

    @Pact(provider = "construction_backend", consumer = "construction_app")
    public RequestResponsePact createFragment(PactDslWithProvider builder) {
        return builder
                //正常用户登录
                .given("登录成功")
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
                        .stringType("msg", "")
                        .object("data", new PactDslJsonBody()
                                .stringType("authToken", "eijsaidf")
                                .stringType("refreshToken", "ewwwrwr")
                                .stringType("expireTime", "324324324324")
                        )
                )
                //获取产品配网二级菜单列表
                .given("产品配网二级菜单列表")
                .uponReceiving("获取产品配网二级菜单列表")
                .path("/api/v1/construction/devicetypes")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .object("data", new PactDslJsonArray()
                                .minArrayLike(1)
                                .numberType("id", 1)
                                .stringType("name", "电工")
                                .object("sub", new PactDslJsonArray()
                                        .minArrayLike(1)
                                        .stringType("name", "网关")
                                        .numberType("cuId", 0)
                                        .numberType("deviceConnectType", 1)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                )
                                .closeObject()
                        )
                )
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
                        .stringType("msg", "success")
                        .object("data", new PactDslJsonBody()
                                .numberType("currentPage", 1)
                                .numberType("pageSize", 10)
                                .numberType("totalPages", 1)
                                .numberType("totalCount", 1)
                                .minArrayLike("resultList", 1, 2)
                                .numberType("id", 1)
                                .numberType("businessId", 1)
                                .stringType("title", "成都酒店111111")
                                .stringType("startDate", "2018-2-5")
                                .stringType("endDate", "2019-5-9")
                                .numberType("constructionStatus", 1)
                                .closeObject()
                                .closeArray()
                        )
                )
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
                        .stringType("msg", "success")
                        .object("data", new PactDslJsonBody()
                                .numberType("currentPage", 1)
                                .numberType("pageSize", 10)
                                .numberType("totalPages", 1)
                                .numberType("totalCount", 1)
                                .minArrayLike("resultList", 1, 2)
                                .numberType("roomId", 1)
                                .stringType("roomName", "房间1")
                                .closeObject()
                                .closeArray()
                        )
                )
                //获取设备列表
                .given("获取工单下房间号下的设备列表数据")
                .uponReceiving("展示设备类型图片，以及设备信息")
                .path("/api/v1/construction/device/list")
                .body(new PactDslJsonBody()
                        .numberType("pageNo", 1)
                        .numberType("pageSize", 10)
                        .numberType("roomId", 11111)
                )
                .method("POST")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "1001")
                        .object("data", new PactDslJsonBody()
                                .numberType("currentPage", 1)
                                .numberType("pageSize", 10)
                                .numberType("totalPages", 1)
                                .numberType("totalCount", 1)
                                .minArrayLike("devices", 1, 2)
                                .numberType("cuId", 1)
                                .stringType("deviceId", "SC000W000194710")
                                .stringType("deviceName", "二路开关")
                                .stringType("nickname", "ggggg")
                                .stringType("deviceStatus", "online")
                                .numberType("connectTypeId", 1)
                                .closeObject()
                                .closeArray()
                        )
                ).given("修改设备名称")
                .uponReceiving("")
                .matchPath("/api/v1/device/.*/info", "/api/v1/device/12345/info")
                .method("PUT")
                .body(new PactDslJsonBody()
                        .stringType("nickName", "开关")
                ).willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "success")
                        .stringType("data", "true")
                )//DSN绑定设备,绑定成功
                .given("绑定成功")
                .uponReceiving("DSN绑定设备")
                .path("/api/v1/construction/device/bind")
                .method("POST")
                .body(new PactDslJsonBody()
                        .stringType("deviceId", "123")
                        .numberType("scopeId", 1)
                        .numberType("cuId", 2)
                        .numberType("scopeType", 2)
                        .stringType("deviceName", "abc")
                        .stringType("nickName", "abc")
                )
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .nullValue("data")
                )
                //获取候选节点
                .given("获取成功")
                .uponReceiving("获取候选节点")
                .matchPath("/api/v1/construction/device/.*/candidates", "/api/v1/construction/device/12345/candidates")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .object("data", new PactDslJsonArray()
                                .minArrayLike(1, 2)
                                .numberType("cuId", 1)
                                .stringType("deviceId", "SC000W000194710")
                                .closeObject()
                        )
                )
                //获取RuleEngines
                .given("获取成功")
                .uponReceiving("获取RuleEngines")
                .matchPath("/api/v1/construction/scene/list/.*", "/api/v1/construction/scene/list/1234")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .object("data", new PactDslJsonArray()
                                .minArrayLike(1, 2)
                                .numberType("ruleId", 111)
                                .stringType("scopeId", "111")
                                .stringType("ruleName", "222")
                                .stringType("ruleDescription", "222")
                                .numberType("ruleType", 2)
                                .numberType("siteType", 2)
                                .numberType("status", 1)
                                .object("action", new PactDslJsonBody()
                                        .stringType("expression", "1111")
                                        .minArrayLike("items", 1, 2)
                                        .numberType("targetDeviceType", 2)
                                        .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                        .stringType("leftValue", "StatusLightSwitch")
                                        .stringType("operator", "==")
                                        .stringType("rightValue", "1")
                                        .numberType("rightValueType", 1)
                                        .closeObject()
                                        .closeArray()
                                )
                                .closeObject()
                        )
                )
                //保存RuleEngine
                .given("保存成功")
                .uponReceiving("保存RuleEngine")
                .path("/api/v1/construction/scene/save")
                .body(new PactDslJsonBody()
                        .numberType("scopeId", 1111)
                        .stringType("ruleName", "222")
                        .stringType("ruleDescription", "222")
                        .numberType("ruleType", 2)
                        .numberType("siteType", 2)
                        .numberType("status", 1)
                        .object("action", new PactDslJsonBody()
                                .stringType("expression", "1111")
                                .minArrayLike("items", 1, 2)
                                .numberType("targetDeviceType", 2)
                                .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                .stringType("leftValue", "StatusLightSwitch")
                                .stringType("operator", "==")
                                .stringType("rightValue", "1")
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
                        .nullValue("data")
                )
                //更新RuleEngine
                .given("更新成功")
                .uponReceiving("更新RuleEngine")
                .path("/api/v1/construction/scene/update")
                .body(new PactDslJsonBody()
                        .numberType("ruleId", 123)
                        .numberType("scopeId", 1111)
                        .stringType("ruleName", "222")
                        .stringType("ruleDescription", "222")
                        .numberType("ruleType", 2)
                        .numberType("siteType", 2)
                        .numberType("status", 1)
                        .object("action", new PactDslJsonBody()
                                .stringType("expression", "1111")
                                .minArrayLike("items", 1, 2)
                                .numberType("targetDeviceType", 2)
                                .stringType("targetDeviceId", "GADw3NnUI4Xa54nsr5tYz20000")
                                .stringType("leftValue", "StatusLightSwitch")
                                .stringType("operator", "==")
                                .stringType("rightValue", "1")
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
                        .nullValue("data")
                )
                //执行一个场景
                .given("执行成功")
                .uponReceiving("执行一个场景")
                .path("/api/v1/construction/scene/excute")
                .body(new PactDslJsonBody()
                        .numberType("ruleId", 111)
                )
                .method("POST")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .nullValue("data")
                )
                //删除一个场景
                .given("删除成功")
                .uponReceiving("删除一个场景")
                .path("/api/v1/construction/scene/delete")
                .body(new PactDslJsonBody()
                        .numberType("ruleId", 111)
                )
                .method("POST")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .nullValue("data")
                )
                //更新属性
                .given("更新成功")
                .uponReceiving("更新属性")
                .matchPath("/api/v1/construction/device/.*/property", "/api/v1/construction/device/1234/property")
                .body(new PactDslJsonBody()
                        .stringType("propertyName", "111")
                        .stringType("propertyValue", "2222")
                )
                .method("PUT")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("msg", "")
                        .nullValue("data")
                )

                .toPact();
    }

    @Test
    @PactVerification("construction_backend")
    public void testLogin() throws IOException {
        Observable.just(1)
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .login("111", "222");
                    }
                })//登录
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .getDeviceCategory();
                    }
                })//获取产品配网二级菜单列表
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .getWorkOrderList(1, 10);
                    }
                })//获取工单列表列表的数据
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .getRoomOrderList("444444", 1, 10);
                    }
                })//获取房间号
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .getDeviceList(444444, 1, 10);
                    }
                })//获取设备列表信息
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN("111", 1, 2, 2, "abc", "abc");
                    }
                })//修改设备名称
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .deviceRename("232332323223", "开关");
                    }
                })//绑定设备
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .fetchCandidateNodes("11111");
                    }
                })//获取网关的候选节点
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .fetchRuleEngines(1);
                    }
                })//通过房间号获取下属的RuleEngines
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
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
                            actionItem.setRightValue("1");
                            actionItem.setOperator("==");
                            actionItem.setRightValueType(1);
                            actionItem.setTargetDeviceId("GADw3NnUI4Xa54nsr5tYz20000");
                            actionItem.setTargetDeviceType(2);
                            actionItems.add(actionItem);
                        }
                        action.setItems(actionItems);
                        ruleEngineBean.setAction(action);

                        return RequestModel.getInstance()
                                .saveRuleEngine(ruleEngineBean);
                    }
                })//保存RuleEngine
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        RuleEngineBean ruleEngineBean = new RuleEngineBean();
                        ruleEngineBean.setRuleId(123L);
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
                            actionItem.setRightValue("1");
                            actionItem.setOperator("==");
                            actionItem.setRightValueType(1);
                            actionItem.setTargetDeviceId("GADw3NnUI4Xa54nsr5tYz20000");
                            actionItem.setTargetDeviceType(2);
                            actionItems.add(actionItem);
                        }
                        action.setItems(actionItems);
                        ruleEngineBean.setAction(action);

                        return RequestModel.getInstance()
                                .updateRuleEngine(ruleEngineBean);
                    }
                })//更新RuleEngine
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .runRuleEngine(1);
                    }
                })//执行一个场景
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .deleteRuleEngine(1);
                    }
                })//删除一个场景
                .concatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .updateProperty("123", "322", "1");
                    }
                })//更新属性
                .test().assertNoErrors();
    }
}
