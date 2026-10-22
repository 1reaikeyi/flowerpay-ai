package common.system;

import lombok.Getter;

/**
 * 送花场景/用途字典枚举
 * 枚举名即稳定 code，供前端引用；name 为中文展示文案
 */
@Getter
public enum OptionEnum {
    CONFESSION("表白"),
    ANNIVERSARY("纪念日"),
    VALENTINES_DAY("情人节"),
    PROPOSAL("求婚"),
    DAILY("日常"),
    BIRTHDAY("生日"),
    APOLOGY("道歉"),
    THANKS("感谢"),
    BLESSING("祝福"),
    VISIT_PATIENT("探病"),
    WEDDING("婚礼"),
    HOUSEWARMING("乔迁"),
    GRADUATION("毕业"),
    FRIENDSHIP("友谊"),
    BUSINESS("商务"),
    PROMOTION("升职"),
    DATING("约会"),
    ENCOURAGEMENT("鼓励"),
    PARTY("聚会"),
    MOTHERS_DAY("母亲节"),
    GRATITUDE("感恩"),
    TEACHERS_DAY("教师节"),
    OPENING("开业"),
    MISSING("思念"),
    LONGEVITY("祝寿");

    private final String name;

    OptionEnum(String name) {
        this.name = name;
    }
}
