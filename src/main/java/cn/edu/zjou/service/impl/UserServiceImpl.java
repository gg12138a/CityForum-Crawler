package cn.edu.zjou.service.impl;

import cn.edu.zjou.crawler.UserCrawler;
import cn.edu.zjou.mapper.UserMapper;
import cn.edu.zjou.po.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author G_xy
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> {

    private final UserMapper userMapper;

    private final UserCrawler userCrawler;

    public UserServiceImpl(UserMapper userMapper, UserCrawler userCrawler) {
        this.userMapper = userMapper;
        this.userCrawler = userCrawler;
    }

    /**
     * 保存User，若数据库不存在该user，则爬取后保存。
     **/

    public void saveOrCrawlUser(int userId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(User::getUserId, userId);

        User selectedUser = userMapper.selectOne(wrapper);
        if (selectedUser == null) {
            String username = userCrawler.getUsernameByUserId(userId);
            this.save(new User(userId, username));
        }
    }
}
