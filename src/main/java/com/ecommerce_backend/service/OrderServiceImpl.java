package com.ecommerce_backend.service;
import com.ecommerce_backend.dto.AdminOrderDashboardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ecommerce_backend.dto.OrderItemResponse;
import com.ecommerce_backend.dto.OrderResponse;
import com.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce_backend.model.*;
import org.springframework.data.domain.*;
import com.ecommerce_backend.repository.*;
import com.ecommerce_backend.util.OrderStatusValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderStatusHistoryRepository historyRepo;

    public OrderServiceImpl(
            OrderRepository orderRepo,
            CartRepository cartRepo,
            UserRepository userRepo,
            ProductRepository productRepo,
            OrderStatusHistoryRepository historyRepo
    ) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.historyRepo = historyRepo;
    }

    // ================= PLACE ORDER =================
    @Override
    public void placeOrder(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);

        double total = 0;

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPriceAtPurchase(ci.getProduct().getPrice());
            order.getItems().add(oi);

            total += ci.getQuantity() * ci.getProduct().getPrice();
        }

        order.setTotalAmount(total);
        orderRepo.save(order);

        saveHistory(order, null, OrderStatus.CREATED, email);

        cart.getItems().clear();
        cartRepo.save(cart);
    }

    // ================= GET ORDERS =================
    @Override
    public List<OrderResponse> getOrders(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepo.findByUser(user).stream().map(order -> {
            OrderResponse dto = new OrderResponse();
            dto.setOrderId(order.getId());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setStatus(order.getStatus().name());

            List<OrderItemResponse> items = order.getItems().stream().map(item -> {
                OrderItemResponse i = new OrderItemResponse();
                i.setProductId(item.getProduct().getId());
                i.setProductName(item.getProduct().getName());
                i.setPrice(item.getPriceAtPurchase());
                i.setQuantity(item.getQuantity());
                return i;
            }).toList();

            dto.setItems(items);
            return dto;
        }).toList();
    }

    // ================= UPDATE STATUS (ADMIN) =================
    @Override
    public void updateOrderStatus(Long id, OrderStatus newStatus, String updatedBy) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus current = order.getStatus();

        if (!OrderStatusValidator.isValidTransition(current, newStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " → " + newStatus
            );
        }

        // Deduct stock when shipping
        if (current == OrderStatus.CONFIRMED && newStatus == OrderStatus.SHIPPED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();

                if (product.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalStateException(
                            "Insufficient stock for " + product.getName()
                    );
                }

                product.setStockQuantity(
                        product.getStockQuantity() - item.getQuantity()
                );
                productRepo.save(product);
            }
        }

        order.setStatus(newStatus);
        orderRepo.save(order);

        saveHistory(order, current, newStatus, updatedBy);
    }

    // ================= CANCEL ORDER =================
    @Override
    public void cancelOrder(Long orderId, String email) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(email)) {
            throw new SecurityException("Unauthorized cancellation attempt");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Order cannot be cancelled");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );
            productRepo.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);

        saveHistory(order, OrderStatus.CREATED, OrderStatus.CANCELLED, email);
    }

    // ================= HISTORY =================
    @Override
    public List<OrderStatusHistory> getOrderHistory(
            Long orderId,
            String email,
            boolean isAdmin
    ) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 🔐 USER can only view their own order
        if (!isAdmin && !order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        return historyRepo.findByOrderIdOrderByChangedAtAsc(orderId);
    }



    // ================= PRIVATE =================
    private void saveHistory(
            Order order,
            OrderStatus from,
            OrderStatus to,
            String by
    ) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.setOrder(order);
        h.setFromStatus(from);
        h.setToStatus(to);
        h.setChangedBy(by);
        h.setChangedAt(LocalDateTime.now());
        historyRepo.save(h);
    }


    @Override
    public Page<OrderResponse> getAllOrders(
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return orderRepo.findAll(pageable)
                .map(order -> {
                    OrderResponse dto = new OrderResponse();
                    dto.setOrderId(order.getId());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setStatus(order.getStatus().name());
                    return dto;
                });
    }


    @Override
    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found")
                );
    }

    @Override
    public AdminOrderDashboardResponse getDashboardCounts() {

        AdminOrderDashboardResponse response = new AdminOrderDashboardResponse();

        response.setTotal(orderRepo.countAllOrders());
        response.setPlaced(orderRepo.countByStatus(OrderStatus.CREATED));
        response.setConfirmed(orderRepo.countByStatus(OrderStatus.CONFIRMED));
        response.setShipped(orderRepo.countByStatus(OrderStatus.SHIPPED));
        response.setDelivered(orderRepo.countByStatus(OrderStatus.DELIVERED));
        response.setCancelled(orderRepo.countByStatus(OrderStatus.CANCELLED));

        return response;
    }

}
