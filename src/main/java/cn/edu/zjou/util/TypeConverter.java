package cn.edu.zjou.util;

import cn.edu.zjou.mapper.TypeMapper;
import cn.edu.zjou.po.Type;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TypeConverter {
    private final TypeMapper typeMapper;

    /**
     * key：typeName
     * value:typeId
     */
    private final Map<String, Integer> map = new HashMap<>();

    public TypeConverter(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @PostConstruct
    public void init() {
        List<Type> types = typeMapper.selectList(null);
        types.forEach(type -> {
            map.put(type.getTypeName(), type.getTypeId());
        });
    }

    public int getTypeId(String typeName) {
        return map.get(typeName);
    }
}
