package cn.edu.zjou.util;

import cn.edu.zjou.mapper.DeptMapper;
import cn.edu.zjou.po.Dept;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeptConverter {

    private final DeptMapper deptMapper;

    /**
     * key：deptName
     * value:deptId
     */
    private final Map<String, Integer> map = new HashMap<>();

    @PostConstruct
    public void init() {
        List<Dept> depts = deptMapper.selectList(null);
        depts.forEach(dept -> {
            map.put(dept.getDeptName(), dept.getDeptId());
        });
    }

    public DeptConverter(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    public int getDeptId(String deptName){
        return map.get(deptName);
    }
}
