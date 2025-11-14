package id.ruriazz.pagination.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private List<T> data;

    public PaginationResponse(int page, int size, long totalItems, List<T> data) {
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.data = data;
        this.totalPages = (int) Math.ceil((double) totalItems / size);
    }
}