package start.paysubmit;

import com.branch.model.dto.UserShoppingDTO;
import com.branch.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/user")
public class PaySubmitController {

    @Autowired
    private PaySubmitService paySubmitService;

    @PostMapping("/submit1")
    @PreAuthorize("hasRole('USER')")
    public Result submit1(UserShoppingDTO userShoppingDTO){
        paySubmitService.submit1(userShoppingDTO);
        return Result.success(userShoppingDTO);
    }

    @PostMapping("/submit2")
    @PreAuthorize("hasRole('USER')")
    public Result submit2(UserShoppingDTO userShoppingDTO, LocalDate localDate){
        paySubmitService.submit2(userShoppingDTO, localDate);
        return Result.success(userShoppingDTO);
    }

}
