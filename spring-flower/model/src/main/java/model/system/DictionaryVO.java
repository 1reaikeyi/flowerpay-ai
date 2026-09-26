package model.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 前端字典返回对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字典项稳定 code，供前端引用
     */
    private String code;

    /**
     * 字典项中文展示文案
     */
    private String name;
}
