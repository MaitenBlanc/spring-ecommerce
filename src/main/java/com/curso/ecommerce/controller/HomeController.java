package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Order;
import com.curso.ecommerce.model.OrderDetail;
import com.curso.ecommerce.model.Product;
import com.curso.ecommerce.model.User;
import com.curso.ecommerce.service.IProductService;
import com.curso.ecommerce.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private IProductService productService;

    @Autowired
    private IUserService userService;

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

        // Validar que el producto no se añada más de una vez
        Integer idProduct = product.getId();
        boolean joined = details.stream().anyMatch(p -> Objects.equals(p.getProduct().getId(), idProduct));
        if(!joined) {
            details.add(orderDetail);
        }

        sumTotal = details.stream().mapToDouble(OrderDetail::getTotal).sum();

        order.setTotal(sumTotal);
        model.addAttribute("cart", details);
        model.addAttribute("order", order);


        return "user/cart";
    }

    // Quitar un producto del carrito
    @GetMapping("/delete/cart/{id}")
    public String deleteProductCart(@PathVariable Integer id,Model model) {

        // Lista nueva de productos
        List<OrderDetail> newOrders = new ArrayList<OrderDetail>();

        for(OrderDetail orderDetail: details) {
            if(!Objects.equals(orderDetail.getProduct().getId(), id)) {
                newOrders.add(orderDetail);
            }
        }

        // Poner la nueva lista con los productos restantes
        details = newOrders;

        double sumTotal = 0;

        sumTotal = details.stream().mapToDouble(OrderDetail::getTotal).sum();

        order.setTotal(sumTotal);
        model.addAttribute("cart", details);
        model.addAttribute("order", order);

        return "user/cart";
    }

    @GetMapping("/getCart")
    public String getCart(Model model) {
        model.addAttribute("cart", details);
        model.addAttribute("order", order);
        return "/user/cart";
    }

    @GetMapping("/order")
    public String order(Model model) {

        User user = userService.findById(1).get();

        model.addAttribute("cart", details);
        model.addAttribute("order", order);
        model.addAttribute("user", user);
        return "user/orderresume";
    }
}
