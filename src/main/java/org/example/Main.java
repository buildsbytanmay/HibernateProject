package org.example;

import org.example.entity.Cart;
import org.example.entity.CartItem;
import org.example.entity.Customer;
import org.example.entity.Order;
import org.example.entity.OrderItem;
import org.example.entity.Product;
import org.example.service.CartService;
import org.example.service.CustomerService;
import org.example.service.OrderService;
import org.example.service.ProductService;
import org.example.util.JPAUtil;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static ProductService productService;
    private static CustomerService customerService;
    private static CartService cartService;
    private static OrderService orderService;

    public static void main(String[] args) {
        try {
            JPAUtil.getEntityManager().close();
            productService = new ProductService();
            customerService = new CustomerService();
            cartService = new CartService();
            orderService = new OrderService();
        } catch (Throwable t) {
            System.out.println("Failed to initialize database connection.");
            System.out.println("Please configure your MySQL credentials in src/main/resources/META-INF/persistence.xml");
            return;
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    handleProductMenu();
                    break;
                case 2:
                    handleCustomerMenu();
                    break;
                case 3:
                    handleCartMenu();
                    break;
                case 4:
                    handleCheckout();
                    break;
                case 5:
                    handleOrderMenu();
                    break;
                case 6:
                    handleInventoryMenu();
                    break;
                case 7:
                    handleReportsMenu();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 0 and 7.");
            }
        }

        JPAUtil.shutdown();
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("   SHOPPING CART AND ORDER MANAGEMENT SYSTEM");
        System.out.println("==================================================");
        System.out.println("1. Product Management");
        System.out.println("2. Customer Management");
        System.out.println("3. Shopping Cart");
        System.out.println("4. Place Order");
        System.out.println("5. Order Management");
        System.out.println("6. Inventory Management");
        System.out.println("7. Reports");
        System.out.println("0. Exit");
        System.out.println("==================================================");
    }

    private static void handleProductMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- PRODUCT MANAGEMENT ---");
            System.out.println("1. Add Product");
            System.out.println("2. View All Products");
            System.out.println("3. Search Products");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewAllProducts();
                    break;
                case 3:
                    searchProducts();
                    break;
                case 4:
                    updateProduct();
                    break;
                case 5:
                    deleteProduct();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void addProduct() {
        System.out.println();
        System.out.println("-- Add New Product --");
        String name = readString("Enter product name: ");
        String category = readString("Enter category: ");
        BigDecimal price = readBigDecimal("Enter price: ");
        int stock = readInt("Enter initial stock quantity: ");

        try {
            Product product = productService.addProduct(name, category, price, stock);
            System.out.println("Success: Product added successfully with ID #" + product.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllProducts() {
        System.out.println();
        System.out.println("-- Product Catalog --");
        try {
            List<Product> products = productService.getAllProducts();
            printProductsTable(products);
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    private static void searchProducts() {
        System.out.println();
        System.out.println("-- Search Products --");
        String kw = readString("Enter search keyword (name or category): ");
        try {
            List<Product> products = productService.searchProducts(kw);
            printProductsTable(products);
        } catch (Exception e) {
            System.out.println("Error searching products: " + e.getMessage());
        }
    }

    private static void updateProduct() {
        System.out.println();
        System.out.println("-- Update Product --");
        long id = readLong("Enter product ID to update: ");
        try {
            Product p = productService.getProductById(id);
            if (p == null) {
                System.out.println("Error: Product not found with ID #" + id);
                return;
            }

            System.out.println("Current Name: " + p.getName());
            String name = readOptionalString("Enter new name (press Enter to keep): ", p.getName());

            System.out.println("Current Category: " + p.getCategory());
            String category = readOptionalString("Enter new category (press Enter to keep): ", p.getCategory());

            System.out.println("Current Price: " + p.getPrice());
            BigDecimal price = readOptionalBigDecimal("Enter new price (press Enter to keep): ", p.getPrice());

            System.out.println("Current Stock: " + p.getStockQuantity());
            int stock = readOptionalInt("Enter new stock quantity (press Enter to keep): ", p.getStockQuantity());

            productService.updateProduct(id, name, category, price, stock);
            System.out.println("Success: Product #" + id + " updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    private static void deleteProduct() {
        System.out.println();
        System.out.println("-- Delete Product --");
        long id = readLong("Enter product ID to delete: ");
        try {
            productService.deleteProduct(id);
            System.out.println("Success: Product #" + id + " deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleCustomerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- CUSTOMER MANAGEMENT ---");
            System.out.println("1. Register Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Search Customers");
            System.out.println("4. Update Customer");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    registerCustomer();
                    break;
                case 2:
                    viewAllCustomers();
                    break;
                case 3:
                    searchCustomers();
                    break;
                case 4:
                    updateCustomer();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void registerCustomer() {
        System.out.println();
        System.out.println("-- Register New Customer --");
        String name = readString("Enter customer name: ");
        String email = readString("Enter email address: ");
        String phone = readString("Enter phone number: ");
        String address = readString("Enter address: ");

        try {
            Customer customer = customerService.registerCustomer(name, email, phone, address);
            System.out.println("Success: Customer registered with ID #" + customer.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllCustomers() {
        System.out.println();
        System.out.println("-- Customer Directory --");
        try {
            List<Customer> customers = customerService.getAllCustomers();
            printCustomersTable(customers);
        } catch (Exception e) {
            System.out.println("Error fetching customers: " + e.getMessage());
        }
    }

    private static void searchCustomers() {
        System.out.println();
        System.out.println("-- Search Customers --");
        String kw = readString("Enter search keyword (name or email): ");
        try {
            List<Customer> customers = customerService.searchCustomers(kw);
            printCustomersTable(customers);
        } catch (Exception e) {
            System.out.println("Error searching customers: " + e.getMessage());
        }
    }

    private static void updateCustomer() {
        System.out.println();
        System.out.println("-- Update Customer --");
        long id = readLong("Enter customer ID to update: ");
        try {
            Customer c = customerService.getCustomerById(id);
            if (c == null) {
                System.out.println("Error: Customer not found with ID #" + id);
                return;
            }

            System.out.println("Current Name: " + c.getName());
            String name = readOptionalString("Enter new name (press Enter to keep): ", c.getName());

            System.out.println("Current Email: " + c.getEmail());
            String email = readOptionalString("Enter new email (press Enter to keep): ", c.getEmail());

            System.out.println("Current Phone: " + c.getPhone());
            String phone = readOptionalString("Enter new phone (press Enter to keep): ", c.getPhone());

            System.out.println("Current Address: " + c.getAddress());
            String address = readOptionalString("Enter new address (press Enter to keep): ", c.getAddress());

            customerService.updateCustomer(id, name, email, phone, address);
            System.out.println("Success: Customer #" + id + " updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }
    }

    private static void handleCartMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- SHOPPING CART ---");
            System.out.println("1. View Cart");
            System.out.println("2. Add Product to Cart");
            System.out.println("3. Update Item Quantity");
            System.out.println("4. Remove Item from Cart");
            System.out.println("5. Clear Cart");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    viewCart();
                    break;
                case 2:
                    addToCart();
                    break;
                case 3:
                    updateCartItemQuantity();
                    break;
                case 4:
                    removeFromCart();
                    break;
                case 5:
                    clearCart();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewCart() {
        long customerId = readLong("Enter Customer ID: ");
        try {
            Cart cart = cartService.getCart(customerId);
            printCartDetails(cart);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addToCart() {
        long customerId = readLong("Enter Customer ID: ");
        long productId = readLong("Enter Product ID: ");
        int quantity = readInt("Enter quantity to add: ");

        try {
            cartService.addToCart(customerId, productId, quantity);
            System.out.println("Success: Product added to cart.");
            Cart cart = cartService.getCart(customerId);
            printCartDetails(cart);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateCartItemQuantity() {
        long customerId = readLong("Enter Customer ID: ");
        long productId = readLong("Enter Product ID to update quantity: ");
        int quantity = readInt("Enter new quantity (0 to remove): ");

        try {
            cartService.updateItemQuantity(customerId, productId, quantity);
            System.out.println("Success: Cart item quantity updated.");
            Cart cart = cartService.getCart(customerId);
            printCartDetails(cart);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeFromCart() {
        long customerId = readLong("Enter Customer ID: ");
        long productId = readLong("Enter Product ID to remove: ");

        try {
            cartService.removeFromCart(customerId, productId);
            System.out.println("Success: Product removed from cart.");
            Cart cart = cartService.getCart(customerId);
            printCartDetails(cart);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void clearCart() {
        long customerId = readLong("Enter Customer ID: ");
        try {
            cartService.clearCart(customerId);
            System.out.println("Success: Cart has been cleared.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleCheckout() {
        System.out.println();
        System.out.println("--- PLACE ORDER (CHECKOUT) ---");
        long customerId = readLong("Enter Customer ID: ");

        try {
            Customer customer = customerService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Error: Customer not found with ID #" + customerId);
                return;
            }

            Cart cart = cartService.getCart(customerId);
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                System.out.println("Error: Cannot place order because the shopping cart is empty.");
                return;
            }

            printCartDetails(cart);

            String confirm = readString("Confirm and place order? (y/n): ");
            if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
                Order order = orderService.checkout(customerId);
                System.out.println();
                System.out.println("==================================================");
                System.out.println("           ORDER PLACED SUCCESSFULLY!");
                System.out.println("==================================================");
                printOrderReceipt(order);
            } else {
                System.out.println("Checkout cancelled.");
            }
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
    }

    private static void handleOrderMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- ORDER MANAGEMENT ---");
            System.out.println("1. View Order Details");
            System.out.println("2. View Customer Order History");
            System.out.println("3. Cancel Order");
            System.out.println("4. View All Orders");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    viewOrderDetails();
                    break;
                case 2:
                    viewCustomerOrderHistory();
                    break;
                case 3:
                    cancelOrder();
                    break;
                case 4:
                    viewAllOrders();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewOrderDetails() {
        long orderId = readLong("Enter Order ID: ");
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                System.out.println("Error: Order not found with ID #" + orderId);
                return;
            }
            printOrderReceipt(order);
        } catch (Exception e) {
            System.out.println("Error fetching order: " + e.getMessage());
        }
    }

    private static void viewCustomerOrderHistory() {
        long customerId = readLong("Enter Customer ID: ");
        try {
            Customer customer = customerService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Error: Customer not found with ID #" + customerId);
                return;
            }
            List<Order> orders = orderService.getOrdersByCustomerId(customerId);
            System.out.println();
            System.out.println("Order History for " + customer.getName() + " (ID #" + customer.getId() + "):");
            printOrdersTable(orders);
        } catch (Exception e) {
            System.out.println("Error fetching order history: " + e.getMessage());
        }
    }

    private static void cancelOrder() {
        long orderId = readLong("Enter Order ID to cancel: ");
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                System.out.println("Error: Order not found with ID #" + orderId);
                return;
            }

            System.out.println("Order #" + order.getId() + " Current Status: " + order.getStatus() + ", Total: $" + order.getTotalAmount());
            String confirm = readString("Are you sure you want to cancel this order? Stock will be restored. (y/n): ");
            if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
                orderService.cancelOrder(orderId);
                System.out.println("Success: Order #" + orderId + " has been cancelled and stock has been restored.");
            } else {
                System.out.println("Cancellation aborted.");
            }
        } catch (Exception e) {
            System.out.println("Error cancelling order: " + e.getMessage());
        }
    }

    private static void viewAllOrders() {
        System.out.println();
        System.out.println("-- All Orders --");
        try {
            List<Order> orders = orderService.getAllOrders();
            printOrdersTable(orders);
        } catch (Exception e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }

    private static void handleInventoryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- INVENTORY MANAGEMENT ---");
            System.out.println("1. View Low-Stock Products");
            System.out.println("2. Update Product Stock (Restock)");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    viewLowStockProducts();
                    break;
                case 2:
                    restockProduct();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewLowStockProducts() {
        int threshold = readInt("Enter low-stock threshold (e.g. 5): ");
        try {
            List<Product> products = productService.getLowStockProducts(threshold);
            System.out.println();
            System.out.println("-- Products with Stock <= " + threshold + " --");
            printProductsTable(products);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void restockProduct() {
        long productId = readLong("Enter Product ID to restock: ");
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                System.out.println("Error: Product not found with ID #" + productId);
                return;
            }
            System.out.println("Product: " + product.getName() + " | Current Stock: " + product.getStockQuantity());
            int newStock = readInt("Enter updated total stock: ");
            productService.updateStock(productId, newStock);
            System.out.println("Success: Stock for '" + product.getName() + "' updated to " + newStock + ".");
        } catch (Exception e) {
            System.out.println("Error updating stock: " + e.getMessage());
        }
    }

    private static void handleReportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- SYSTEM REPORTS ---");
            System.out.println("1. Overview Dashboard");
            System.out.println("2. Low-Stock Alert Report");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    viewDashboardReport();
                    break;
                case 2:
                    viewLowStockProducts();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewDashboardReport() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("             SYSTEM OVERVIEW DASHBOARD");
        System.out.println("==================================================");
        try {
            long totalProducts = productService.getTotalProductsCount();
            long totalCustomers = customerService.getTotalCustomersCount();
            long totalOrders = orderService.getTotalOrdersCount();
            BigDecimal totalSales = orderService.getTotalSales();

            System.out.printf("%-25s: %d%n", "Total Products Cataloged", totalProducts);
            System.out.printf("%-25s: %d%n", "Total Registered Customers", totalCustomers);
            System.out.printf("%-25s: %d%n", "Total Orders Placed", totalOrders);
            System.out.printf("%-25s: $%.2f%n", "Total Sales Revenue", totalSales);
            System.out.println("==================================================");
        } catch (Exception e) {
            System.out.println("Error generating dashboard: " + e.getMessage());
        }
    }

    private static void printProductsTable(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-6s %-26s %-18s %-12s %-8s%n", "ID", "Name", "Category", "Price", "Stock");
        System.out.println("--------------------------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-6d %-26s %-18s $%-11.2f %-8d%n",
                    p.getId(),
                    truncate(p.getName(), 25),
                    truncate(p.getCategory(), 17),
                    p.getPrice(),
                    p.getStockQuantity());
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void printCustomersTable(List<Customer> customers) {
        if (customers == null || customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.printf("%-6s %-20s %-28s %-16s %-24s%n", "ID", "Name", "Email", "Phone", "Address");
        System.out.println("------------------------------------------------------------------------------------------------------");
        for (Customer c : customers) {
            System.out.printf("%-6d %-20s %-28s %-16s %-24s%n",
                    c.getId(),
                    truncate(c.getName(), 19),
                    truncate(c.getEmail(), 27),
                    truncate(c.getPhone(), 15),
                    truncate(c.getAddress(), 23));
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
    }

    private static void printCartDetails(Cart cart) {
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Customer: " + cart.getCustomer().getName() + " (ID #" + cart.getCustomer().getId() + ")");
        System.out.println("-----------------------------------------------------------------------------");
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            System.out.println("Shopping cart is empty.");
            System.out.println("-----------------------------------------------------------------------------");
            return;
        }

        System.out.printf("%-6s %-26s %-12s %-10s %-12s%n", "PID", "Product Name", "Unit Price", "Quantity", "Subtotal");
        System.out.println("-----------------------------------------------------------------------------");
        for (CartItem item : cart.getItems()) {
            System.out.printf("%-6d %-26s $%-11.2f %-10d $%-11.2f%n",
                    item.getProduct().getId(),
                    truncate(item.getProduct().getName(), 25),
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getSubtotal());
        }
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%-56s $%.2f%n", "TOTAL CART AMOUNT:", cart.calculateTotal());
        System.out.println("-----------------------------------------------------------------------------");
    }

    private static void printOrdersTable(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-22s %-14s %-14s%n", "Order ID", "Customer", "Date", "Status", "Total Amount");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Order o : orders) {
            String customerName = o.getCustomer() != null ? o.getCustomer().getName() : "N/A";
            String dateStr = o.getOrderDate() != null ? o.getOrderDate().format(dateFormatter) : "N/A";
            System.out.printf("%-8d %-20s %-22s %-14s $%-13.2f%n",
                    o.getId(),
                    truncate(customerName, 19),
                    dateStr,
                    o.getStatus(),
                    o.getTotalAmount());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private static void printOrderReceipt(Order order) {
        System.out.println();
        System.out.println("=============================================================================");
        System.out.println("                             ORDER RECEIPT");
        System.out.println("=============================================================================");
        System.out.println("Order ID   : #" + order.getId());
        System.out.println("Customer   : " + (order.getCustomer() != null ? order.getCustomer().getName() : "N/A"));
        System.out.println("Date       : " + (order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "N/A"));
        System.out.println("Status     : " + order.getStatus());
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%-6s %-28s %-12s %-10s %-12s%n", "PID", "Product Name", "Unit Price", "Quantity", "Subtotal");
        System.out.println("-----------------------------------------------------------------------------");
        if (order.getItems() != null) {
            for (OrderItem oi : order.getItems()) {
                String pName = oi.getProduct() != null ? oi.getProduct().getName() : "Unknown";
                Long pid = oi.getProduct() != null ? oi.getProduct().getId() : 0L;
                System.out.printf("%-6d %-28s $%-11.2f %-10d $%-11.2f%n",
                        pid,
                        truncate(pName, 27),
                        oi.getUnitPrice(),
                        oi.getQuantity(),
                        oi.getSubtotal());
            }
        }
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%-58s $%.2f%n", "FINAL TOTAL:", order.getTotalAmount());
        System.out.println("=============================================================================");
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen - 3) + "...";
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer ID.");
            }
        }
    }

    private static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                BigDecimal val = new BigDecimal(input);
                if (val.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Value cannot be negative.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal number. Please enter a valid price/amount.");
            }
        }
    }

    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please enter a value.");
        }
    }

    private static String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private static BigDecimal readOptionalBigDecimal(String prompt, BigDecimal defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                BigDecimal val = new BigDecimal(input);
                if (val.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Value cannot be negative.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal number.");
            }
        }
    }

    private static int readOptionalInt(String prompt, int defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer.");
            }
        }
    }
}
