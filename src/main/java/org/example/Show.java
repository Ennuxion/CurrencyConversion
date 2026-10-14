package org.example;

import java.util.Comparator;
import java.util.Scanner;
import static org.example.Cash.*;


public class Show {
    public static void showMetals() {
        System.out.println("\n\nТекущие курсы металлов ЦБ РФ (руб./грамм):");
        System.out.printf("\n%-20s %-20s %-20s %-20s %-20s%n","Дата", "Золото", "Серебро", "Платина", "Палладий");
        System.out.println();

        metals.values().stream()
                .sorted(Comparator.comparing(Cash.Metal::date))
                .forEach(m -> System.out.printf("%-20s %-20s %-20s %-20.4f %-20.4f%n",
                        m.date(),m.gold(), m.silver(), m.platinum(), m.palladium()));
    }

    public static void showAllCurrencies() {
        System.out.println("\nТекущие курсы валют ЦБ РФ:\n");
        System.out.printf("\n%-15s %-10s %-40s %-25s %-15s%n","Идентификатор", "Код", "Название", "Курс (в рублях)", "За 1 ед.");
        System.out.println();

        currencies.values().stream()
                .sorted(Comparator.comparing(Currency::code))
                .forEach(c -> System.out.printf("%-15d %-10s %-40s %-25.4f %-15.4f%n",
                        c.numb(),c.code(), c.name(), c.rate(), c.getRatePerUnit()));
    }

    public static void showOneCurrency(Scanner in) {
        System.out.println("\nКакая валюта вас интересует?\n");
        System.out.printf("\n%-5s ", "Название валюты");
        System.out.println();

        currencies.values().stream()
                .sorted(Comparator.comparing(Cash.Currency::numb))
                .forEach(c -> System.out.printf("\n%d - %s (%s) ", c.numb(), c.name(),  c.code()));

        System.out.println("\nВведите код валюты:");
        int codeCurr = in.nextInt ();

        if (!currCode.containsKey(codeCurr)) {
            System.out.println("\nВалюта не найдена!");
            return;
        }
        Cash.Currency currency = currCode.get(codeCurr);

        System.out.println("\nКурс данной валюты сегодня:");
        System.out.printf("\n%-15s %-10s %-40s %-25s %-15s%n","Идентификатор", "Код", "Название", "Курс (в рублях)", "За 1 ед.");
        System.out.printf("%-15d %-10s %-40s %-25.4f %-15.4f%n",
                currency.numb(), currency.code(), currency.name(), currency.rate(), currency.getRatePerUnit());
    }
}
