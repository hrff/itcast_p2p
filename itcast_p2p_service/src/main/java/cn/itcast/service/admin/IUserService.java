package cn.itcast.service.admin;

import cn.itcast.domain.user.UserModel;

public interface IUserService {

    public UserModel findByUsername(String username);

    public UserModel findByPhone(String phone);

    public Boolean signUp(UserModel userModel);

    public UserModel login(String username,String password);

    public UserModel findById(int id);

    public void updatePhoneStatus(String phone, Integer id);

    public void updateRealNameStatus(String realName, String identity, Integer id);

    public void addEmail(String email, int i);

    public void updateEmailStatus(int i);
}
