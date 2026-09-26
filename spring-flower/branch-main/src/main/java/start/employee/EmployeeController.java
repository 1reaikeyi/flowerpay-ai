package start.employee;




import framework.aop.oparation.OperationEnum;
import framework.aop.OperationLogging;
import common.result.Result;
import lombok.extern.slf4j.Slf4j;
import dto.EmployeeDTO;
import dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.EmployeeService;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/register")
    public Result register(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.register(employeeDTO);
        return Result.success("register");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String token = employeeService.login(loginDTO);
        return Result.success(token);
    }

    @OperationLogging(operation = OperationEnum.CREATE)
    @PostMapping("/logout")
    public Result logout() {
        employeeService.logout();
        return Result.success("logout");
    }

    @OperationLogging(operation = OperationEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateByObject(employeeDTO);
        return Result.success(employeeDTO.getId());
    }

}