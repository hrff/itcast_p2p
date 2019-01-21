package cn.itcast.service.admin.impl;

import cn.itcast.dao.admin.IProductRateDAO;
import cn.itcast.dao.admin.ProductDAO;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.admin.IProductService;
import cn.itcast.utils.ProductStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private IProductRateDAO iProductRateDAO;

    @Override
    public List<Product> findAll() {
        List<Product> ps=productDAO.findAll();
        changeStatusToChinese(ps);
        return ps;
    }

    @Override
    public Product findById(Long proId) {
        Product product=productDAO.findOne(proId);
        changeStatusToChinese(product);
        return product;
    }

    @Override
    public void update(Product product) {
        //先删除再添加
        List<ProductEarningRate> productEarningRates=iProductRateDAO.findByProductId((int) product.getProId());
        if(productEarningRates!=null&&productEarningRates.size()>0){
            iProductRateDAO.delByProId((int) product.getProId());
        }
        //添加
        iProductRateDAO.save(product.getProEarningRate());
        //修改产品信息
        productDAO.save(product);

    }

    /**
     * 方法描述：将状态转换为中文
     *
     * @param products
     *            void
     */
    private void changeStatusToChinese(Product product){
        List<Product> products=new ArrayList<Product>();
        products.add(product);
        changeStatusToChinese(products);
    }

    /**
     * 方法描述：将状态转换为中文
     *
     * @param products
     *            void
     */
    private void changeStatusToChinese(List<Product> products) {
        if (null == products)
            return;
        for (Product product : products) {
            int way = product.getWayToReturnMoney();
            // 每月部分回款
            if (ProductStyle.REPAYMENT_WAY_MONTH_PART.equals(String.valueOf(way))) {
                product.setWayToReturnMoneyDesc("每月部分回款");
                // 到期一次性回款
            } else if (ProductStyle.REPAYMENT_WAY_ONECE_DUE_DATE.equals(String.valueOf(way))) {
                product.setWayToReturnMoneyDesc("到期一次性回款");
            }

            // 是否复投 isReaptInvest 136：是、137：否
            // 可以复投
            if (ProductStyle.CAN_REPEAR == product.getIsRepeatInvest()) {
                product.setIsRepeatInvestDesc("是");
                // 不可复投
            } else if (ProductStyle.CAN_NOT_REPEAR == product.getIsRepeatInvest()) {
                product.setIsRepeatInvestDesc("否");
            }
            // 年利率
            if (ProductStyle.ANNUAL_RATE == product.getEarningType()) {
                product.setEarningTypeDesc("年利率");
                // 月利率 135
            } else if (ProductStyle.MONTHLY_RATE == product.getEarningType()) {
                product.setEarningTypeDesc("月利率");
            }

            if (ProductStyle.NORMAL == product.getStatus()) {
                product.setStatusDesc("正常");
            } else if (ProductStyle.STOP_USE == product.getStatus()) {
                product.setStatusDesc("停用");
            }

            // 是否可转让
            if (ProductStyle.CAN_NOT_TRNASATION == product.getIsAllowTransfer()) {
                product.setIsAllowTransferDesc("否");
            } else if (ProductStyle.CAN_TRNASATION == product.getIsAllowTransfer()) {
                product.setIsAllowTransferDesc("是");
            }
        }
    }

}
