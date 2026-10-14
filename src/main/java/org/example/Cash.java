package org.example;

import java.util.*;
import static org.example.Show.*;
import static org.example.Metal_Trade.*;
import static org.example.Parsing.*;
import static org.example.Convertation_Currencies.*;


public class Cash {

    public static final String CBR_URL_MET = "https://www.cbr.ru/hd_base/metall/metall_base_new/";
    public static final Map <String, Metal> metals = new HashMap<>();
    public static final Map <String,Metal> metDate = new HashMap<>();
    public static final String CBR_URL_CUR = "https://www.cbr.ru/currency_base/daily/";
    public static final Map <String, Currency> currencies = new HashMap<>();
    public static final Map <Integer,Currency> currCode = new HashMap<>();

    public static void main(String[] args) {

        loadMetal();
        loadCurrencies();

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.print("""
                     \n
                     Что вы хотите сделать?
                    
                    1. Конвертировать валюту в рубли
                    2. Конвертировать рубли в валюту
                    3. Конвертация иностранных валют
                    4. Показать курс валют (в рублях)
                    5. Показать курс определённой валюты (в рублях)
                    6. Показать курс драг. металлов (руб./грамм)
                    7. Купить металл
                    8. Продать металл
                    9. Выход
                    \n
                    """);
            System.out.print("Выберите действие: ");

            int choice = in.nextInt();
            switch (choice) {
                case 1:
                    convert_to_RUB(in);
                    break;
                case 2:
                    to_convert_RUB(in);
                    break;
                case 3:
                    convert_currencies(in);
                    break;
                case 4:
                    showAllCurrencies();
                    break;
                case 5:
                    showOneCurrency(in);
                    break;
                case 6:
                    showMetals();
                    break;
                case 7:
                    buy_Metal(in);
                    break;
                case 8:
                    sell_metal(in);
                    break;
                case 9:
                    System.out.print("До свидания!");
                    return;
                default:
                    System.out.println("Такой опции нет, попробуйте еще раз.");
            }
        }
    }


    public record Currency(int numb, String code, String name, double rate, int units) {
        public double getRatePerUnit() {
            return rate / units;
        }
        }

    public record Metal(String date, double gold, double silver, double platinum, double palladium) {

    }
}