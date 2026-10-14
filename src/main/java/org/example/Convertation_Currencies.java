package org.example;

import java.util.Comparator;
import java.util.Scanner;
import static org.example.Cash.*;

public class Convertation_Currencies {


    public static void convert_to_RUB(Scanner in) {
        System.out.println("\nДоступные валюты:");
        currencies.values().stream()
                .sorted(Comparator.comparing(Cash.Currency::code))
                .forEach(c -> System.out.printf("%03d - %s(%s) (1 %s = %.4f руб.)%n",
                        c.numb(), c.code(), c.name(), c.code(), c.getRatePerUnit()));

        System.out.print("\nВведите код валюты (например, 840 для USD): ");
        int numb = in.nextInt();

        if (!currCode.containsKey(numb)) {
            System.out.println("Валюта не найдена!");
            return;
        }

        Cash.Currency currency = currCode.get(numb);
        System.out.printf("Введите сумму в %s: ", currency.code());
        double amount = in.nextDouble();

        double result = amount * currency.getRatePerUnit();
        System.out.printf("%.2f %s(%03d) = %.2f RUB%n", amount, currency.code(),currency.numb(), result);
    }

    public static void to_convert_RUB(Scanner in) {
        System.out.println("\nДоступные валюты:");
        currencies.values().stream()
                .sorted(Comparator.comparing(Cash.Currency::code))
                .forEach(c -> System.out.printf("%03d - %s(%s) (1 %s = %.4f руб.)%n",
                        c.numb(), c.code(), c.name(), c.code(), c.getRatePerUnit()));

        System.out.print("\nВведите цифровой код валюты (например, 978 для EUR): ");
        Integer numb = in.nextInt();

        if (!currCode.containsKey(numb)) {
            System.out.println("Валюта не найдена!");
            return;
        }

        Cash.Currency currency = currCode.get(numb);
        System.out.print("Введите сумму в RUB: ");
        double amount = in.nextDouble();

        double result = amount / currency.getRatePerUnit();
        System.out.printf("%.2f RUB = %.2f %s%n", amount, result, currency.code());
    }


    public static void convert_currencies(Scanner in) {
        System.out.println("\nДоступные валюты:");
        currencies.values().stream()
                .sorted(Comparator.comparing(Cash.Currency::code))
                .forEach(c -> System.out.printf("%03d - %s(%s) (1 %s = %.4f руб.)%n",
                        c.numb(), c.code(), c.name(), c.code(), c.getRatePerUnit()));

        System.out.print("\nВведите код валюты, которую хотите перевести (например, 840 для USD): ");
        int numb1 = in.nextInt();

        System.out.print("\nВведите код необходимой валюты (например, 840 для USD): ");
        int numb2 = in.nextInt();

        if (!currCode.containsKey(numb1)) {
            System.out.println("Валюта не найдена!");
            return;
        } else if(!currCode.containsKey(numb2)) {
            System.out.println("Валюта не найдена!");
            return;}

        Cash.Currency currency1 = currCode.get(numb1);
        Cash.Currency currency2 = currCode.get(numb2);
        System.out.printf("Введите сумму в %s: ", currency1.code());
        double amount = in.nextDouble();

        double result1 = amount * currency1.getRatePerUnit();
        double result2 = result1 / currency2.getRatePerUnit();
        System.out.printf("%.2f %s(%03d) = %.2f %s(%03d)%n", amount, currency1.code(),currency1.numb(), result2, currency2.code(), currency2.numb());
    }

}
