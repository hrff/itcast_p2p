package cn.itcast.dao.admin;

import cn.itcast.domain.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IUserDAO extends JpaRepository<UserModel,Integer> {

    UserModel findByUsername(String username);

    UserModel findByPhone(String phone);

    @Query("select u from UserModel u where u.username=?1 and u.password=?2")
    UserModel login(String username,String password);

    @Modifying
    @Query("update  UserModel u set u.phone=?1,u.phoneStatus=1 where u.id=?2")
    void updatePhoneStatus(String phone, Integer id);

    @Modifying
    @Query("update  UserModel u set u.realName=?1,u.identity=?2,u.realNameStatus=1 where u.id=?3")
    void updateRealNameStatus(String realName, String identity, Integer id);

    @Modifying
    @Query("update  UserModel u set u.email=?1 where u.id=?2")
    void addEmail(String email, int i);

    @Modifying
    @Query("update  UserModel u set u.emailStatus=1 where u.id=?1")
    void updateEmailStatus(int i);
}
