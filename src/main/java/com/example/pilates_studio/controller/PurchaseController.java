package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.dto.PurchaseDto;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.model.PackageStatus;
import com.example.pilates_studio.model.Purchase;
import com.example.pilates_studio.service.CustomerService;
import com.example.pilates_studio.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PurchaseController {

    private PurchaseService purchaseService;
    private CustomerService customerService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, CustomerService customerService){
        this.purchaseService = purchaseService;
        this.customerService = customerService;
    }

    @GetMapping("/purchases")
    public String listPurchases(Model model){
        List<PurchaseDto> purchases = purchaseService.findAllPurchases();
        model.addAttribute("purchases", purchases);
        return "purchases-list";
    }

    @GetMapping("/purchases/new")
    public String createPurchaseForm(Model model){
        Purchase purchase = new Purchase();
        model.addAttribute("purchase", purchase);
        model.addAttribute("customers", customerService.findAllCustomers());
        return "purchases-create";
    }

    @PostMapping("/purchases/new")
    public String savePurchase(@Valid @ModelAttribute("purchase") Purchase purchase, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if (purchaseService.existsInUsePurchaseByCustomerId(purchase.getCustomer().getId()) && customerService.findCustomerById(purchase.getCustomer().getId()).getRemainingUsage() != 0) {
            result.rejectValue("customer", "error.purchase", "This customer already has an active purchase.");
            redirectAttributes.addFlashAttribute("errorMessage", "Can't create a new purchase for this customer. There is already a purchase in use");
            return "redirect:/purchases";
        }
        if(result.hasErrors()){
            model.addAttribute("purchase", purchase);
            model.addAttribute("customers", customerService.findAllCustomers());
            return "purchases-create";
        }

        PurchaseDto purchaseDto = new PurchaseDto(null, customerService.findCustomerById(purchase.getCustomer().getId()).getName(), purchase.isPaymentComplete(),PackageStatus.IN_USE, purchase.getDescription(), purchase.getLessonCount());
        purchaseService.savePurchase(purchaseDto);

        CustomerDto customer = customerService.findCustomerById(purchase.getCustomer().getId());
        customer.setRemainingUsage(purchase.getLessonCount());
        customerService.updateCustomer(customer);

        return "redirect:/purchases";
    }

    @GetMapping("/purchases/{purchaseId}/edit")
    public String editPurchaseForm(@PathVariable("purchaseId") Integer purchaseId, Model model){
        PurchaseDto purchase = purchaseService.findPurchaseById(purchaseId);
        model.addAttribute("purchase", purchase);
        return "purchases-edit";
    }

    @PostMapping("/purchases/{purchaseId}/edit")
    public String updatePurchase(@PathVariable("purchaseId") Integer purchaseId, @Valid @ModelAttribute("purchase") PurchaseDto purchaseDto, BindingResult result){
        if(result.hasErrors()){
            return "purchases-edit";
        }
        PurchaseDto purchase = purchaseService.findPurchaseById(purchaseId);
        purchaseDto.setId(purchaseId);
        purchaseDto.setCustomerName(purchase.getCustomerName());
        purchaseDto.setPackageStatus(purchase.getPackageStatus());
        purchaseDto.setLessonCount(purchase.getLessonCount());
        purchaseService.updatePurchase(purchaseDto);
        return "redirect:/purchases";
    }


}
