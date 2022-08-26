package cn.edu.zjou.event;

import lombok.Getter;
import lombok.Setter;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserEvent extends ApplicationEvent {

    private JXDocument jxd;
    private int userId;

    public UserEvent(Object source, JXDocument jxd, int userId) {
        super(source);
        this.jxd = jxd;
        this.userId = userId;
    }
}
