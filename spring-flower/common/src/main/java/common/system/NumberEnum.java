package common.system;

import lombok.Getter;

/**
 * 花语支数字典枚举
 * count 为支数（数据库存储值），meaning 为花语展示文案
 */
@Getter
public enum NumberEnum {
    ONE(1, "1支 - 唯一、一见钟情"),
    TWO(2, "2支 - 世界只有你和我"),
    THREE(3, "3支 - I Love You"),
    FOUR(4, "4支 - 誓言、承诺"),
    FIVE(5, "5支 - 无悔"),
    SIX(6, "6支 - 一切顺利"),
    SEVEN(7, "7支 - 喜相逢"),
    EIGHT(8, "8支 - 弥补、歉意"),
    NINE(9, "9支 - 长相守、坚定的爱"),
    TEN(10, "10支 - 十全十美"),

    // ==================== 11~20 支（热恋告白） ====================
    ELEVEN(11, "11支 - 一心一意"),
    TWELVE(12, "12支 - 心心相印"),
    THIRTEEN(13, "13支 - 暗恋"),
    FIFTEEN(15, "15支 - 青春美丽"),
    SIXTEEN(16, "16支 - 一帆风顺"),
    SEVENTEEN(17, "17支 - 好聚好散（也可指陪伴）"),
    EIGHTEEN(18, "18支 - 财源滚滚、真诚"),
    NINETEEN(19, "19支 - 爱到永久"),
    TWENTY(20, "20支 - 两情相悦"),

    // ==================== 21~50 支（深情厚谊） ====================
    TWENTY_ONE(21, "21支 - 真诚的爱"),
    TWENTY_TWO(22, "22支 - 双双对对"),
    TWENTY_FOUR(24, "24支 - 思念"),
    TWENTY_FIVE(25, "25支 - 祝你幸福"),
    TWENTY_SEVEN(27, "27支 - 爱妻"),
    THIRTY(30, "30支 - 请接受我的爱"),
    THIRTY_THREE(33, "33支 - 三生三世"),
    THIRTY_SIX(36, "36支 - 我心属于你"),
    FORTY(40, "40支 - 誓死不渝"),
    FORTY_FOUR(44, "44支 - 至死不渝"),
    FORTY_EIGHT(48, "48支 - 挚爱"),
    FIFTY(50, "50支 - 无悔的爱"),

    // ==================== 51~100 支（隆重仪式感） ====================
    FIFTY_ONE(51, "51支 - 我心中只有你"),
    FIFTY_TWO(52, "52支 - 吾爱"),
    FIFTY_SEVEN(57, "57支 - 吾爱吾妻"),
    SIXTY_SIX(66, "66支 - 事事顺利、真爱不变"),
    SEVENTY_SEVEN(77, "77支 - 喜相逢、相逢自是有缘"),
    EIGHTY_EIGHT(88, "88支 - 用心弥补、歉意"),
    NINETY_NINE(99, "99支 - 天长地久"),
    ONE_HUNDRED(100, "100支 - 百年好合、白头偕老");

    private final int count;
    private final String meaning;

    NumberEnum(int count, String meaning) {
        this.count = count;
        this.meaning = meaning;
    }
}
