package me.loki2302.jdbc;

import java.util.List;

public class Page<T> {
    public int TotalItems;
    public int TotalPages;
    public int CurrentPage;
    public List<T> Items;
}