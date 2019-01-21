package cn.itcast.service.admin.impl;

import cn.itcast.dao.admin.IUserDAO;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.admin.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserService implements IUserService {

    @Autowired
    private IUserDAO userDAO;

    @Override
    public UserModel findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public UserModel findByPhone(String phone) {
        return userDAO.findByPhone(phone);
    }

    @Override
    public Boolean signUp(UserModel userModel) {
       UserModel user=userDAO.save(userModel);
       return user!=null;
    }

    @Override
    public UserModel login(String username, String password) {
        return userDAO.login(username,password);
    }

    @Override
    public UserModel findById(int id) {
        return userDAO.findOne(id);
    }

    @Override
    public void updatePhoneStatus(String phone, Integer id) {
        userDAO.updatePhoneStatus(phone,id);
    }

    @Override
    public void updateRealNameStatus(String realName, String identity, Integer id) {
         userDAO.updateRealNameStatus(realName,identity,id);
    }

    @Override
    public void addEmail(String email, int i) {
        userDAO.addEmail(email,i);
    }

    @Override
    public void updateEmailStatus(int i) {
        userDAO.updateEmailStatus(i);
    }
}
