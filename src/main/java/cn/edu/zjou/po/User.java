package cn.edu.zjou.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    @TableId(type = IdType.INPUT)
    private Integer userId;

    private String username;

    public User(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public User(Integer userId) {
        this.userId = userId;
    }
}
