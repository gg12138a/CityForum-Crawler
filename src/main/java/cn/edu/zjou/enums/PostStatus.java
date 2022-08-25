package cn.edu.zjou.enums;

import lombok.Getter;

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

    private final int key;

    PostStatus(int key) {
        this.key = key;
    }
}
