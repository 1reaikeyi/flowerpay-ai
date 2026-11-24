package start.paysubmit;

import com.branch.model.dto.UserShoppingDTO;
import com.branch.model.entity.Festival;
import com.branch.model.entity.Flower;
import com.branch.model.entity.FlowerOrder;
import com.branch.model.entity.FlowerOrderDetail;
import com.branch.model.enums.DeliveryStatusEnum;
import com.branch.model.vo.UserAddressVO;
import com.branch.framework.bo.SecurityContextParam;
import com.branch.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PaySubmitService {

    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private FlowerOrderService flowerOrderService;
    @Autowired
    private FlowerOrderDetailService flowerOrderDetailService;
    @Autowired
    private FlowerService flowerService;
    @Autowired
    private FestivalService festivalService;

    public void submit1(UserShoppingDTO userShoppingDTO) {
        UserAddressVO userAddressVO = userAddressService.readDefaultAddress();

        FlowerOrder flowerOrder = FlowerOrder.builder()
                .addressId(userAddressVO.getId())
                .userId(SecurityContextParam.getCurrentUserId())
                .deliveryType(DeliveryStatusEnum.NOW)
                .deliveryDate(LocalDate.now())
                .remark("尽快送达")
                .build();
        flowerOrderService.save(flowerOrder);

        if (userShoppingDTO.getFlowerId() != null){
            Flower flower = flowerService.getById(userShoppingDTO.getFlowerId());
            FlowerOrderDetail flowerOrderDetail = FlowerOrderDetail.builder()
                    .orderId(flowerOrder.getId()).flowerId(flower.getId())
                    .amount(flower.getPrice()).image(flower.getImage()).name(flower.getName())
                    .number(userShoppingDTO.getNumber())
                    .wrapFee(2L)
                    .build();
            flowerOrderDetailService.save(flowerOrderDetail);
        }
        if (userShoppingDTO.getFestivalId() != null){
            Festival festival = festivalService.getById(userShoppingDTO.getFestivalId());
            FlowerOrderDetail flowerOrderDetail = FlowerOrderDetail.builder()
                    .orderId(flowerOrder.getId()).festivalId(festival.getId())
                    .amount(festival.getPrice()).image(festival.getImage()).name(festival.getName())
                    .number(userShoppingDTO.getNumber())
                    .wrapFee(3L)
                    .build();
            flowerOrderDetailService.save(flowerOrderDetail);
        }
    }
    public void submit2(UserShoppingDTO userShoppingDTO, LocalDate localDate) {
        UserAddressVO userAddressVO = userAddressService.readDefaultAddress();

        FlowerOrder flowerOrder = FlowerOrder.builder()
                .addressId(userAddressVO.getId())
                .userId(SecurityContextParam.getCurrentUserId())
                .deliveryType(DeliveryStatusEnum.BOOK_TIME)
                .deliveryDate(localDate)
                .build();
        flowerOrderService.save(flowerOrder);

        if (userShoppingDTO.getFlowerId() != null){
            Flower flower = flowerService.getById(userShoppingDTO.getFlowerId());
            FlowerOrderDetail flowerOrderDetail = FlowerOrderDetail.builder()
                    .orderId(flowerOrder.getId()).flowerId(flower.getId())
                    .amount(flower.getPrice()).image(flower.getImage()).name(flower.getName())
                    .number(userShoppingDTO.getNumber())
                    .wrapFee(2L)
                    .build();
            flowerOrderDetailService.save(flowerOrderDetail);
        }
        if (userShoppingDTO.getFestivalId() != null){
            Festival festival = festivalService.getById(userShoppingDTO.getFestivalId());
            FlowerOrderDetail flowerOrderDetail = FlowerOrderDetail.builder()
                    .orderId(flowerOrder.getId()).festivalId(festival.getId())
                    .amount(festival.getPrice()).image(festival.getImage()).name(festival.getName())
                    .number(userShoppingDTO.getNumber())
                    .wrapFee(3L)
                    .build();
            flowerOrderDetailService.save(flowerOrderDetail);
        }
    }
}
