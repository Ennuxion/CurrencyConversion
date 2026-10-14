package org.example;

import java.util.Scanner;
import static org.example.Cash.metals;


public class Metal_Trade {

    public static void buy_Metal(Scanner in) {

        System.out.println("Введите сумму, на которую хотите купить металл:");

        Cash.Metal latest = metals.values().iterator().next();
        double sum = in.nextDouble();
        double goldRub = (sum / latest.gold());
        double silverRub = (sum / latest.silver());
        double platinumRub = (sum / latest.platinum());
        double palladiumRub = (sum / latest.palladium());
        System.out.println("На эти деньги можно приобрести: ");
        System.out.printf(" Золото - %.3f грамм\n Серебро - %.3f грамм\n Платина - %.3f грамм\n Палладий - %.3f грамм\n", goldRub, silverRub, platinumRub, palladiumRub);
    }


    public static void sell_metal(Scanner in) {
        System.out.println("Выберите металл, который вы хотите продать:");
        System.out.println("1. Золото");
        System.out.println("2. Серебро");
        System.out.println("3. Платина");
        System.out.println("4. Палладий");
        Cash.Metal latest = metals.values().iterator().next();
        int choiceMet = in.nextInt();
        switch (choiceMet) {
            case 1:
                System.out.println("Введите количество (в граммах): ");
                double goldSum = in.nextDouble();
                double rubGold = goldSum  * latest.gold();
                System.out.printf("Вы получите: %.3f \n", rubGold);
                break;

            case 2:
                System.out.println("Введите количество (в граммах): ");
                double silverSum = in.nextDouble();
                double rubSilver = silverSum  * latest.silver();
                System.out.printf("Вы получите: %.3f \n", rubSilver);
                break;

            case 3:
                System.out.println("Введите количество (в граммах): ");
                double platinumSum = in.nextDouble();
                double rubPlatinum = platinumSum * latest.platinum();
                System.out.printf("Вы получите: %.3f \n", rubPlatinum);
                break;

            case 4:
                System.out.println("Введите количество (в граммах): ");
                double palladiumSum = in.nextDouble();
                double rubPalladium = palladiumSum * latest.palladium();
                System.out.printf("Вы получите: %.3f \n", rubPalladium);
                break;
        }
    }
}
