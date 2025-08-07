package dto;

public class Item {
    public int id;
    public String name;
    public String quantity;
    public Double price;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity='" + quantity + '\'' +
                ", price=" + price +
                '}';
    }
}
