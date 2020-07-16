package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RetrofitHelper;

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
    public PactProviderRule mockProvider = new PactProviderRule("construction_backend", "localhost", 9292, this);

    @Pact(provider = "construction_backend", consumer = "construction_app")
    public RequestResponsePact createFragment(PactDslWithProvider builder) throws UnsupportedEncodingException {
        return builder
                //正常用户登录
                .given("正确的用户密码")
                .uponReceiving("用户实例，当前简单，只包含token")
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
                .given("错误的用户名或密码")
                .uponReceiving("code = 1001，表示：用户名或密码错误")
                .path("/login")
                .method("POST")
                .body("username=111&password=333", "application/x-www-form-urlencoded")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody().numberValue("code", 1001)
                        .stringType("error", "用户名或密码错误"))
                //获取产品配网二级菜单列表
                .given("获取产品配网二级菜单列表")
                .uponReceiving("配网支持的产品列表，二级菜单")
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
                                        .stringType("name", "电工")
                                        .numberType("cuid", 1)
                                        .stringType("icon", "http://172.31.16.100/product/typeIcon/cz.png")
                                        .closeObject()
                                )
                                .closeObject()
                        ))
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
    }
}
