package Service;

import Model.Customer;
import Model.Payment;
import Repository.CustomerRepository;
import Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Customer registerCustomer(String firstName, String lastName, String email, String password) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPassword(password);
        return customerRepository.save(customer);
    }

    public void saveCustomerCreditCardInfoEncrypted(Long customerId, String creditCardNumber, String cvv, LocalDate expiryDate) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        EncryptedCreditCard creditCard = new EncryptedCreditCard();
        creditCard.setCreditCardNumber(creditCardNumber);
        creditCard.setCvv(cvv);
        creditCard.setExpiryDate(expiryDate);
        customer.setEncryptedCreditCard(creditCard);
        customerRepository.save(customer);
    }

    public List<Payment> listPayments(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return paymentRepository.findByCustomer(customer);
    }

    public Map<LocalDate, BigDecimal> queryMonthlyPaymentStatistics(Long customerId, int year) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        List<Payment> payments = paymentRepository.findByCustomer(customer);
        Map<LocalDate, BigDecimal> monthlyPaymentStatistics = new TreeMap<>();
        LocalDate date = LocalDate.of(year, 1, 1);
        while (date.getYear() == year) {
            int count = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Payment payment : payments) {
                if (payment.getPaymentDate().getYear() == year && payment.getPaymentDate().getMonthValue() == date.getMonthValue()) {
                    count++;
                    totalAmount = totalAmount.add(payment.getAmount());
                }
            }
            monthlyPaymentStatistics.put(date, totalAmount.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
            date = date.plusDays(1);
        }
        return monthlyPaymentStatistics;
    }

    public void placeNewPayment(Long customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        EncryptedCreditCard creditCard = customer.getEncryptedCreditCard();
        if (creditCard == null) {
            throw new IllegalStateException("Customer has no encrypted credit card information");
        }
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        paymentRepository.save(payment);
    }
}
