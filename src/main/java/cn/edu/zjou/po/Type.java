package cn.edu.zjou.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Type {

    @TableId(type = IdType.ASSIGN_ID)
    private Integer typeId;

    private String typeName;

    public Type(String typeName) {
        this.typeName=typeName;
    }
}
