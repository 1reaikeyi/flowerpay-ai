package framework.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserBO {
    private Long id;
    private String username;
    private String password;
    private List<String> permission;
    private String role;
    private Long status;

    public UserBO(Long id, String username, String password, List<String> permission, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.permission = permission;
        this.role = role;
    }

    public UserBO(Long id, String username, String password, List<String> permission, String role, Long status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.permission = permission;
        this.role = role;
        this.status = status;
    }
}
