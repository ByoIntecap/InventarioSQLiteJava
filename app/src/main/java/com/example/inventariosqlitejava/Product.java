package com.example.inventariosqlitejava;

public class Product {
    private long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;

    // Constructor por defecto (necesario para algunos frameworks como Room o JSON)
    public Product() {
    }

    // Constructor completo con todos los campos (incluyendo ID)
    public Product(long id, String nombre, String descripcion, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Constructor sin ID (útil para crear productos nuevos)
    public Product(String nombre, String descripcion, double precio, int cantidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
        this.id = 0; // Asigna 0 por defecto (no existe en DB aún)
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}