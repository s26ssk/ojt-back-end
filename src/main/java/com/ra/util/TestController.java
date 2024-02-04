package com.ra.util;

import com.ra.config.Config;
import com.ra.repository.OrdersRepository;
import com.ra.repository.WareHouseRepository;
import com.ra.service.impl.LabelService;
import com.ra.service.inter.OrderService;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;

@RestController
@CrossOrigin(value = "*", allowedHeaders = "*")
public class TestController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    WareHouseRepository wareHouseRepository;
    @Autowired
    LabelService labelService;

    @GetMapping(value = "/barcode/code128/{barcode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> zxingBarcode(@PathVariable("barcode") String barcode)
            throws Exception {
        return ResponseEntity.ok(Config.generateBarcodeImage(barcode));
    }

    @GetMapping(value = "/barcode/qr/{barcode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> zxingQRCode(@PathVariable("barcode") String barcode)
            throws Exception {
        return ResponseEntity.ok(Config.generateQRCodeImage(barcode));
    }

}
