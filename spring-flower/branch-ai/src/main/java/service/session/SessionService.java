package service.session;

import model.entity.Session;
import start.vo.MessageVO;
import start.vo.SessionTitleVO;
import start.vo.SessionVO;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;
import java.util.Map;

public interface SessionService extends IService<Session> {
    /**
     * 创建会话session
     * @return 会话信息
     */
    SessionVO createSession();

    /**
     * 查询chat
     * @param sessionId
     * @return
     */
    List<MessageVO> queryBySessionId(String sessionId);
    /**
     * 查询历史会话列表
     */
    Map<String, List<SessionTitleVO>> queryHistorySession();
    /**
     * 更新历史会话标题
     *
     * @param sessionId 会话id
     * @param title     标题
     */
    void updateSessionTitle(String sessionId, String title);
    /**
     * 删除历史会话
     *
     * @param sessionId 会话ID
     */
    void deleteHistorySession(String sessionId);

}
