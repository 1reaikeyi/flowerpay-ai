package com.branch.constant;

/**
 * redisKey前缀
 */
public interface RedisPrefixConstant {
    String ADMIN_AUTH_PREFIX = "flower:admin:";
    String USER_AUTH_PREFIX = "flower:user:";


    String CATEGORY_TYPE_PREFIX = "flower:category:type:";
    String FLOWER_PREFIX = "flower:";
    String FLOWERDETAIL_PREFIX = "flowerDetail:";
    String FESTIVAL_PREFIX = "festival:";
    String FESTIVALDETAIL_PREFIX = "festivalDetail:";


    String SHOPPING_CART_PREFIX = "shopping:cart:";


    String ARTICLE_PREFIX = "blog:article:";
    String BLOG_LIKED_PREFIX = "blog:liked:";
    String BLOG_FOLLOW_PREFIX = "blog:follow:";
    String BLOG_VIEW_PREFIX = "blog:view:";
    String BLOG_WATCH_PREFIX = "blog:watch:";

    String SIGN_DAY = "sign:day:";

    String SHOP_TYPE = "shop:type:";

    String VOUCHER_STOCK_PREFIX = "voucher:second:stock:";
    String VOUCHER_ORDER_PREFIX = "voucher:second:order:";

}
