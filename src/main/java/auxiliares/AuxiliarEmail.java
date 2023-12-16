package auxiliares;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuxiliarEmail {

    public static boolean emailNaoEhValido(String email) {
        email = email.trim();
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return !matcher.matches();
    }

    public static String formatarEmail(String email) {
        return email.trim().toLowerCase();
    }


}
