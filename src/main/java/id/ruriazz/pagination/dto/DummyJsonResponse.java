package id.ruriazz.pagination.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import id.ruriazz.pagination.model.User;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DummyJsonResponse {
    private List<User> users;
    private int total;
    private int skip;
    private int limit;
}