package cn.itcast.action.product;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.admin.IProductRateService;
import cn.itcast.service.admin.IProductService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.JsonMapper;
import cn.itcast.utils.Response;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
@Namespace("/product")
public class ProductAction extends BaseAction implements ModelDriven<Product> {

    private Product product=new Product();
    @Override
    public Product getModel() {
        return product;
    }

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IProductRateService iProductRateService;

    @Action("/modifyProduct")
    public void modifyProduct(){
        String proEarningRates=this.getRequest().getParameter("proEarningRates");
        Map map=new JsonMapper().fromJson(proEarningRates, Map.class);
        List<ProductEarningRate> productEarningRates=new ArrayList<ProductEarningRate>();
        for (Object key:map.keySet()) {
            ProductEarningRate productEarningRate=new ProductEarningRate();
            productEarningRate.setMonth(Integer.parseInt(key.toString()));
            productEarningRate.setIncomeRate(Double.parseDouble(map.get(key).toString()));
            productEarningRate.setProductId((int) product.getProId());
            productEarningRates.add(productEarningRate);
        }
        product.setProEarningRate(productEarningRates);
        //更新
        iProductService.update(product);
    }


    @Action("/findRates")
    public void findRates(){
        this.getResponse().setCharacterEncoding("utf-8");
        String proId=this.getRequest().getParameter("proId");
        List<ProductEarningRate> productEarningRates=iProductRateService.findRateByProductId(proId);

        try {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(productEarningRates).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Action("/findProductById")
    public void findProductById(){
        this.getResponse().setCharacterEncoding("utf-8");
        String proId=this.getRequest().getParameter("proId");
        Product product=iProductService.findById(Long.parseLong(proId));
        try {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(product).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Action("/findAllProduct")
    public void findAllProduct(){

        this.getResponse().setCharacterEncoding("utf-8");
        List<Product> ps=iProductService.findAll();
        try {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(ps).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
