package com.ra.service.impl;

import com.ra.model.Orders;
import com.ra.service.inter.EmailService;
import com.ra.service.inter.OrderService;
import com.ra.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    private OrderService orderService;

    @Value("${spring.mail.username}")
    private String sender;


    @Override
    public String sendMailtoUserOrder(String orderCode) throws CustomException {
        Orders orders = orderService.findById(orderCode);
        if(orders.getFailCount() >= 3) {
            String message = "Xin chào " + orders.getUsers().getShopName() + " bạn có đơn hàng từ \n" +
                    "-Mã đơn: " + orders.getOrderCode() + "\n" +
                    "-Người gửi: " + orders.getProviderName() + "\n" +
                    "-Địa chỉ gửi: " + orders.getProviderAddress() + "\n" +
                    "-Do đã quá : " + orders.getFailCount() + " lần thực hiện giao hàng không thành công. Đơn hàng của bạn sẽ được hoàn trả về người gửi hàng." + "\n" +
                    "Trân trọng cảm ơn.";

            String message2 = "Xin chào " + orders.getUsers().getShopName() + " bạn có đơn hàng từ \n" +
                    "-Mã đơn: " + orders.getOrderCode() + "\n" +
                    "-Người gửi: " + orders.getReceiverName() + "\n" +
                    "-Địa chỉ gửi: " + orders.getReceiverAddress() + "\n" +
                    "-Do đã quá : " + orders.getFailCount() + " lần thực hiện giao hàng không thành công. Đơn hàng của bạn sẽ được hoàn trả về người gửi hàng." + "\n" +
                    "Trân trọng cảm ơn.";
            try {
                // Gửi tin nhắn đến người gửi hàng
                SimpleMailMessage mailMessageToProvider = new SimpleMailMessage();
                mailMessageToProvider.setFrom(sender);
                mailMessageToProvider.setTo(orders.getProviderEmail());
                mailMessageToProvider.setText(message);
                mailMessageToProvider.setSubject("Giao Hàng Tiết Kiệm");
                javaMailSender.send(mailMessageToProvider);

                // Gửi tin nhắn đến người nhận hàng
                SimpleMailMessage mailMessageToReceiver = new SimpleMailMessage();
                mailMessageToReceiver.setFrom(sender);
                mailMessageToReceiver.setTo(orders.getReceiverEmail());
                mailMessageToReceiver.setText(message2);
                mailMessageToReceiver.setSubject("Giao Hàng Tiết Kiệm");
                javaMailSender.send(mailMessageToReceiver);
                return "Gửi Mail Thành công";
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
        return null;

    }

}
