package Repository;

import Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByEmail(String email);

    List<Customer> findByLastName(String lastName);

    List<Customer> findByFirstName(String firstName);

}
