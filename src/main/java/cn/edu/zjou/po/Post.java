package cn.edu.zjou.po;

import cn.edu.zjou.enums.PostStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author G_xy
 */
@Data
@NoArgsConstructor
public class Post {
    /**
     * 采用BBS数据库所使用的ID值
     */
    @TableId(type = IdType.NONE)
    private Integer postId;

    private Integer typeId;
    private Integer deptId;
    private String title;
    private Integer publishUserId;
    private Date publishDate;
    public Integer replyCount;
    private Integer checkCount;
    private PostStatus postStatus;

    public Post(Integer postId, Integer typeId, Integer deptId, String title, Integer publishUserId, Date publishDate, Integer replyCount, Integer checkCount, PostStatus postStatus) {
        this.postId = postId;
        this.typeId = typeId;
        this.deptId = deptId;
        this.title = title;
        this.publishUserId = publishUserId;
        this.publishDate = publishDate;
        this.replyCount = replyCount;
        this.checkCount = checkCount;
        this.postStatus = postStatus;
    }
}
