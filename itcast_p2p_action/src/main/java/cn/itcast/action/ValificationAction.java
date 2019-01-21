package cn.itcast.action;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.admin.IUserService;
import cn.itcast.service.admin.impl.UserService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Lenovo on 2019/1/20.
 */
@Namespace("/verification")
@Controller
@Scope("prototype")
public class ValificationAction extends BaseAction {

    @Resource(name = "redisCache")
    private BaseCacheService baseCacheService;
    @Autowired
    private IUserService userService;

    //获取短信验证码
    @Action("sendMessage")
    public void sendMessage(){
        String phone=this.getRequest().getParameter("phone");
        String number=RandomStringUtils.randomNumeric(6);
        //存入redis
        baseCacheService.set(phone,number);
        baseCacheService.expire(phone,3*60);
        System.out.println("向"+phone+"发送短信验证码:"+number);
        try {
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;

    }
}
