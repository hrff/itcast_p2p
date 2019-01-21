package cn.itcast.service.admin;

import cn.itcast.domain.product.ProductEarningRate;

import java.util.List;

public interface IProductRateService {

    public List<ProductEarningRate> findRateByProductId(String productId);
}
