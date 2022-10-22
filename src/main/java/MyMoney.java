package Service.src.main.java;


import Service.src.main.java.Service.PortfolioService;

import java.io.IOException;
import java.util.Scanner;

public class MyMoney {
    public static void main(String[] str) throws IOException {

//        File file = new File("/Users/abhay.gupta1/Downloads/portfolio.txt");
//        String path = "/Users/abhay.gupta1/Downloads/portfolio.txt";
        Scanner sc = new Scanner(System.in);
//        System.out.print("Please Enter File Path Here: ");
        PortfolioService.processRequest(sc.next());
    }
}
