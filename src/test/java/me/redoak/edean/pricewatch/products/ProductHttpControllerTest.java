package me.redoak.edean.pricewatch.products;

import me.redoak.edean.pricewatch.logic.UrlTransformer;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import me.redoak.edean.pricewatch.util.TestUrlTransformer;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductHttpControllerTest {

    private static final String TEST_PASSWORD = "hehe";
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TrackedProductRepository repo;

    @Autowired
    private TrackedProductService trackedProductService;

    @Autowired
    private SubscriberService subscriberService;
    private Subscriber subscriber = Subscriber.builder().name("boy").build();

    @BeforeAll
    public void setUp() {
        log.info("registering test subscriber");
        subscriber = subscriberService.register(Subscriber.builder().name("boy").build(), TEST_PASSWORD);
    }

    @AfterAll
    public void tearDown() {
        log.info("unregistering test subscriber");
        subscriberService.unregister(subscriber);
    }

    @AfterEach
    public void tearDownEach() {
        log.info("removing all TrackedProducts");
        repo.deleteAll();
    }

    @Transactional
    @Test
    public void testValidAmazon() throws Exception {
        ResultActions actions = mvc.perform(post("/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .header("PricewatchUser", subscriber.getName())
                .header("PricewatchPassword",TEST_PASSWORD)
                .content("{\"url\":\"amazon.de/dp/B01CFWCN14\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(endsWith("\",\"name\":null,\"price\":null,\"oldPrice\":null,\"url\":\"https://amazon.de/dp/B01CFWCN14\",\"tracked\":true,\"shop\":\"AMAZON\",\"updatedAt\":null}")));
        // the id should be the same in both responses, since I send the same product (with differing url).
        String id = new JSONObject(actions.andReturn().getResponse().getContentAsString()).get("id").toString();
        mvc.perform(post("/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .header("PricewatchUser", subscriber.getName())
                .header("PricewatchPassword",TEST_PASSWORD)
                .content("{\"url\":\"www.smile.amazon.de/dp/B01CFWCN14\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":\"" + id + "\",\"name\":null,\"price\":null,\"oldPrice\":null,\"url\":\"https://amazon.de/dp/B01CFWCN14\",\"tracked\":true,\"shop\":\"AMAZON\",\"updatedAt\":null}"));
    }

    @Transactional
    @Test
    public void testMissingSaver() throws Exception {
        List<UrlTransformer> oldTransformers = trackedProductService.getTransformers();
        try {
            trackedProductService.setTransformers(Arrays.asList(new TestUrlTransformer()));
            mvc.perform(post("/subscribe")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("PricewatchUser", subscriber.getName())
                    .header("PricewatchPassword",TEST_PASSWORD)
                    .content("{\"url\":\"amazon.de/dp/B01CFWCN14\"}"));
            fail();
        } catch(NestedServletException e) {
            assertThat(e.getMessage()).isEqualTo("Request processing failed; nested exception is java.lang.RuntimeException: No saver found for shop: MINDFACTORY");
        } finally {
            trackedProductService.setTransformers(oldTransformers);
        }
    }
}
