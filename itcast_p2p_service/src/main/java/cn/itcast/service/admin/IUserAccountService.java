package cn.itcast.service.admin;

import cn.itcast.domain.userAccount.UserAccountModel;

public interface IUserAccountService {

    public void add(int id);

    public UserAccountModel findByUserId(int id);
}
