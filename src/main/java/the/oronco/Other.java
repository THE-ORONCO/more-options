package the.oronco;

import static the.oronco.builder.Builder.buildWith;

import the.oronco.builder.RequiredField;
import the.oronco.builder.Testy;

public class Other {
    public static void main(String[] args) {
        Testy.TestyBuilder<RequiredField.Missing<String>, RequiredField.Missing<Integer>> b0 = Testy.builder();
        Testy.TestyBuilder<RequiredField.Present<String>, RequiredField.Missing<Integer>> b1 = b0.value("a");
        Testy.TestyBuilder<RequiredField.Present<String>, RequiredField.Present<Integer>> b2 = b1.otherValue(1);
        Testy a = buildWith(b2);
        System.out.println(a);
    }
}
