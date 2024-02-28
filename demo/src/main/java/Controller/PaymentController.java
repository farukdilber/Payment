package Controller;

import Model.Payment;
import Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/new")
    public ResponseEntity<?> placePayment(@RequestBody Payment payment) {
        try {
            Payment placedPayment = paymentService.placePayment(payment);
            return ResponseEntity.ok(placedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/monthly-statistics")
    public ResponseEntity<?> getMonthlyPaymentStatistics(@RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Long startDate,
                                                         @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, BigDecimal> statistics = paymentService.getMonthlyPaymentStatistics(startDate, endDate);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
