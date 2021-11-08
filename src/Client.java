import java.util.Random;
import java.util.ArrayList;

public class Client implements Comparable<Client>{

    public Client(int number_in_name, double age){
        this.name = "Klient_" + number_in_name;
        this.age = age;
        this.getProductIndex = getProductIndex(age);
        this.day_of_next_buy += daysToNextBuy(age);
    }

    String name;
    double age;
    int getProductIndex;
    int waitingRating = 5;
    public Integer day_of_next_buy = 0;
    ArrayList<Order> orders = new ArrayList<>();

    @Override
    public int compareTo(Client o) {
        return this.day_of_next_buy.compareTo(o.day_of_next_buy);
    }
    
    int getProductIndex(double age){
        if(20 < age && age < 30) return 0;
        else if(30 < age && age < 40) return 1;
        else if(40 < age && age < 50) return 2;
        else if(50 < age && age < 60) return 3;
        else if(60 < age && age < 70) return 4;
        else if(70 < age && age < 80) return 5;
        else if(80 < age && age < 90) return 6;
        else return 7;
    }

    public static int daysToNextBuy(double age){
        Random rand = new Random();
        double lambda;
        if(20 < age && age < 30) lambda = 20;
        else if(30 < age && age < 40) lambda = 25;
        else if(40 < age && age < 50) lambda = 30;
        else if(50 < age && age < 60) lambda = 35;
        else if(60 < age && age < 70) lambda = 40;
        else if(70 < age && age < 80) lambda = 45;
        else if(80 < age && age < 90) lambda = 50;
        else lambda = 60;
        return (int)(-lambda*Math.log(1-rand.nextDouble()));
    }

    public static int howManyProductToBuy(double age){
        double lambda;
        if(20 < age && age < 30) lambda = 15;
        else if(30 < age && age < 40) lambda = 13;
        else if(40 < age && age < 50) lambda = 11;
        else if(50 < age && age < 60) lambda = 10;
        else if(60 < age && age < 70) lambda = 9;
        else if(70 < age && age < 80) lambda = 7;
        else if(80 < age && age < 90) lambda = 5;
        else lambda = 4;
        Random rand = new Random();
        double l = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;
        do {
            k++;
            p *= rand.nextDouble();
        } while (p > l);
        return k - 1;
    }

}