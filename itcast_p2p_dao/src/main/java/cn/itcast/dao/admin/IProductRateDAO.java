package cn.itcast.dao.admin;

import cn.itcast.domain.product.ProductEarningRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface IProductRateDAO extends JpaRepository<ProductEarningRate,Integer> {

    public List<ProductEarningRate> findByProductId(int productId);

    @Transactional
    @Modifying//不可少
    @Query("delete from ProductEarningRate per where per.productId=?1")
    public void delByProId(int proId);
}
