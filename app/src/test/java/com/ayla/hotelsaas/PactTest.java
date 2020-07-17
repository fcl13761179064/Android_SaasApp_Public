package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.ayla.hotelsaas.mvp.model.RequestModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
    public PactProviderRule mockProvider = new PactProviderRule("construction_backend", "localhost", 9292, PactSpecVersion.V2, this);

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
                .given("项目列表的数据")
                .uponReceiving("获取项目的数据内容").path("/work_order")
                .method("POST").willRespondWith().status(200).body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "")
                        .stringType("currentPage", "1")
                        .stringType("pageSize", "10")
                        .object("data", new PactDslJsonArray()
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
                .uponReceiving("获取正确的房间号数据").path("/order_order")
                .method("POST").willRespondWith().status(200).body(new PactDslJsonBody()
                        .numberValue("code", 0)
                        .stringType("error", "")
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
                .toPact();
    }

    /**
     * {
     * "code": 200,
     * "data": [
     * {
     * "id": 1,
     * "name": "电工",
     * "sub": [
     * {
     * "name": "一路开关",
     * "connectMode": 1,
     * "icon": "http://172.31.16.100/product/typeIcon/cz.png"
     * },
     * {
     * "name": "二路开关",
     * "connectMode": 1,
     * "icon": "http://172.31.16.100/product/typeIcon/cz.png"
     * },
     * {
     * "name": "三路开关",
     * "connectMode": 1,
     * "icon": "http://172.31.16.100/product/typeIcon/cz.png"
     * }
     * ]
     * }
     * ],
     * "error": ""
     * }
     */

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

        RetrofitHelper.getInstance()
                .getApiService()
                .getWorkOrders()
                .test().assertNoErrors();
        {//绑定设备
            RequestModel.getInstance()
                    .bindDeviceWithDSN("111", "222", "333", "444").test().assertNoErrors();
        }

        {//解绑设备
            RequestModel.getInstance()
                    .unbindDeviceWithDSN("111", "222", "333").test().assertNoErrors();
        }
    }
}
