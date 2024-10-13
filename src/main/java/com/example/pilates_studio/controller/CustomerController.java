package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.service.CustomerService;
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
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public String listCustomers(Model model){
        List<CustomerDto> customers = customerService.findAllCustomers();
        model.addAttribute("customers", customers);
        return "customers-list";
    }

    @GetMapping("/customers/new")
    public String createCustomerForm(Model model){
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "customers-create";
    }

    @PostMapping("/customers/new")
    public String saveCustomer(@Valid @ModelAttribute("customer") CustomerDto customerDto, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("customer", customerDto);
            return "customers-create";
        }
        customerDto.setRemainingUsage(0);
        customerService.saveCustomer(customerDto);
        return "redirect:/customers";
    }

    @GetMapping("/customers/{customerId}/edit")
    public String editCustomerForm(@PathVariable("customerId") Integer customerId, Model model){
        CustomerDto customer = customerService.findCustomerById(customerId);
        model.addAttribute("customer", customer);
        return "customers-edit";
    }

    @PostMapping("/customers/{customerId}/edit")
    public String updateCustomer(@PathVariable("customerId") Integer customerId, @Valid @ModelAttribute("customer") CustomerDto customerDto, BindingResult result){
        if(result.hasErrors()){
            return "customers-edit";
        }
        customerDto.setId(customerId);
        customerService.updateCustomer(customerDto);
        return "redirect:/customers";
    }

    @GetMapping("/customers/{customerId}/delete")
    public String deleteCustomer(@PathVariable("customerId") Integer customerId, RedirectAttributes redirectAttributes){
        try{
            customerService.delete(customerId);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete customer. This may be due to the customer having an appointment");
        }
        return "redirect:/customers";
    }



}
