package Util;

import Model.Customer;
import Model.Payment;
import Repository.CustomerRepository;
import Repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class PaymentUtil {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Payment createPayment(Long customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    public Map<LocalDate, BigDecimal> getMonthlyPaymentStatistics(Long customerId, int year) {
        List<Payment> payments = getPaymentsByCustomerId(customerId);
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
}
