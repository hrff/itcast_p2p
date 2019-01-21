package cn.itcast.service.admin.impl;

import cn.itcast.dao.admin.IProductRateDAO;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.admin.IProductRateService;
import cn.itcast.service.admin.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Service
public class ProductRateService implements IProductRateService {

    @Autowired
    private IProductRateDAO iProductRateDAO;
    @Override
    public List<ProductEarningRate> findRateByProductId(String productId) {
        return iProductRateDAO.findByProductId(Integer.parseInt(productId));
    }
}
