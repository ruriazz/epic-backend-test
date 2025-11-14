package id.ruriazz.pagination.service;

import id.ruriazz.pagination.client.DummyJsonClient;
import id.ruriazz.pagination.dto.DummyJsonResponse;
import id.ruriazz.pagination.dto.PaginationResponse;
import id.ruriazz.pagination.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final DummyJsonClient dummyJsonClient;

    public PaginationResponse<User> getUsers(int page, int size, String nameFilter) {
        validatePaginationParameters(page, size);

        DummyJsonResponse apiResponse = dummyJsonClient.fetchAllUsers();
        List<User> allUsers = apiResponse.getUsers();

        List<User> filteredUsers = applyNameFilter(allUsers, nameFilter);

        return applyPagination(filteredUsers, page, size);
    }

    private void validatePaginationParameters(int page, int size) {
        if (page <= 0) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Size cannot exceed 100");
        }
    }

    private List<User> applyNameFilter(List<User> users, String nameFilter) {
        if (nameFilter == null || nameFilter.trim().isEmpty()) {
            return users;
        }

        String lowerCaseFilter = nameFilter.toLowerCase().trim();
        log.info("Applying name filter: {}", lowerCaseFilter);

        return users.stream()
                .filter(user -> {
                    String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                    return fullName.contains(lowerCaseFilter) ||
                            user.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                            user.getLastName().toLowerCase().contains(lowerCaseFilter);
                })
                .collect(Collectors.toList());
    }

    private PaginationResponse<User> applyPagination(List<User> users, int page, int size) {
        int totalItems = users.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalItems);

        List<User> paginatedUsers;
        if (startIndex >= totalItems) {
            paginatedUsers = List.of();
        } else {
            paginatedUsers = users.subList(startIndex, endIndex);
        }

        log.info("Returning page {} of {} with {} items (total: {})",
                page, totalPages, paginatedUsers.size(), totalItems);

        return new PaginationResponse<>(page, size, totalItems, totalPages, paginatedUsers);
    }
}