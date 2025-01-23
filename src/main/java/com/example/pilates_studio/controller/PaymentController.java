package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.PaymentDto;
import com.example.pilates_studio.model.Payment;
import com.example.pilates_studio.model.Purchase;
import com.example.pilates_studio.service.CustomerService;
import com.example.pilates_studio.service.PaymentService;
import com.example.pilates_studio.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class PaymentController {

    private PaymentService paymentService;
    private CustomerService customerService;
    private PurchaseService purchaseService;

    @Autowired
    public PaymentController(PaymentService paymentService, CustomerService customerService, PurchaseService purchaseService) {
        this.paymentService = paymentService;
        this.customerService = customerService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/payments")
    public String listPayments(Model model) {
        List<PaymentDto> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payments-list";
    }

    @GetMapping("/payments/new")
    public String createPaymentForm(Model model) {
        PaymentDto payment = new PaymentDto();
        model.addAttribute("payment", payment);
        model.addAttribute("customers", customerService.findAllCustomers());
        return "payments-create";

    }

    @PostMapping("/payments/new")
    public String savePayment(@Valid @ModelAttribute("payment") PaymentDto payment, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            model.addAttribute("payment", payment);
            model.addAttribute("customers", customerService.findAllCustomers());
            return "purchases-create";
        }

        Purchase purchase = purchaseService.findUnpaidPurchaseIdByCustomerName(payment.getCustomerName());

        if(purchase == null){
            result.rejectValue("customerName", "error.payment", "This customer has no unpaid purchases.");
            redirectAttributes.addFlashAttribute("errorMessage", "Can't create a payment for this customer. There are no unpaid purchases.");
            return "redirect:/payments";
        }

        if(payment.getAmount().compareTo(purchase.getAmountDue()) > 0){
            result.rejectValue("amount", "error.payment", "The payment amount exceeds the amount due.");
            redirectAttributes.addFlashAttribute("errorMessage", "The payment amount exceeds the amount due.");
            return "redirect:/payments";
        }

        if(payment.getAmount().compareTo(purchase.getAmountDue()) == 0){
            purchase.setPaymentComplete(true);
        }

        if(payment.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            result.rejectValue("amount", "error.payment", "The payment amount must be greater than zero.");
            redirectAttributes.addFlashAttribute("errorMessage", "The payment amount must be greater than zero.");
            return "redirect:/payments";
        }

        purchase.setAmountDue(purchase.getAmountDue().subtract(payment.getAmount()));
        payment.setAmountDue(purchase.getAmountDue());
        payment.setTotalPaidAmount(purchase.getPrice().subtract(purchase.getAmountDue()));
        payment.setPurchaseId(purchase.getId());

        purchaseService.updatePurchase(purchaseService.mapToPurchaseDto(purchase));
        paymentService.savePayment(payment);
        redirectAttributes.addFlashAttribute("successMessage", "Payment created successfully.");

        return "redirect:/payments";
    }

}
