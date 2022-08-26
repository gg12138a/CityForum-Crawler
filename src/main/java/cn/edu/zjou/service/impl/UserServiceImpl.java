package cn.edu.zjou.service.impl;

import cn.edu.zjou.crawler.UserCrawler;
import cn.edu.zjou.mapper.UserMapper;
import cn.edu.zjou.po.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author G_xy
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> {

}
