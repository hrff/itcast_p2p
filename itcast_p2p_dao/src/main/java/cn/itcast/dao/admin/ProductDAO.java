package cn.itcast.dao.admin;

import cn.itcast.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDAO extends JpaRepository<Product,Long> {

}
