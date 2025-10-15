package com.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Category {

    private static final Map<String, Category> CACHE = new ConcurrentHashMap<>();
    private final String name;

    private Category(String name) {
        this.name = name;
    }

    public static Category of(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Category name can't be null");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Category name can't be blank");
        }
        String normalized = trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
        return CACHE.computeIfAbsent(normalized, Category::new);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Category{" + "name='" + name + '\'' + '}';
    }
}
