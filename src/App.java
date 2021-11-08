import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class App {

    public static ArrayList<Shop> shops = new ArrayList<>();
    public static void main(String[] args) throws Exception {

        // ----- ----- ZAŁOŻENIA ----- -----
        // Dla uproszczenia miesiąc = 30 dni
        // Utrzymywanie synchronizacji wątków na poziomie 1 dnia
        // Brak towaru = użytkownik ponawia zakup w pierwszy dzień kolejnego miesiąca
        // Koszt kredytu jest obliczany na podstawie produktów znajdujących się na początku miesiąca w sklepie (po dostawie)

        shops.add(new Shop("Shop_1", 500));
        shops.add(new Shop("Shop_2", 300));
        shops.add(new Shop("Shop_3", 100));

        ArrayList<Client> clients = new ArrayList<>();

        while(clients.size() != 1000){
            double age = new Random().nextGaussian()*20+65;
            if(20 <= age && age <= 100){
                clients.add(new Client(clients.size() + 1, age));
            }
        }

        Collections.sort(clients);

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for(int i=0; i<360; i++){
            List<Simulation> shopTasks = new ArrayList<>();
            List<Integer> shopTasktsClientsIndexes = new ArrayList<>();

            for(int a=0; a<clients.size(); a++){
                if(clients.get(a).day_of_next_buy == i){
                    shopTasktsClientsIndexes.add(a);
                    shopTasks.add(new Simulation(clients.get(a)));
                }else if(clients.get(a).day_of_next_buy > i) break;
            }

            List<Future<Client>> tasksResponds = executorService.invokeAll(shopTasks);

            Collections.sort(shopTasktsClientsIndexes);
            Collections.reverse(shopTasktsClientsIndexes);

            for(int a=0; a<tasksResponds.size(); a++){
                clients.remove((int)shopTasktsClientsIndexes.get(a));
                clients.add(tasksResponds.get(a).get());
            }

            Collections.sort(clients);

            if((i + 1) % 30 == 0) endOfMonthActions((i + 1) / 30);
        }

        executorService.shutdown();
    
    }

    private static void endOfMonthActions(int month){
        for(int i=0; i<shops.size(); i++){
            System.out.println("----- ----- RAPORT MIESIĄC " + month + " DLA Sklep_" + (i+1) + " ----- -----");

            double rating = 0;
            double income = 0;
            double margin = 0;
            int orders_number = 0;

            for(int a=0; a<shops.get(i).orders.size(); a++){
                rating += shops.get(i).orders.get(a).rating;
                income += shops.get(i).orders.get(a).price * shops.get(i).orders.get(a).amount;
                margin += (shops.get(i).orders.get(a).price - (shops.get(i).products.get(shops.get(i).orders.get(a).product_id).original_price / 120 * 100)) * shops.get(i).orders.get(a).amount;
                orders_number++;
            }
            
            System.out.println("Ocena sklepu: " + Math.floor((rating / shops.get(i).orders.size()) * 100) / 100 + " na podstawie " + orders_number + " zamówienie.");
            System.out.println("Księgowość do tego momentu wyniosi w przybliżeniu:");
            System.out.println("Przychód: " + Math.floor((income * 100) / 100) + " | " + Math.floor(((income / orders_number) * 100) / 100) + "/zamówienie.");
            System.out.println("Koszt kredytu: " + shops.get(i).cost_of_credit + " | " + Math.floor(((shops.get(i).cost_of_credit / orders_number) * 100) / 100) + "/zamówienie.");
            System.out.println("Marża: " + Math.floor((margin * 100) / 100) + " | " + Math.floor(((margin / orders_number) * 100) / 100) + "/zamówienie.");
            System.out.println("Zysk: " + Math.floor(((margin - shops.get(i).cost_of_credit) * 100) / 100) + " | " + Math.floor((((margin - shops.get(i).cost_of_credit) / orders_number) * 100) / 100) + "/zamówienie.");
            
            for(int a=0; a<shops.get(i).products.size(); a++){
                System.out.println("Sklep_" + (i + 1) + " " + shops.get(i).products.get(a).name + " ilość: " + shops.get(i).products.get(a).amount);

                if(shops.get(i).products.get(a).amount > shops.get(i).amount_of_one_product){
                    if(shops.get(i).products.get(a).price == shops.get(i).products.get(a).original_price) shops.get(i).products.get(a).price = Math.floor((shops.get(i).products.get(a).price / 120 * 102) * 100) / 100;
                }else shops.get(i).products.get(a).price = shops.get(i).products.get(a).original_price;

                shops.get(i).products.get(a).amount += shops.get(i).amount_of_one_product;
                shops.get(i).cost_of_credit += shops.get(i).products.get(a).amount * shops.get(i).products.get(a).original_price / 120 * 2;
            }
        }
    }
}