package cn.edu.zjou.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Type {

    @TableId(type = IdType.AUTO)
    private Integer typeId;

    private String typeName;

    public Type(String typeName) {
        this.typeName = typeName;
    }

    public Type(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }
}
