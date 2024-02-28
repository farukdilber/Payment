package Controller;

import Model.Customer;
import Model.Payment;
import Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {
        try {
            Customer registeredCustomer = customerService.registerCustomer(customer);
            return ResponseEntity.ok(registeredCustomer);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{customerId}/payments")
    public ResponseEntity<?> listPayments(@PathVariable Long customerId) {
        try {
            List<Payment> payments = customerService.listPayments(customerId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
