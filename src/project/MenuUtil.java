package project;

import java.util.Scanner;

public class MenuUtil {
    public static void displayMenu(String[] menuOptions, String menuTitle){
        System.out.println(menuTitle);
        System.out.println("Please choose from one of the following options:");
        for (String option: menuOptions){
            System.out.println(option);
        }
    }

    public static int getMenuChoice(int numOptions) {
        Scanner keyboard = new Scanner(System.in);
        int choice = keyboard.nextInt();
        while (choice < 1 || choice > numOptions){
            System.out.printf("Please enter a valid options (1 - %d)\n", numOptions);
            choice = keyboard.nextInt();
        }
        return choice;
    }
}