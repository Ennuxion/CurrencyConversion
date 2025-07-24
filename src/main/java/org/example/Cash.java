package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class Cash {
    private static final String CBR_URL = "https://www.cbr.ru/currency_base/daily/";
    private static final Map <String, Currency> currencies = new HashMap<>();
    private static final Map <Integer,Currency> currCode = new HashMap<>();

    public static void main(String[] args) {
        loadCurrencies();

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\nЧто вы хотите сделать?");
            System.out.println("1. Конвертировать валюту в рубли");
            System.out.println("2. Конвертировать рубли в валюту");
            System.out.println("3. Показать все курсы валют");
            System.out.println("4. Выход");
            System.out.print("Выберите действие: ");

            int choice = in.nextInt();
            switch (choice) {
                case 1:
                    convertToRubles(in);
                    break;
                case 2:
                    convertFromRubles(in);
                    break;
                case 3:
                    showAllCurrencies();
                    break;
                case 4:
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Такой опции нет, попробуйте еще раз.");
            }
        }
    }

    private static void loadCurrencies() {
        try {
            Document doc = Jsoup.connect(CBR_URL)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            Element table = doc.select("table.data").first();
            assert table != null;
            Elements rows = table.select("tr:gt(0)"); // Пропускаем заголовок

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() >= 5) {
                    try {
                        int numb = Integer.parseInt(cols.get(0).text());
                        String code = cols.get(1).text();
                        String name = cols.get(3).text();
                        double rate = parseNumber(cols.get(4).text());
                        int units = Integer.parseInt(cols.get(2).text());

                        Currency currency = new Currency(numb, code, name, rate, units);
                        currCode.put(numb, currency);
                        currencies.put(code.toLowerCase(), currency);
                    } catch (Exception e) {
                        System.err.println("Ошибка парсинга строки: " + row.text());
                    }
                }
            }

            System.out.println("Курсы валют успешно загружены. Доступно " + currencies.size() + " валют.");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке курсов валют: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void convertToRubles(Scanner in) {
        System.out.println("\nДоступные валюты:");
        currencies.values().stream()
                .sorted(Comparator.comparing(Currency::code))
                .forEach(c -> System.out.printf("%03d - %s(%s) (1 %s = %.4f руб.)%n",
                        c.numb(), c.code(), c.name(), c.code(), c.getRatePerUnit()));

        System.out.print("\nВведите код валюты (например, 840 для USD): ");
        int numb = in.nextInt();

        if (!currCode.containsKey(numb)) {
            System.out.println("Валюта не найдена!");
            return;
        }

        Currency currency = currCode.get(numb);
        System.out.printf("Введите сумму в %s: ", currency.code());
        double amount = in.nextDouble();

        double result = amount * currency.getRatePerUnit();
        System.out.printf("%.2f %s(%03d) = %.2f RUB%n", amount, currency.code(),currency.numb(), result);
    }

    private static void convertFromRubles(Scanner in) {
        System.out.println("\nДоступные валюты:");
        currencies.values().stream()
                .sorted(Comparator.comparing(Currency::code))
                .forEach(c -> System.out.printf("%03d - %s(%s) (1 %s = %.4f руб.)%n",
                        c.numb(), c.code(), c.name(), c.code(), c.getRatePerUnit()));

        System.out.print("\nВведите цифровой код валюты (например, 978 для EUR): ");
        Integer  numb = in.nextInt();

        if (!currCode.containsKey(numb)) {
            System.out.println("Валюта не найдена!");
            return;
        }

        Currency currency = currCode.get(numb);
        System.out.print("Введите сумму в RUB: ");
        double amount = in.nextDouble();

        double result = amount / currency.getRatePerUnit();
        System.out.printf("%.2f RUB = %.2f %s%n", amount, result, currency.code());
    }

    private static void showAllCurrencies() {
        System.out.println("\nТекущие курсы валют ЦБ РФ:\n");
        System.out.printf("\n%-5s %-5s %-40s %-25s %-15s%n","Идентификатор", "Код", "Название", "Курс", "За 1 ед.");
        System.out.println();

        currencies.values().stream()
                .sorted(Comparator.comparing(Currency::code))
                .forEach(c -> System.out.printf("%-5d %-5s %-40s %-25.4f %-15.4f%n",
                        c.numb(),c.code(), c.name(), c.rate(), c.getRatePerUnit()));
    }

    private static double parseNumber(String numberStr) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        return format.parse(numberStr.replace(" ", "")).doubleValue();
    }

    record Currency(int numb, String code, String name, double rate, int units) {
        public double getRatePerUnit() {
            return rate / units;
        }
        }
}