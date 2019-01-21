package cn.itcast.service.admin.impl;

import cn.itcast.dao.admin.AdminDAO;
import cn.itcast.domain.AdminModel;
import cn.itcast.service.admin.IAdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminService implements IAdminService {

    @Resource
    private AdminDAO adminDAO;

    @Override
    public AdminModel login(String username, String password) {
        return adminDAO.login(username,password);
    }
}
