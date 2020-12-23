package me.redoak.edean.pricewatch.logic;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

/**
 * Service for retrieving web content from a URL.
 */
@Slf4j
@Service
public class WebClient {

    @Getter
    @Setter
    private RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));

    /**
     * {@link PostConstruct} for adding an interceptor to the {@link RestTemplate}, in order to set the User-Agent to something valid for Amazon.
     */
    @PostConstruct
    public void postConstruct() {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", UUID.randomUUID().toString() + " I'm surely a legit user agent, lol");
            return execution.execute(request, body);
        });
    }

    /**
     *
     * @param url {@link URL} of the web page to be read.
     * @return Web content from given {@link URL}.
     */
    public String getContent(URL url) {
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url.toURI(), String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not create URI for given URL: " + url + ".", e);
        }
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (!statusCode.is2xxSuccessful()) {
            throw new RuntimeException("Failed to retrieve Data from given URL: " + url + ". Status code: " + statusCode.value() + ", " + statusCode.name());
        }
        return responseEntity.getBody();
    }

}
