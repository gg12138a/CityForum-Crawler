package cn.edu.zjou.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Post {
    @TableId
    private Integer postId;
}
