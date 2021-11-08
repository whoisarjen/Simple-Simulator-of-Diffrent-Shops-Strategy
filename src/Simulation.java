import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation implements Callable<Client>{

    public Simulation(Client client){
        this.client = client;
    }

    private Client client;

    @Override
    public Client call() throws Exception {
        int index_of_shop = getShopToBuyFrom(client);
        int how_many_products_to_buy = Client.howManyProductToBuy(client.age);

        // Sprawdzanie możliwości zakupienia w dniu złożenia zamówienia
        if(App.shops.get(index_of_shop).products.get(client.getProductIndex).amount - how_many_products_to_buy >= 0){
            App.shops.get(index_of_shop).products.get(client.getProductIndex).amount -= how_many_products_to_buy;

            // Wstawianie zamówienia do sklepu i profilu użytkownika
            App.shops.get(index_of_shop).orders.add(new Order(client.name, how_many_products_to_buy, App.shops.get(index_of_shop).products.get(client.getProductIndex).price, index_of_shop, client.getProductIndex, client.waitingRating, client.day_of_next_buy));
            client.orders.add(new Order(client.name, how_many_products_to_buy, App.shops.get(index_of_shop).products.get(client.getProductIndex).price, index_of_shop, client.getProductIndex, client.waitingRating, client.day_of_next_buy));
            client.waitingRating = 5;

            // Losowanie kolejnego dnia zakupu
            client.day_of_next_buy += Client.daysToNextBuy(client.age);
        }else{
            // Oszacowanie dni do odczekania
            int current_day_of_order = client.day_of_next_buy;
            do{
                current_day_of_order -= 30;
            }while(current_day_of_order > 0);

            // Nowy dzień zamówienia
            client.day_of_next_buy += current_day_of_order * -1;

            // Oszacowywanie przyszłej oceny
            if(current_day_of_order * -1 + 1 <= 7) client.waitingRating -= 1;
            else if(current_day_of_order * -1 + 1 <= 14) client.waitingRating -= 2;
            else if(current_day_of_order * -1 + 1 <= 30) client.waitingRating -= 3;
            else client.waitingRating -= 4;

            // Blokada zejścia poniżej oceny 1 dla kilkukrotnej próby zamówienia
            if(client.waitingRating < 1) client.waitingRating = 1;
        }

        System.out.println("Thread:" + Thread.currentThread().getId() + " | Left in " + App.shops.get(index_of_shop).name + " => " + App.shops.get(index_of_shop).products.get(client.getProductIndex).name + ": " + App.shops.get(index_of_shop).products.get(client.getProductIndex).amount);

        return client;
    }
    
    public static int getShopToBuyFrom(Client client){
        if(client.day_of_next_buy >= 30){

            // Losowanie na bazie ocen itp.
            // Każdy sklep zbiera punkty za poszczególne zmienne
            // Następnie punkty są zamieniane na prawdopodobieństwo
            // Losowanie zwycięzkiego sklepu

            List<Integer> shopsChances = new ArrayList<>();
            shopsChances.add(0);
            shopsChances.add(0);
            shopsChances.add(0);

            // Promocja cenowa
            if(App.shops.get(0).products.get(client.getProductIndex).price < App.shops.get(1).products.get(client.getProductIndex).price
            || App.shops.get(0).products.get(client.getProductIndex).price < App.shops.get(2).products.get(client.getProductIndex).price){
                shopsChances.set(0, 150);
            }
            if(App.shops.get(1).products.get(client.getProductIndex).price < App.shops.get(2).products.get(client.getProductIndex).price
            || App.shops.get(1).products.get(client.getProductIndex).price < App.shops.get(0).products.get(client.getProductIndex).price){
                shopsChances.set(1, 150);
            }
            
            if(App.shops.get(2).products.get(client.getProductIndex).price < App.shops.get(1).products.get(client.getProductIndex).price
            || App.shops.get(2).products.get(client.getProductIndex).price < App.shops.get(0).products.get(client.getProductIndex).price){
                shopsChances.set(2, 150);
            }

            // Aktualna średnia ocena sklepu przez klientów
            for(int a=0; a<App.shops.size(); a++){
                int rating = 0;
                int orders = 0;
                for(int i =0; i<App.shops.get(a).orders.size(); i++){
                    orders++;
                    rating += App.shops.get(a).orders.get(i).rating;
                }
                shopsChances.set(a, (int)(shopsChances.get(a) + (rating / orders * 10)));
            }

            // Stopień zadowolonia z poprzednich zakupów
            if(client.orders.size() > 0){
                for(Order o : client.orders){
                    if(o.rating == 1) shopsChances.set(o.shop_id, (int)(shopsChances.get(o.shop_id) * 0));
                    if(o.rating == 2) shopsChances.set(o.shop_id, (int)(shopsChances.get(o.shop_id) * 0.5));
                    if(o.rating == 3) shopsChances.set(o.shop_id, (int)(shopsChances.get(o.shop_id) * 0));
                    if(o.rating == 4) shopsChances.set(o.shop_id, (int)(shopsChances.get(o.shop_id) * 1.25));
                    if(o.rating == 5) shopsChances.set(o.shop_id, (int)(shopsChances.get(o.shop_id) * 1.5));

                    // oraz skłonności do zmian na bazie wieku
                    if(o.shop_id == 0){
                        shopsChances.set(1, (int)(shopsChances.get(1) + Client.howManyProductToBuy(client.age)));
                        shopsChances.set(2, (int)(shopsChances.get(2) + Client.howManyProductToBuy(client.age)));
                    }
                    if(o.shop_id == 1){
                        shopsChances.set(0, (int)(shopsChances.get(0) + Client.howManyProductToBuy(client.age)));
                        shopsChances.set(2, (int)(shopsChances.get(2) + Client.howManyProductToBuy(client.age)));
                    }
                    if(o.shop_id == 2){
                        shopsChances.set(1, (int)(shopsChances.get(1) + Client.howManyProductToBuy(client.age)));
                        shopsChances.set(0, (int)(shopsChances.get(0) + Client.howManyProductToBuy(client.age)));
                    }
                }
            }

            int random = (int) ((Math.random() * ((shopsChances.get(0) + shopsChances.get(1) + shopsChances.get(2)) - 0)) + 0);
            if(random <= shopsChances.get(0)) return 0;
            else if(random <= shopsChances.get(0) + shopsChances.get(1)) return 1;
            else return 2;

        }else return new Random().nextInt(2 - 0 + 1) + 0;
    }

}
