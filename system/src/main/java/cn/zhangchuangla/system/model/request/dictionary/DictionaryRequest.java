package cn.zhangchuangla.system.model.request.dictionary;

import cn.zhangchuangla.common.base.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 字典表
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DictionaryRequest extends BasePageRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
}
