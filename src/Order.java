public class Order {

    Order(String user_name, int amount, double price, int shop_id, int product_id, int rating, int day){
        this.user_name = user_name;
        this.amount = amount;
        this.shop_id = shop_id;
        this.product_id = product_id;
        this.rating = rating;
        this.day = day;
        this.price = price;
    }

    String user_name;
    int amount, shop_id, product_id, rating, day;
    double price;
}
