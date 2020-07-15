package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.data.net.RetrofitHelper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import io.reactivex.functions.Consumer;
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
        PactDslJsonBody body = new PactDslJsonBody()
                .stringType("test")
//                .stringType("valid_date", DateHelper.toString(DATE_TIME))
                .eachLike("animals", 3)
                .stringType("name", "Doggy")
                .stringType("image", "dog")
                .closeObject()
                .closeArray()
                .asBody();

        return builder
                .given("data count is > 0")
                .uponReceiving("a request for json data")
                .path("/provider.json")
                .method("GET")
                .query("valid_date=111222333")
                .willRespondWith()
                .status(200)
                .headers(HEADERS)
                .body(body)
                .toFragment();
    }

    @Test
    @PactVerification("our_provider")
    public void should_process_the_json_payload_from_provider() {
        TestObserver<String> test = RetrofitHelper.getInstance()
                .getApiService().loadProviderJson("111222333").test();
        test.assertNoErrors();
    }
}
