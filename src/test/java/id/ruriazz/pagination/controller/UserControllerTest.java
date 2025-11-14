package id.ruriazz.pagination.controller;

import id.ruriazz.pagination.dto.PaginationResponse;
import id.ruriazz.pagination.model.User;
import id.ruriazz.pagination.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new id.ruriazz.pagination.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUsers_WithDefaultParameters_ShouldReturnDefaultPagination() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = createMockPaginationResponse();
        when(userService.getUsers(1, 10, null)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalItems", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].firstName", is("John")))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].firstName", is("Jane")));

        verify(userService, times(1)).getUsers(1, 10, null);
    }

    @Test
    void getUsers_WithCustomParameters_ShouldReturnCustomPagination() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = createMockPaginationResponse();
        when(userService.getUsers(2, 5, "John")).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "2")
                .param("size", "5")
                .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalItems", is(2)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(userService, times(1)).getUsers(2, 5, "John");
    }

    @Test
    void getUsers_WithNameFilter_ShouldReturnFilteredResults() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        user.setFirstName("Emily");
        user.setLastName("Johnson");

        PaginationResponse<User> mockResponse = new PaginationResponse<>(
                1, 10, 1, Collections.singletonList(user));

        when(userService.getUsers(1, 10, "Emily")).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("name", "Emily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems", is(1)))
                .andExpect(jsonPath("$.data[0].firstName", is("Emily")));

        verify(userService, times(1)).getUsers(1, 10, "Emily");
    }

    @Test
    void getUsers_WithInvalidPageParameter_ShouldReturnBadRequest() throws Exception {
        // Given
        when(userService.getUsers(eq(-1), eq(10), isNull()))
                .thenThrow(new IllegalArgumentException("Page must be greater than 0"));

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "-1"))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getUsers(-1, 10, null);
    }

    @Test
    void getUsers_WithInvalidSizeParameter_ShouldReturnBadRequest() throws Exception {
        // Given
        when(userService.getUsers(eq(1), eq(0), isNull()))
                .thenThrow(new IllegalArgumentException("Size must be greater than 0"));

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getUsers(1, 0, null);
    }

    @Test
    void getUsers_WithNonIntegerPageParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "abc"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUsers(anyInt(), anyInt(), anyString());
    }

    @Test
    void getUsers_WithNonIntegerSizeParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users")
                .param("size", "xyz"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUsers(anyInt(), anyInt(), anyString());
    }

    @Test
    void getUsers_WithEmptyNameParameter_ShouldPassEmptyString() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = createMockPaginationResponse();
        when(userService.getUsers(1, 10, "")).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("name", ""))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsers(1, 10, "");
    }

    @Test
    void getUsers_WithSpacesInNameParameter_ShouldPassSpaces() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = createMockPaginationResponse();
        when(userService.getUsers(1, 10, "   ")).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("name", "   "))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsers(1, 10, "   ");
    }

    @Test
    void getUsers_WithLargePageNumber_ShouldReturnEmptyResults() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = new PaginationResponse<>(
                100, 10, 50, Collections.emptyList());
        when(userService.getUsers(100, 10, null)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(100)))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(userService, times(1)).getUsers(100, 10, null);
    }

    @Test
    void getUsers_WithMaximumSizeParameter_ShouldWork() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = createMockPaginationResponse();
        when(userService.getUsers(1, 100, null)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("size", "100"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsers(1, 100, null);
    }

    @Test
    void getUsers_WithServiceThrowingRuntimeException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.getUsers(eq(1), eq(10), isNull()))
                .thenThrow(new RuntimeException("External API is unreachable"));

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isServiceUnavailable());

        verify(userService, times(1)).getUsers(1, 10, null);
    }

    @Test
    void getUsers_WithComplexNameFilter_ShouldWork() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = createMockPaginationResponse();
        when(userService.getUsers(1, 10, "John Doe")).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("name", "John Doe"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsers(1, 10, "John Doe");
    }

    @Test
    void getUsers_WithSpecialCharactersInName_ShouldWork() throws Exception {
        // Given
        PaginationResponse<User> mockResponse = createMockPaginationResponse();
        when(userService.getUsers(1, 10, "O'Connor")).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("name", "O'Connor"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsers(1, 10, "O'Connor");
    }

    private PaginationResponse<User> createMockPaginationResponse() {
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

        return new PaginationResponse<>(1, 10, 2, Arrays.asList(user1, user2));
    }
}