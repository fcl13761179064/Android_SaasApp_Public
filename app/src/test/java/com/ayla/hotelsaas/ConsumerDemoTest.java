package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import io.reactivex.observers.TestObserver;

public class ConsumerDemoTest {

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
    public PactProviderRule mockProvider = new PactProviderRule("our_provider", "localhost", 9292, this);

    @Pact(provider = "our_provider", consumer = "our_consumer")
    public PactFragment createFragment(PactDslWithProvider builder) throws UnsupportedEncodingException {
        BaseResult<User> result = new BaseResult<>();
        result.code = "200";
        result.error = "";
        result.data = new User();
        result.data.setUserName("111");
        result.data.setUserName("111");
        result.data.setToken("111");
        result.data.setGroupName("111");

        return builder
                .uponReceiving("a request for json data")
                .path("/login")
                .method("GET")
                .query("username=111&password=222")
                .willRespondWith()
                .status(200)
                .headers(HEADERS)
                .body(new Gson().toJson(result))
                .toFragment();
    }

    @Test
    @PactVerification("our_provider")
    public void should_process_the_json_payload_from_provider() {
        TestObserver<BaseResult<User>> test = RetrofitHelper.getInstance()
                .getApiService().login("111", "222").test();
        test.assertNoErrors();
    }
}
