package cn.itcast.dao.admin;

import cn.itcast.domain.userAccount.UserAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserAccountDAO extends JpaRepository<UserAccountModel,Integer> {

    UserAccountModel findByUserId(int id);
}
