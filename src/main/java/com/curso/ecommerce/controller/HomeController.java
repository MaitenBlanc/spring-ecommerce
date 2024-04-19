package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Order;
import com.curso.ecommerce.model.OrderDetail;
import com.curso.ecommerce.model.Product;
import com.curso.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductService productService;

    // Para almacenar los detalles de la orden de compra
    List<OrderDetail> details = new ArrayList<OrderDetail>();

    // Almacena los datos de la orden
    Order order = new Order();

    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        return "user/home";
    }

    @GetMapping("producthome/{id}")
    public String productHome(@PathVariable Integer id, Model model) {
        logger.info("Id producto enviado como parámetro {}", id);

        Product product = new Product();
        Optional<Product> productOptional = productService.get(id);
        product = productOptional.get();

        model.addAttribute("product", product);

        return "user/producthome";
    }

    @PostMapping("/cart")
    public String addToCart(@RequestParam Integer id, @RequestParam Integer stock, Model model) {
        OrderDetail orderDetail = new OrderDetail();
        Product product = new Product();
        double sumTotal = 0;

        Optional<Product> productOptional = productService.get(id);
        logger.info("Producto añadido: {}", productOptional.get());
        logger.info("Cantidad: {}", stock);
        product = productOptional.get();

        orderDetail.setAmount(stock);
        orderDetail.setPrice(product.getPrice());
        orderDetail.setName(product.getName());
        orderDetail.setTotal(product.getPrice() * stock);
        orderDetail.setProduct(product);

        details.add(orderDetail);

        sumTotal = details.stream().mapToDouble(dt -> dt.getTotal()).sum();

        order.setTotal(sumTotal);
        model.addAttribute("cart", details);
        model.addAttribute("order", order);


        return "user/cart";
    }
}
