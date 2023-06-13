package com.example.lab2_server.service;

import com.example.lab2_server.dao.PaymentDao;
import com.example.lab2_server.model.Payment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentsService {
    private final PaymentDao paymentDao;

    public PaymentsService(PaymentDao paymentDao){
        this.paymentDao = paymentDao;
    }

    public void addPayment(int clientId, int serviceId){
        Payment payment = new Payment(clientId, serviceId);
        paymentDao.save(payment);
    }

    public List<Long> getClientPaidServices(int clientId){
        List<Payment> payments = paymentDao.findAllByClientId(clientId);
        List<Long> servicesIds =  payments.stream()
                .map(payment -> payment.getServiceId())
                .collect(Collectors.toList());
        return servicesIds;
    }
}

