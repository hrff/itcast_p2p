package cn.itcast.action;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.dao.admin.IUserAccountDAO;
import cn.itcast.domain.user.UserModel;
import cn.itcast.domain.userAccount.UserAccountModel;
import cn.itcast.service.admin.IUserAccountService;
import cn.itcast.service.admin.IUserService;
import cn.itcast.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static org.apache.struts2.interceptor.DateTextFieldInterceptor.DateWord.m;

@Namespace("/user")
@Controller
@Scope("prototype")
public class UserAction extends BaseAction implements ModelDriven<UserModel> {

    private UserModel user=new UserModel();

    //手机认证
    @Action("addPhone")
    public void addPhone() {
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

        //判断验证码是否填写正确
        String phone=this.getRequest().getParameter("phone");
        String code=this.getRequest().getParameter("phoneCode");
        String _code=baseCacheService.get(phone);
        if(!code.equals(_code)){
            try {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //判断用户是否绑定手机
        UserModel userModel=userService.findById((Integer) map.get("id"));
        if(userModel.getPhoneStatus()==1){
            try {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.MOBILE_ALREADY_REGISTER).toJSON());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //若无修改状态
        userService.updatePhoneStatus(phone,(Integer) map.get("id"));
        try {
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Resource(name = "redisCache")
    private BaseCacheService baseCacheService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAccountService userAccountService;


    public String generateUserToken(String userName) {

        try {
            // 生成令牌
            String token = TokenUtil.generateUserToken(userName);

            // 根据用户名获取用户
            UserModel user = userService.findByUsername(userName);
            // 将用户信息存储到map中。
            Map<String, Object> tokenMap = new HashMap<String, Object>();
            tokenMap.put("id", user.getId());
            tokenMap.put("userName", user.getUsername());
            tokenMap.put("phone", user.getPhone());
            tokenMap.put("userType", user.getUserType());
            tokenMap.put("payPwdStatus", user.getPayPwdStatus());
            tokenMap.put("emailStatus", user.getEmailStatus());
            tokenMap.put("realName", user.getRealName());
            tokenMap.put("identity", user.getIdentity());
            tokenMap.put("realNameStatus", user.getRealNameStatus());
            tokenMap.put("payPhoneStatus", user.getPhoneStatus());

            baseCacheService.del(token);
            baseCacheService.setHmap(token, tokenMap); // 将信息存储到redis中

            // 获取配置文件中用户的生命周期，如果没有，默认是30分钟
            String tokenValid = ConfigurableConstants.getProperty("token.validity", "30");
            tokenValid = tokenValid.trim();
            baseCacheService.expire(token, Long.valueOf(tokenValid) * 60);

            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.build().setStatus("-9999").toJSON();
        }
    }

    //获取安全等级
    @Action("userSecureDetailed")
    public void userSecureDetailed() {

        try{
            //获取token
            String token= GetHttpResponseHeader.getHeadersInfo(this.getRequest());
            if(StringUtils.isEmpty(token)){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
                return;
            }
            //获取redis内的token
            Map<String,Object> map=baseCacheService.getHmap(token);
            if(map==null||map.size()==0){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
                return;
            }
            int id= (int) map.get("id");
            //根据id查用户
            UserModel userModel=userService.findById(id);
            List<Map<String,Object>> list=new ArrayList<>();
            if(userModel==null){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.USER_NOT_EXIST).toJSON());
                return;
            }
            map.put("phoneStatus",userModel.getPhoneStatus());
            map.put("emailStatus",userModel.getEmailStatus());
            map.put("payPwdStatus",userModel.getPayPwdStatus());
            map.put("realNameStatus",userModel.getRealNameStatus());
            map.put("passwordStatus",userModel.getPassword());
            map.put("username",userModel.getUsername());
            map.put("phone",userModel.getPhone());
            list.add(map);
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
            return;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取安全等级
    @Action("userSecure")
    public void userSecure() {

        try{
            //获取token
            String token= GetHttpResponseHeader.getHeadersInfo(this.getRequest());
            if(StringUtils.isEmpty(token)){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
                return;
            }
            //获取redis内的token
            Map<String,Object> map=baseCacheService.getHmap(token);
            if(map==null||map.size()==0){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
                return;
            }
            int id= (int) map.get("id");
            //根据id查用户
            UserModel userModel=userService.findById(id);
            List<Map<String,Object>> list=new ArrayList<>();
            if(userModel==null){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.USER_NOT_EXIST).toJSON());
                return;
            }
            map.put("phoneStatus",userModel.getPhoneStatus());
            map.put("emailStatus",userModel.getEmailStatus());
            map.put("payPwdStatus",userModel.getPayPwdStatus());
            map.put("realNameStatus",userModel.getRealNameStatus());
            list.add(map);
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
            return;

        }catch (Exception e){
           e.printStackTrace();
        }
    }
    //登出
    @Action("logout")
    public void loginout() {
        String token=this.getRequest().getHeader("token");
        Map<String, Object> map=baseCacheService.getHmap(token);
        try {
            if(map==null&&map.size()==0) {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            }
            baseCacheService.del(token);
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
        } catch (IOException e) {
                e.printStackTrace();
        }

    }

    //登录
    @Action("login")
    public void login() {
        //获取uuid
        String signUuid=this.getRequest().getParameter("signUuid");
        String signCode=this.getRequest().getParameter("signCode");
        //判空
        try {
            if (StringUtils.isEmpty(user.getUsername())) {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_USERNAME).toJSON());
                return;
            }
            if (StringUtils.isEmpty(user.getPassword())) {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_PASSWORD).toJSON());
                return;
            }
            //判断是否为手机号登录
            Boolean str=CommomUtil.isMobile(user.getUsername());
            String username=user.getUsername();
            if(str){
               username=userService.findByPhone(user.getUsername()).getUsername();
            }
            String token=generateUserToken(username);
            //密码加密
            String pwd=MD5Util.md5(username+user.getPassword());
            UserModel userModel=userService.login(username,pwd);
            Map<String,Object> map=new HashMap<>();
            if(userModel!=null){
                map.put("id",userModel.getId());
                map.put("userName",userModel.getUsername());
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(map).setToken(token).toJSON());
                return;
            }
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN ).toJSON());
            return;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //注册
    @Action("signup")
    public void signUp(){
        String psMD5=MD5Util.md5(user.getUsername()+user.getPassword().toLowerCase());
        user.setPassword(psMD5);
        Boolean flag=userService.signUp(user);
        if(flag){
            //注册成功开账户
            userAccountService.add(user.getId());
            //将用户存入redis中，有效时间为30分钟
            String token=generateUserToken(user.getUsername());
            //响应数据返回 token data status
            Map<String,Object>  m=new HashMap<>();
            m.put("id",user.getId());
            try {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(m).setToken(token).toJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.REGISTER_LOSED).toJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //验证图形验证码
    @Action("codeValidate")
    public void codeValidate(){
        String signUuid=this.getRequest().getParameter("signUuid");
        String signCode=this.getRequest().getParameter("signCode");//文本框输入的验证码
        //从redis中获取
        String _signCode=baseCacheService.get(signUuid);
        try{
            if(StringUtils.isEmpty(signUuid)){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
                return;
            }
            if(StringUtils.isEmpty(signCode)){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
                return;
            }
            if(signCode.equalsIgnoreCase(_signCode)){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                return;
            }
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
            return;
        }catch(IOException e){

        }
    }

    //验证用户名
    @Action("validatePhone")
    public void validatePhone(){
        String phone=this.getRequest().getParameter("phone");
        UserModel user=userService.findByPhone(phone);

        try {
            if(user==null){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                return;
            }
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.MOBILE_ALREADY_REGISTER).toJSON());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //验证用户名
    @Action("validateUserName")
    public void validateUsername(){
        String username=this.getRequest().getParameter("username");
        UserModel user=userService.findByUsername(username);

        try {
            if(user==null){
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                return;
            }
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.ALREADY_EXIST_OF_USERNAME).toJSON());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取一个随机数
    @Action("uuid")
    public void uuid(){
       //随机产生一个uuid
        String uuid=UUID.randomUUID().toString();
      //将uuid存入redis中
      //设置有效时间3s
        baseCacheService.set(uuid, uuid);
        baseCacheService.expire(uuid, 3*60);
        try {
            getResponse().getWriter().write(Response.build().setStatus("1").setUuid(uuid).toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //画图形验证码
    @Action("validateCode")
    public void validateCode(){

        String tokenUuid=this.getRequest().getParameter("tokenUuid");
        String uuid=baseCacheService.get(tokenUuid);

        if(StringUtils.isEmpty(uuid)){
            try {
                this.getResponse().getWriter().write(Response.build().setStatus("-999").toJSON());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通过工具产生图形验证码
        String str=ImageUtil.getRundomStr();
        baseCacheService.del(uuid);
        baseCacheService.set(tokenUuid,str);
        baseCacheService.expire(tokenUuid,3*60);

        //将验证码相应到浏览器
        try {
            ImageUtil.getImage(str,this.getResponse().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserModel getModel() {
        return user;
    }
}
