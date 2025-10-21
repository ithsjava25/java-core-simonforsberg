package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Warehouse {
    private static final Map<String, Warehouse> INSTANCES = new HashMap<>();
    private final Map<Category, List<Product>> productsByCategory = new HashMap<>();
    private final Set<UUID> changedProducts = new HashSet<>();
    private final String name;

    private Warehouse(String name) {
        this.name = name;
    }

    public static Warehouse getInstance(String name) {
        return INSTANCES.computeIfAbsent(name, Warehouse::new);
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        productsByCategory.computeIfAbsent(product.category(), c -> new ArrayList<>()).add(product);
    }

    public List<Product> getProducts() {
        return productsByCategory.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public Optional<Product> getProductById(UUID id) {
        return productsByCategory.values().stream()
                .flatMap(Collection::stream)
                .filter(p -> p.uuid().equals(id))
                .findFirst();
    }

    public void updateProductPrice(UUID id, BigDecimal newPrice) {
        Product product = getProductById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        BigDecimal roundedPrice = newPrice.setScale(2, RoundingMode.HALF_UP);
        product.price(roundedPrice);
        changedProducts.add(id);
    }

    public List<Product> getChangedProducts() {
        return getProducts().stream()
                .filter(p -> changedProducts.contains(p.uuid()))
                .toList();
    }

    public List<Perishable> expiredProducts() {
        return getProducts().stream()
                .filter(p -> p instanceof Perishable)
                .map(p -> (Perishable) p)
                .filter(Perishable::isExpired)
                .toList();
    }

    public List<Shippable> shippableProducts() {
        return getProducts().stream()
                .filter(p -> p instanceof Shippable)
                .map(p -> (Shippable) p)
                .toList();
    }

    public void remove(UUID id) {
        productsByCategory.values().forEach(list -> list.removeIf(p -> p.uuid().equals(id)));
        productsByCategory.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public Map<Category, List<Product>> getProductsGroupedByCategories() {
        return productsByCategory.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> List.copyOf(e.getValue())
                ));
    }

    public void clearProducts() {
        productsByCategory.clear();
        changedProducts.clear();
    }

    public boolean isEmpty() {
        return getProducts().isEmpty();
    }
}
