package cn.edu.zjou.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Msg {

    /**
     * 采用BBS数据库所使用的ID值（pid或post_)
     */
    @TableId(type = IdType.NONE)
    private Integer msgId;

    private Integer userId;
    private Integer postId;
    private String content;
    private Date releaseDate;
    private Integer pageNum;

    public Msg(Integer msgId, Integer userId, Integer postId, String content, Date releaseDate, Integer pageNum) {
        this.msgId = msgId;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.releaseDate = releaseDate;
        this.pageNum = pageNum;
    }
}
