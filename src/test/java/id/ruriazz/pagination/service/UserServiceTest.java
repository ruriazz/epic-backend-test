package id.ruriazz.pagination.service;

import id.ruriazz.pagination.client.DummyJsonClient;
import id.ruriazz.pagination.dto.DummyJsonResponse;
import id.ruriazz.pagination.dto.PaginationResponse;
import id.ruriazz.pagination.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private DummyJsonClient dummyJsonClient;

    @InjectMocks
    private UserService userService;

    private List<User> mockUsers;
    private DummyJsonResponse mockResponse;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setAge(30);
        user1.setEmail("john.doe@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setAge(25);
        user2.setEmail("jane.smith@example.com");

        User user3 = new User();
        user3.setId(3L);
        user3.setFirstName("Emily");
        user3.setLastName("Johnson");
        user3.setAge(28);
        user3.setEmail("emily.johnson@example.com");

        User user4 = new User();
        user4.setId(4L);
        user4.setFirstName("Michael");
        user4.setLastName("Brown");
        user4.setAge(35);
        user4.setEmail("michael.brown@example.com");

        User user5 = new User();
        user5.setId(5L);
        user5.setFirstName("Sarah");
        user5.setLastName("Davis");
        user5.setAge(32);
        user5.setEmail("sarah.davis@example.com");

        mockUsers = Arrays.asList(user1, user2, user3, user4, user5);

        mockResponse = new DummyJsonResponse();
        mockResponse.setUsers(mockUsers);
        mockResponse.setTotal(5);
        mockResponse.setSkip(0);
        mockResponse.setLimit(5);
    }

    @Test
    void getUsers_WithDefaultParameters_ShouldReturnFirstPage() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(1, 10, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(5, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertEquals(5, result.getData().size());
        verify(dummyJsonClient, times(1)).fetchAllUsers();
    }

    @Test
    void getUsers_WithCustomPageSize_ShouldReturnCorrectPagination() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(1, 2, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalItems());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getData().size());
        assertEquals("John", result.getData().get(0).getFirstName());
        assertEquals("Jane", result.getData().get(1).getFirstName());
    }

    @Test
    void getUsers_WithSecondPage_ShouldReturnCorrectUsers() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(2, 2, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalItems());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getData().size());
        assertEquals("Emily", result.getData().get(0).getFirstName());
        assertEquals("Michael", result.getData().get(1).getFirstName());
    }

    @Test
    void getUsers_WithPageBeyondRange_ShouldReturnEmptyData() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(10, 2, null);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalItems());
        assertEquals(3, result.getTotalPages());
        assertEquals(0, result.getData().size());
    }

    @Test
    void getUsers_WithNameFilter_ShouldReturnFilteredUsers() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(1, 10, "John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getData().size());
        assertTrue(result.getData().stream().anyMatch(user -> "John".equals(user.getFirstName())));
        assertTrue(result.getData().stream().anyMatch(user -> "Johnson".equals(user.getLastName())));
    }

    @Test
    void getUsers_WithCaseInsensitiveNameFilter_ShouldReturnFilteredUsers() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(1, 10, "emily");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        assertEquals("Emily", result.getData().get(0).getFirstName());
    }

    @Test
    void getUsers_WithEmptyNameFilter_ShouldReturnAllUsers() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result1 = userService.getUsers(1, 10, "");
        PaginationResponse<User> result2 = userService.getUsers(1, 10, "   ");

        // Then
        assertEquals(5, result1.getTotalItems());
        assertEquals(5, result2.getTotalItems());
    }

    @Test
    void getUsers_WithInvalidPage_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsers(0, 10, null));
        assertEquals("Page must be greater than 0", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsers(-1, 10, null));
        assertEquals("Page must be greater than 0", exception2.getMessage());
    }

    @Test
    void getUsers_WithInvalidSize_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsers(1, 0, null));
        assertEquals("Size must be greater than 0", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsers(1, -1, null));
        assertEquals("Size must be greater than 0", exception2.getMessage());
    }

    @Test
    void getUsers_WithSizeExceedsLimit_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsers(1, 101, null));
        assertEquals("Size cannot exceed 100", exception.getMessage());
    }

    @Test
    void getUsers_WithExactSizeLimit_ShouldWork() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(1, 100, null);

        // Then
        assertNotNull(result);
        assertEquals(100, result.getSize());
        assertEquals(5, result.getData().size());
    }

    @Test
    void getUsers_WithPartialLastPage_ShouldReturnRemainingUsers() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When
        PaginationResponse<User> result = userService.getUsers(3, 2, null);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalItems());
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getData().size());
        assertEquals("Sarah", result.getData().get(0).getFirstName());
    }

    @Test
    void getUsers_WithFilterAndPagination_ShouldWork() {
        // Given
        when(dummyJsonClient.fetchAllUsers()).thenReturn(mockResponse);

        // When - Filter for names containing "a" and paginate
        PaginationResponse<User> result = userService.getUsers(1, 2, "a");

        // Then - Should find Jane, Sarah, Michael (3 users with 'a')
        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(3, result.getTotalItems());
        assertEquals(2, result.getTotalPages());
        assertEquals(2, result.getData().size());
    }
}