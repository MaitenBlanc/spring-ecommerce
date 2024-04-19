package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Product;
import com.curso.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private ProductService productService;

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
    public String addToCart() {
        return "user/cart";
    }
}
