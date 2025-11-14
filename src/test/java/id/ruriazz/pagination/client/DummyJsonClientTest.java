package id.ruriazz.pagination.client;

import id.ruriazz.pagination.dto.DummyJsonResponse;
import id.ruriazz.pagination.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DummyJsonClientTest {

    @Mock
    private RestTemplate restTemplate;

    private DummyJsonClient dummyJsonClient;

    @BeforeEach
    void setUp() {
        dummyJsonClient = new DummyJsonClient("https://dummyjson.com");
        ReflectionTestUtils.setField(dummyJsonClient, "restTemplate", restTemplate);
    }

    @Test
    void fetchAllUsers_WithValidResponse_ShouldReturnUsers() {
        // Given
        DummyJsonResponse mockResponse = createMockResponse();
        when(restTemplate.getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class))
                .thenReturn(mockResponse);

        // When
        DummyJsonResponse result = dummyJsonClient.fetchAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(0, result.getSkip());
        assertEquals(2, result.getLimit());
        assertNotNull(result.getUsers());
        assertEquals(2, result.getUsers().size());

        User user = result.getUsers().get(0);
        assertEquals(1L, user.getId());
        assertEquals("Emily", user.getFirstName());
        assertEquals("Johnson", user.getLastName());
        assertEquals("emily.johnson@example.com", user.getEmail());

        verify(restTemplate, times(1)).getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class);
    }

    @Test
    void fetchAllUsers_WithNetworkError_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dummyJsonClient.fetchAllUsers());

        assertTrue(exception.getMessage().contains("External API is unreachable"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResourceAccessException);
    }

    @Test
    void fetchAllUsers_WithServerError_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class))
                .thenThrow(new RestClientException("500 Internal Server Error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dummyJsonClient.fetchAllUsers());

        assertTrue(exception.getMessage().contains("Failed to fetch data from external API"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof RestClientException);
    }

    @Test
    void fetchAllUsers_WithNullResponse_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class))
                .thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dummyJsonClient.fetchAllUsers());

        assertEquals("Invalid response from external API", exception.getMessage());
    }

    @Test
    void fetchAllUsers_WithNullUsersArray_ShouldThrowRuntimeException() {
        // Given
        DummyJsonResponse mockResponse = new DummyJsonResponse();
        mockResponse.setUsers(null);
        mockResponse.setTotal(0);

        when(restTemplate.getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class))
                .thenReturn(mockResponse);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dummyJsonClient.fetchAllUsers());

        assertEquals("Invalid response from external API", exception.getMessage());
    }

    @Test
    void fetchAllUsers_WithEmptyUsersArray_ShouldReturnEmptyResponse() {
        // Given
        DummyJsonResponse mockResponse = new DummyJsonResponse();
        mockResponse.setUsers(Collections.emptyList());
        mockResponse.setTotal(0);
        mockResponse.setSkip(0);
        mockResponse.setLimit(30);

        when(restTemplate.getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class))
                .thenReturn(mockResponse);

        // When
        DummyJsonResponse result = dummyJsonClient.fetchAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertNotNull(result.getUsers());
        assertEquals(0, result.getUsers().size());
    }

    @Test
    void fetchAllUsers_WithUnexpectedException_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dummyJsonClient.fetchAllUsers());

        assertEquals("Unexpected error occurred", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    @Test
    void fetchAllUsers_WithGenericException_ShouldThrowRuntimeException() throws Exception {
        // Given - Use doAnswer to throw a checked exception
        doAnswer(invocation -> {
            throw new Exception("Generic checked exception");
        }).when(restTemplate).getForObject("https://dummyjson.com/users?limit=100", DummyJsonResponse.class);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dummyJsonClient.fetchAllUsers());

        assertEquals("Unexpected error occurred", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof Exception);
    }

    @Test
    void constructor_ShouldSetCorrectBaseUrl() {
        // Given & When
        DummyJsonClient customClient = new DummyJsonClient("https://custom-api.com");

        // Then
        assertNotNull(customClient);
    }

    private DummyJsonResponse createMockResponse() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Emily");
        user1.setLastName("Johnson");
        user1.setAge(28);
        user1.setEmail("emily.johnson@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("John");
        user2.setLastName("Doe");
        user2.setAge(30);
        user2.setEmail("john.doe@example.com");

        DummyJsonResponse response = new DummyJsonResponse();
        response.setUsers(Arrays.asList(user1, user2));
        response.setTotal(2);
        response.setSkip(0);
        response.setLimit(2);

        return response;
    }
}