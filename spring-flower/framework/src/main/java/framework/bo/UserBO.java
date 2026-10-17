package framework.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBO {

    private Long id;
    private String username;
    private String password;
    private Long status;
}
