package cn.edu.zjou.crawler;

import cn.edu.zjou.mapper.TypeMapper;
import cn.edu.zjou.po.Type;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagCrawler {

    private Logger logger = LoggerFactory.getLogger(TagCrawler.class);

    private TypeMapper typeMapper;
    @Autowired
    public TagCrawler(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public void updateTypes(String url) {
        JXDocument jxd = JXDocument.createByUrl(url);
        JXNode ul = jxd.selNOne("""
                //*[@id="thread_types"]""");

        List<JXNode> aOfTypes = ul.sel(".//li[not(@id)]//a");

        List<Type> types = new ArrayList<>();
        for (var node : aOfTypes) {
            types.add(new Type(node.asElement().ownText()));
        }

        typeMapper.saveAllWithUniqueTypeName(types);
    }
}
