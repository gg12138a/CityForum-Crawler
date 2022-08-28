package cn.edu.zjou.crawler;

import cn.edu.zjou.config.CrawlerConfig;
import cn.edu.zjou.mapper.DeptMapper;
import cn.edu.zjou.mapper.TypeMapper;
import cn.edu.zjou.po.Dept;
import cn.edu.zjou.po.Type;
import cn.edu.zjou.util.DeptConverter;
import cn.edu.zjou.util.TypeConverter;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagCrawler {

    private final Logger logger = LoggerFactory.getLogger(TagCrawler.class);

    private final JXDocument jxDocument;
    private final TypeMapper typeMapper;
    private final DeptMapper deptMapper;

    public TagCrawler(TypeMapper typeMapper, DeptMapper deptMapper, CrawlerConfig crawlerConfig) {
        this.typeMapper = typeMapper;
        this.deptMapper = deptMapper;

        this.jxDocument = JXDocument.createByUrl(crawlerConfig.getStartUrl());
    }


    public void updateTypes() {
        JXNode ul = jxDocument.selNOne("""
                //*[@id="thread_types"]""");

        List<JXNode> aOfTypes = ul.sel(".//li[not(@id)]//a");

        List<Type> types = new ArrayList<>();
        for (var node : aOfTypes) {
            types.add(new Type(node.asElement().ownText()));
        }
        types.add(new Type(TypeConverter.UNKNOWN_TYPE_NAME));

        typeMapper.saveAllWithUniqueTypeName(types);
    }

    public void updateDepts() {
        JXNode ul = jxDocument.selN("""
                //*[@id="thread_types"]""").get(1);

        List<JXNode> aOfDepts = ul.sel(".//li//a");

        List<Dept> depts = new ArrayList<>();
        for (var node : aOfDepts) {
            depts.add(new Dept(node.asElement().ownText()));
        }
        depts.add(new Dept(DeptConverter.UNKNOWN_DEPT_NAME));
        deptMapper.saveAllWithUniqueDeptName(depts);

    }


}
