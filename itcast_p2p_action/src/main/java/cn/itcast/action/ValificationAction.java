package cn.itcast.action;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.admin.IEmailService;
import cn.itcast.service.admin.IUserService;
import cn.itcast.service.admin.impl.UserService;
import cn.itcast.utils.EmailUtils;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import cn.itcast.utils.SecretUtil;
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
    @Autowired
    private IEmailService emailService;

    //激活邮件其实就是修改状态
    @Action("emailactivation")
    public void emailactivation() {
        this.getResponse().setContentType("text/html;charset=utf-8");
        String us=this.getRequest().getParameter("us");
        //解密
        try {
            String userId=SecretUtil.decode(us);
            //修改邮箱状态
            userService.updateEmailStatus(Integer.parseInt(userId));
            getResponse().getWriter().write("邮箱认证成功!");
        } catch (Exception e) {
            try {
                getResponse().getWriter().write("邮箱认证失败!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

    }
    //发送邮件
    @Action("auth")
    public void vauth(){
        String userId=this.getRequest().getParameter("userId");
        String username=this.getRequest().getParameter("username");
        String email=this.getRequest().getParameter("email");

        String title="p2p邮箱认证激活";
        //加密后的id
        try {
            String enc=SecretUtil.encrypt(userId);
            String content= EmailUtils.getMailCapacity(email,enc,username);
            emailService.sendEmail(email,title,content);//发送邮件
            //检查是否绑定
            UserModel userModel=userService.findById(Integer.parseInt(userId));
            if(userModel==null&&userModel.getEmailStatus()==0){
                userService.addEmail(email,Integer.parseInt(userId));
            }
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //实名认证
    @Action("verifiRealName")
    public void verifiRealName(){
        String token= GetHttpResponseHeader.getHeadersInfo(this.getRequest());
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            //token过期
            try {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String,Object> map=baseCacheService.getHmap(token);
        if(map==null&&map.size()==0){ //未登录
            try {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //姓名
        String realName=this.getRequest().getParameter("realName");
        String identity=this.getRequest().getParameter("identity");
        userService.updateRealNameStatus(realName,identity,(Integer) map.get("id"));
        try {
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
