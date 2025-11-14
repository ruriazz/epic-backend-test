package id.ruriazz.pagination.controller;

import id.ruriazz.pagination.dto.PaginationResponse;
import id.ruriazz.pagination.model.User;
import id.ruriazz.pagination.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user data with pagination")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    @Operation(summary = "Get paginated users", description = "Retrieve users with pagination and optional name filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "External API unreachable or internal server error")
    })
    public ResponseEntity<PaginationResponse<User>> getUsers(
            @Parameter(description = "Page number (starts from 1)", example = "1") @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Filter users by name (first name or last name)", example = "John") @RequestParam(required = false) String name) {
        log.info("GET /api/users - page: {}, size: {}, name: {}", page, size, name);

        PaginationResponse<User> response = userService.getUsers(page, size, name);

        log.info("Returning {} users for page {} of {}",
                response.getData().size(), response.getPage(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
