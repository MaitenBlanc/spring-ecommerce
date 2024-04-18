package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Product;
import com.curso.ecommerce.model.User;
import com.curso.ecommerce.service.ProductService;
import com.curso.ecommerce.service.UploadFileService;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UploadFileService upload;

    @GetMapping("")
    public String show(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/show";
    }

    @GetMapping("/create")
    public String create() {
        return "products/create";
    }

    @PostMapping("/save")
    public String save(Product product, @RequestParam("img") MultipartFile file) throws IOException {
        logger.info("Este es el objeto producto {}", product);
        User u = new User(1, "", "", "", "", "", "", "");
        product.setUser(u);

        // Para subir imagen al servidor
        if(product.getId() == null) {     // Cuando se crea un producto
            String nameImage = upload.saveImage(file);
            product.setImage(nameImage);
        } else {
            if(file.isEmpty()) {    // Cuando se edita el producto pero no se cambia la imagen
                Product p = new Product();
                p = productService.get(product.getId()).get();
                product.setImage(p.getImage());
            } else {
                String nameImage = upload.saveImage(file);
                product.setImage(nameImage);
            }
        }

        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Product product = new Product();
        Optional<Product> optionalProduct = productService.get(id);
        product = optionalProduct.get();

        logger.info("Producto buscado: {}", product);
        model.addAttribute("product", product);

        return "products/edit";
    }

    @PostMapping("/update")
    public String update(Product product) {
        productService.update(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.delete(id);
        return "redirect:/products";
    }

}
