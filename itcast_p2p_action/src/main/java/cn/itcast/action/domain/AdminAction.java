package cn.itcast.action.domain;
import cn.itcast.domain.AdminModel;
import cn.itcast.action.common.BaseAction ;
import cn.itcast.service.admin.IAdminService;
import cn.itcast.service.admin.impl.AdminService;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Scope("prototype")
@Namespace("/account")
public class AdminAction extends BaseAction {

    @Autowired
    private IAdminService adminService;

    @Action("/login")
    public void login(){
      String username=this.getRequest().getParameter("username")  ;
      String password=this.getRequest().getParameter("password")  ;

      try {
          AdminModel admin=adminService.login(username,password);
          if(admin!=null) {
              this.getResponse().getWriter().write("{\"status\":\"1\"}");
          }else {
              this.getResponse().getWriter().write("{\"status\":\"0\"}");
          }
      } catch (IOException e) {
          e.printStackTrace();
      }

    }
}
