package org.example.service;

import org.example.dao.CustomerDAO;
import org.example.entity.Customer;

import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer registerCustomer(String name, String email, String phone, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        Customer existing = customerDAO.findByEmail(email.trim());
        if (existing != null) {
            throw new IllegalArgumentException("Email is already registered: " + email);
        }
        Customer customer = new Customer(name.trim(), email.trim(), phone != null ? phone.trim() : "", address != null ? address.trim() : "");
        customerDAO.save(customer);
        return customer;
    }

    public Customer updateCustomer(Long id, String name, String email, String phone, String address) {
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with ID: " + id);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        Customer existing = customerDAO.findByEmail(email.trim());
        if (existing != null && !existing.getId().equals(id)) {
            throw new IllegalArgumentException("Email is already used by another customer: " + email);
        }
        customer.setName(name.trim());
        customer.setEmail(email.trim());
        customer.setPhone(phone != null ? phone.trim() : "");
        customer.setAddress(address != null ? address.trim() : "");
        customerDAO.update(customer);
        return customer;
    }

    public Customer getCustomerById(Long id) {
        return customerDAO.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerDAO.findAll();
        }
        return customerDAO.searchByNameOrEmail(keyword.trim());
    }

    public long getTotalCustomersCount() {
        return customerDAO.countTotal();
    }
}
