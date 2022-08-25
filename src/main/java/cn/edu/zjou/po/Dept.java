package cn.edu.zjou.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Dept {
    @TableId(type = IdType.AUTO)
    private Integer deptId;
    private String deptName;

    public Dept(String deptName) {
        this.deptName = deptName;
    }

    public Dept(int deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }
}
