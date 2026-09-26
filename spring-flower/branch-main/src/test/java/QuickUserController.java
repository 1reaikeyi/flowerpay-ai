import org.springframework.web.bind.annotation.*;
import entity.Employee;
import framework.aop.Logging;

@CrossOrigin
@RestController
public class QuickUserController {
    @PostMapping("/register")
    public Employee register(@RequestBody Employee user) {
        return user;
    }
    @Logging
    @PostMapping("/login")
    public Employee login(@RequestBody Employee employee) {
        System.out.println(employee);
        return employee;
    }
}
