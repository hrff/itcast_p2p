package cn.itcast.service.admin.impl;

import cn.itcast.dao.admin.IUserAccountDAO;
import cn.itcast.domain.userAccount.UserAccountModel;
import cn.itcast.service.admin.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService implements IUserAccountService {
    @Autowired
    private IUserAccountDAO userAccountDAO;

    @Override
    public void add(int id) {
        UserAccountModel userAccountModel=new UserAccountModel();
        userAccountModel.setId(id);
        userAccountDAO.save(userAccountModel);
    }

    @Override
    public UserAccountModel findByUserId(int id) {
        return userAccountDAO.findByUserId(id);
    }
}
