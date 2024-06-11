package pl.project.oop2.shoppinglistgui;

public class Product {

    private int id;
    private final String name;
    private final double quantity;
    private final String unit;
    private final boolean isInteger;

    public Product(int id, String name, double quantity, String unit, boolean isInteger) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.isInteger = isInteger;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public boolean getIsInteger() {
        return isInteger;
    }

    public String toString() {
        return id + " " + name + " " + quantity + " " + unit + " " + isInteger;
    }

}
