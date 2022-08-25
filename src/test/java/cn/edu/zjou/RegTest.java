package cn.edu.zjou;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class RegTest {

    @Test
    public void getPageNum() {
        String str = "/ 349 页";
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(str);
        System.out.println(matcher.find());
        System.out.println(matcher.group(0));
    }

    @Test
    public void getBBSID() {
        String href = "thread-812083-1-1.html";
        Pattern pattern = Pattern.compile("thread-(?<bbsID>\\d+)-");
        Matcher matcher = pattern.matcher(href);
        if (matcher.find()){
            String group = matcher.group("bbsID");
            int bbsId = Integer.parseInt(group);
            System.out.println(bbsId);
        }else {
            System.out.println("not found!");
        }
    }

}
