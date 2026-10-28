package com.branch.service;



import com.baomidou.mybatisplus.extension.service.IService;

import com.branch.domain.dto.BlogDTO;
import com.branch.domain.dto.BlogPageDTO;
import com.branch.domain.entity.Blog;
import com.branch.domain.vo.BlogVO;
import com.branch.result.PageResult;
import com.branch.result.ScrollResult;

import java.util.Set;

public interface BlogService extends IService<Blog> {

    /**
     * 发布博客：保存博客并推送到所有粉丝的收件箱
     *
     * @return 博客ID
     */
    BlogDTO postBlog(BlogDTO blogDTO);

    /**
     * 点赞/取消点赞
     *
     * @param id 博客ID
     * @return 操作结果描述
     */
    String toggleLike(Long id);

    /**
     * 当前用户是否已点赞该博客
     */
    Boolean isLiked(Long id);

    /**
     * 博客的全部点赞用户ID列表
     */
    Set<String> likedAll(Long id);

    /**
     * 关注流收件箱：滚动分页查询已关注用户发布的博客
     */
    ScrollResult followScroll(Long max, Long offset);

    PageResult<BlogVO> readAll(BlogPageDTO blogPageDTO);

}
