package service;

import com.baomidou.mybatisplus.extension.service.IService;
import dto.FestivalDetailDTO;
import entity.FestivalDetail;
import vo.FestivalDetailVO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * 芊店的festival关系 Service（对应 festival_detail 表）
 */

public interface FestivalDetailService extends IService<FestivalDetail> {

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    FestivalDetailDTO create(FestivalDetailDTO festivalDetailDTO);

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_EMP') or hasAuthority('ROLE_ADMIN')")
    FestivalDetailVO readCache(Long id);

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    void updateCache(FestivalDetailDTO festivalDetailDTO);

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    void deleteCache(List<Long> ids);
}
