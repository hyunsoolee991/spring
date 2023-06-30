package hello.typeconverter.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

    @Override
    public Number parse(String text, Locale locale) throws ParseException {

        log.info("text={}, locale={}", text, locale);

        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        log.info("numberFormat={}", numberFormat);

        // "1,000" -> 1000
        return numberFormat.parse(text);
    }

    @Override
    public String print(Number object, Locale locale) {

        log.info("object={}, locale={}", object, locale);

        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        log.info("numberFormat={}", numberFormat);

        return numberFormat.format(object);
    }
}
