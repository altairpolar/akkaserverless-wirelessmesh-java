package common.type;

import akka.japi.Option;

import java.math.BigDecimal;

public class DecimalTransformer {

    public static Option<BigDecimal> safeConvertFromString(String value) {
        Option<BigDecimal> result;
        try {
            result = new Option.Some<>(new BigDecimal(value));
        } catch (Exception e) {
            result = Option.none();
        }
        return result;
    }

    public static String safeConvertToString(Option<BigDecimal> value) {
        if (value.get() == null)
            return "";
        else
            return value.get().toPlainString();
    }

}
