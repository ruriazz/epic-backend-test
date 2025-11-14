package id.ruriazz.pagination.client;

import id.ruriazz.pagination.dto.DummyJsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class DummyJsonClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public DummyJsonClient(@Value("${external.api.dummyjson.url:https://dummyjson.com}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    @Cacheable(value = "users", unless = "#result == null")
    public DummyJsonResponse fetchAllUsers() {
        String url = baseUrl + "/users?limit=250";

        try {
            log.info("Fetching users from external API: {}", url);
            DummyJsonResponse response = restTemplate.getForObject(url, DummyJsonResponse.class);

            if (response == null || response.getUsers() == null) {
                log.error("Received null response from external API");
                throw new RuntimeException("Invalid response from external API");
            }

            log.info("Successfully fetched {} users from external API", response.getUsers().size());
            return response;

        } catch (ResourceAccessException e) {
            log.error("Network error while calling external API: {}", e.getMessage());
            throw new RuntimeException("External API is unreachable", e);
        } catch (RestClientException e) {
            log.error("Error calling external API: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch data from external API", e);
        } catch (RuntimeException e) {
            if ("Invalid response from external API".equals(e.getMessage())) {
                throw e;
            }
            log.error("Unexpected error while calling external API: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred", e);
        } catch (Exception e) {
            log.error("Unexpected error while calling external API: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }
}
