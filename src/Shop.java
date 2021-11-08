import java.util.ArrayList;

public class Shop{

    public Shop(String name, int amount_of_one_product){
        this.name = name;
        this.amount_of_one_product = amount_of_one_product;

        for(int i = 0; i < 8; i++){
            this.products.add(new Product(i + 1, (int)(i * 274.35 + 80), (int)(i * 274.35 + 80), amount_of_one_product));
            this.cost_of_credit += this.products.get(i).amount * this.products.get(i).price / 120 * 2; 
            System.out.println(this.name + " | Produkt: " + this.products.get(i).name + " | Cena: " + this.products.get(i).price + " | Ilość: " + this.products.get(i).amount + " | Wątek: " + Thread.currentThread().getId());
        }
    }

    String name;
    int amount_of_one_product;
    int cost_of_credit = 0;
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Order> orders = new ArrayList<>();

}