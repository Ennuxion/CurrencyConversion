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
            System.out.println("3. Конвертация иностранных валют");
            System.out.println("4. Показать все курсы валют (в рублях)");
            System.out.println("5. Показать курс одной валюты (в рублях)");
            System.out.println("6. Показать курс драг. металлов (руб./грамм)");
            System.out.println("7. Конвертировать рубли в металл");
            System.out.println("8. Конвертировать металл в рубли");
            System.out.println("9. Выход");
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
                    fromCurrToCurr(in);
                    break;
                case 4:
                    showAllCurrencies();
                    break;
                case 5:
                    showCurrency(in );
                    break;
                case 6:
                    showMetal(in);
                    break;
                case 7:
                    rubToMetall(in);
                    break;
                case 8:
                    metallToRub(in);
                    break;
                case 9:
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Такой опции нет, попробуйте еще раз.");
            }
        }
    }

    private static void metallToRub(Scanner in) {
        System.out.println("Выберите металл, который вы хотите продать:");
        System.out.println("1. Золото");
        System.out.println("2. Серебро");
        System.out.println("3. Платина");
        System.out.println("4. Палладий");
        Metall latest = metalls.values().iterator().next();
        Scanner input = new Scanner(System.in);
        int choiceMet = input.nextInt();
        switch (choiceMet) {
            case 1:
                System.out.println("Введите количество (в граммах): ");
                double goldSum = input.nextDouble();
                double rubGold = goldSum  * latest.gold;
                System.out.printf("Вы получите: %.3f", rubGold);
                break;

            case 2:
                System.out.println("Введите количество (в граммах): ");
                double silverSum = input.nextDouble();
                double rubSilver = silverSum  * latest.silver;
                System.out.printf("Вы получите: %.3f", rubSilver);
                break;

            case 3:
                System.out.println("Введите количество (в граммах): ");
                double platinumSum = input.nextDouble();
                double rubPlatinum = platinumSum * latest.platinum;
                System.out.printf("Вы получите: %.3f", rubPlatinum);
                break;

            case 4:
                System.out.println("Введите количество (в граммах): ");
                double palladiumSum = input.nextDouble();
                double rubPalladium = palladiumSum * latest.palladium;
                System.out.printf("Вы получите: %.3f", rubPalladium);
                break;
        }
    }

    private static void rubToMetall(Scanner in) {

        System.out.println("Введите сумму, на которую хотите купить металл:");

        double sum = in.nextDouble();

        Metall latest = metalls.values().iterator().next();

        double goldRub = (sum / latest.gold);
        double silverRub = (sum / latest.silver);
        double platinumRub = (sum / latest.platinum);
        double palladiumRub = (sum / latest.palladium);
        System.out.println("На эти деньги можно приобрести: ");
        System.out.printf(" Золото - %.3f грамм\n Серебро - %.3f грамм\n Платина - %.3f грамм\n Палладий - %.3f грамм\n", goldRub, silverRub, platinumRub, palladiumRub);
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

            System.out.println("\nКурсы валют успешно загружены. Доступно " + currencies.size() + " валют.");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке курсов валют: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void loadMetall() {
        try {
            Document doc = Jsoup.connect(CBR_URL_MET)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            Element table = doc.select("table.data").first();
            if (table == null) {
                throw new RuntimeException("Таблица с курсами металлов не найдена");
            }

            Elements rows = table.select("tbody tr"); // Более точный селектор

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() >= 5) {  // Проверяем, что есть все нужные колонки
                    try {
                        String date = cols.get(0).text().trim();
                        double gold = parseMetalValue(cols.get(1).text());
                        double silver = parseMetalValue(cols.get(2).text());
                        double platinum = parseMetalValue(cols.get(3).text());
                        double palladium = parseMetalValue(cols.get(4).text());

                        Metall rateMet = new Metall(date, gold, silver, platinum, palladium);
                        metDate.put(date, rateMet);
                        metalls.put(date, rateMet);
                    } catch (Exception e) {
                        System.err.println("Ошибка парсинга строки: " + row.text());
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Курсы металлов успешно загружены. Записей: " + metalls.size());
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке курсов металлов: " + e.getMessage());
            System.exit(1);
        }
    }

    private static double parseMetalValue(String text) throws ParseException {
        // Удаляем все пробелы (как разделители тысяч) и заменяем запятую на точку
        String normalized = text.replaceAll("\\s+", "").replace(',', '.');
        return Double.parseDouble(normalized);
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
        System.out.printf("\n%-15s %-10s %-40s %-25s %-15s%n","Идентификатор", "Код", "Название", "Курс (в рублях)", "За 1 ед.");
        System.out.println();

        currencies.values().stream()
                .sorted(Comparator.comparing(Currency::code))
                .forEach(c -> System.out.printf("%-15d %-10s %-40s %-25.4f %-15.4f%n",
                        c.numb(),c.code(), c.name(), c.rate(), c.getRatePerUnit()));
    }

    private static void fromCurrToCurr(Scanner in) {
        System.out.println("\nДоступные валюты:");
        currencies.values().stream()
                .sorted(Comparator.comparing(Currency::code))
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

        Currency currency1 = currCode.get(numb1);
        Currency currency2 = currCode.get(numb2);
        System.out.printf("Введите сумму в %s: ", currency1.code());
        double amount = in.nextDouble();

        double result1 = amount * currency1.getRatePerUnit();
        double result2 = result1 / currency2.getRatePerUnit();
        System.out.printf("%.2f %s(%03d) = %.2f %s(%03d)%n", amount, currency1.code(),currency1.numb(), result2, currency2.code(), currency2.numb());
    }

    private static void showCurrency(Scanner in) {
        System.out.println("\nКакая валюта вас интересует?\n");
        System.out.printf("\n%-5s ", "Название валюты");
        System.out.println();

        currencies.values().stream()
                .sorted(Comparator.comparing(Currency::numb))
                .forEach(c -> System.out.printf("\n%d - %s (%s) ",c.numb, c.name(),  c.code()));

        System.out.println("\nВведите код валюты:");
        int codeCurr = in.nextInt ();

        if (!currCode.containsKey(codeCurr)) {
            System.out.println("\nВалюта не найдена!");
            return;
        }
        Currency currency = currCode.get(codeCurr);

        System.out.println("\nКурс данной валюты сегодня:");
        System.out.printf("\n%-15s %-10s %-40s %-25s %-15s%n","Идентификатор", "Код", "Название", "Курс (в рублях)", "За 1 ед.");
        System.out.printf("%-15d %-10s %-40s %-25.4f %-15.4f%n",
                currency.numb(), currency.code(), currency.name(), currency.rate(), currency.getRatePerUnit());
    }

    private static void showMetal(Scanner in) {
        System.out.println("\n\nТекущие курсы металлов ЦБ РФ (руб./грамм):");
        System.out.printf("\n%-20s %-20s %-20s %-20s %-20s%n","Дата", "Золото", "Серебро", "Платина", "Палладий");
        System.out.println();

        metalls.values().stream()
                .sorted(Comparator.comparing(Metall::date))
                .forEach(m -> System.out.printf("%-20s %-20s %-20s %-20.4f %-20.4f%n",
                        m.date(),m.gold(), m.silver(), m.platinum(), m.palladium()));
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

    record Metal(String date, double gold, double silver, double platinum, double palladium) {
        public String getSummary() {
            return String.format(
                    "Курс на %s:\n- Золото: %.2f руб/г \n- Серебро: %.2f руб/г\n- Платина: %.2f руб/г\n- Палладий: %.2f руб/г",
                    date, gold, silver, platinum, palladium
            );
        }
    }
}