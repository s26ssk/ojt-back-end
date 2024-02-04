package com.ra.service.inter;

import com.ra.model.Orders;
import com.ra.util.exception.CustomException;

public interface EmailService {
    String sendMailtoUserOrder(String orderCode) throws CustomException;
}
