package cn.edu.zjou.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author G_xy
 */
@Getter
public enum MsgType {

    /**
     * 普通
     */
    COMMON(0),

    /**
     * 帖子第一条
     */
    MAIN(1),

    /**
     * 官方部门回复
     */
    OFFICIAL(2);

    @EnumValue
    private final int key;


    MsgType(int key) {
        this.key = key;
    }
}
