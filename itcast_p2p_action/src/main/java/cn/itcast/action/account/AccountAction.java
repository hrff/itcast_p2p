package cn.itcast.action.account;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.userAccount.UserAccountModel;
import cn.itcast.service.admin.IUserAccountService;
import cn.itcast.service.admin.IUserService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Namespace("/account")
@Controller
@Scope("prototype")
public class AccountAction extends BaseAction {

    @Resource(name = "redisCache")
    private BaseCacheService baseCacheService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAccountService userAccountService;

    //获取用户信息
    @Action("accountHomepage")
    public void accountHomepage() {
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
            UserAccountModel userAccountModel=userAccountService.findByUserId(id);
            //将数据封装，响应到服务器
            List<JSONObject> list=new ArrayList<>();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("u_total",userAccountModel.getTotal());//账户总额
            jsonObject.put("u_balance",userAccountModel.getBalance());//账户余额
            jsonObject.put("u_interest_a",userAccountModel.getInterestA());//账户收益
            list.add(jsonObject);
            getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
            return;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
