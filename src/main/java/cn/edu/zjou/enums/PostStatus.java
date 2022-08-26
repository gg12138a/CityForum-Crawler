package cn.edu.zjou.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author G_xy
 */

@Getter
public enum PostStatus {
    /**
     * 未设置状态
     */
    UNKNOWN(0),
    /**
     * 已回复
     */
    REPLIED(1),
    /**
     * 已受理
     */
    ACCEPTED(2),
    /**
     * 不受理
     */
    REJECTED(3);

    @EnumValue
    private final int key;

    PostStatus(int key) {
        this.key = key;
    }

    public static class PostStatusBuilder {
        public static PostStatus getPostStatusByName(String statusName) {
            if (statusName == null) {
                return UNKNOWN;
            }

            return switch (statusName) {
                case "已回复" -> REPLIED;
                case "已受理" -> ACCEPTED;
                case "不受理" -> REJECTED;
                default -> UNKNOWN;
            };
        }
    }

}
