package cn.itcast.service.admin;

import cn.itcast.domain.product.Product;

import java.util.List;

public interface IProductService {

    public List<Product> findAll();
    public Product findById(Long proId);
    public  void update(Product product);
}
