package id.ruriazz.pagination.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "external.api.dummyjson.url=https://dummyjson.com",
        "logging.level.id.ruriazz.pagination=DEBUG"
})
@ActiveProfiles("test")
class PaginationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetUsers_WithDefaultParameters_ShouldReturnSuccess() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"page\":1"));
        assertTrue(response.getBody().contains("\"size\":10"));
        assertTrue(response.getBody().contains("\"totalItems\""));
        assertTrue(response.getBody().contains("\"data\""));
    }

    @Test
    void testGetUsers_WithCustomPagination_ShouldReturnSuccess() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users?page=2&size=5", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"page\":2"));
        assertTrue(response.getBody().contains("\"size\":5"));
    }

    @Test
    void testGetUsers_WithNameFilter_ShouldReturnFilteredResults() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users?name=Emily", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"data\""));
    }

    @Test
    void testGetUsers_WithInvalidPage_ShouldReturnBadRequest() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users?page=-1", String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"status\":400"));
        assertTrue(response.getBody().contains("Invalid pagination parameter"));
    }

    @Test
    void testGetUsers_WithInvalidSize_ShouldReturnBadRequest() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users?size=0", String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"status\":400"));
        assertTrue(response.getBody().contains("Invalid pagination parameter"));
    }

    @Test
    void testGetUsers_WithInvalidParameterType_ShouldReturnBadRequest() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users?page=abc", String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"status\":400"));
        assertTrue(response.getBody().contains("Invalid parameter type"));
    }

    @Test
    void testApplicationContext_ShouldLoadSuccessfully() {
        assertTrue(port > 0);
    }
}