package common.system;

import lombok.Getter;

/**
 * 送花对象字典枚举
 * 枚举名即稳定 code，供前端引用；name 为中文展示文案
 */
@Getter
public enum ObjectEnum {
    LOVER_OR_SPOUSE("恋人/爱人"),
    MOTHER_OR_ELDER("母亲/长辈"),
    FRIEND_OR_COLLEAGUE("朋友/同事"),
    FRIEND("朋友"),
    FRIEND_OR_CLASSMATE("朋友/同学"),
    COLLEAGUE_OR_SUPERIOR("同事/上司"),
    CLIENT_OR_BUSINESS("客户/商务"),
    ELDER_OR_TEACHER("长辈/老师"),
    FRIEND_OR_TEACHER("朋友/老师"),
    MOTHER_OR_PARENT("母亲/父母"),
    ELDER_OR_PARENT("长辈/父母"),
    FRIEND_LOVER("朋友,恋人/爱人"),
    PATIENT("病人"),
    PATIENT_ELDER("病人,长辈");

    private final String name;

    ObjectEnum(String name) {
        this.name = name;
    }
}
