package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.*;
import java.util.Locale;
import static org.example.Cash.*;


public class Parsing {

    public static void loadCurrencies() {
        try {
            Document doc = Jsoup.connect(CBR_URL_CUR)
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

                        Cash.Currency currency = new Cash.Currency(numb, code, name, rate, units);
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


    public static void loadMetal() {
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

                        Cash.Metal rateMet = new Cash.Metal(date, gold, silver, platinum, palladium);
                        metDate.put(date, rateMet);
                        metals.put(date, rateMet);
                    } catch (Exception e) {
                        System.err.println("Ошибка парсинга строки: " + row.text());
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Курсы металлов успешно загружены. Записей: " + metals.size());
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


    private static double parseNumber(String numberStr) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        return format.parse(numberStr.replace(" ", "")).doubleValue();
    }
}
