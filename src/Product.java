public class Product {

    public Product(int number_in_name, int price, int original_price, int amount){
        this.name = "Produkt_" + number_in_name;
        this.price = price;
        this.original_price = original_price;
        this.amount = amount;
    }

    String name;
    double price, original_price;
    int amount;

}