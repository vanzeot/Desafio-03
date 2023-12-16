package auxiliares;

import java.util.InputMismatchException;

public class AuxiliarCpf {

    public static boolean cpfEhValido(String inputCpf) {

        String cpfCru = inputCpf.replace("-","")
                .replace(".","")
                .replace("/","");

        if (cpfCru.equals("00000000000") || cpfCru.equals("11111111111") || cpfCru.equals("22222222222") || cpfCru.equals("33333333333") || cpfCru.equals("44444444444") || cpfCru.equals("55555555555") || cpfCru.equals("66666666666") || cpfCru.equals("77777777777") || cpfCru.equals("88888888888") || cpfCru.equals("99999999999") || (cpfCru.length() != 11))
            return (false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {

            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = cpfCru.charAt(i) - 48;
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + 48);


            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = cpfCru.charAt(i) - 48;
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + 48);

            return (dig10 == cpfCru.charAt(9)) && (dig11 == cpfCru.charAt(10));
        } catch (InputMismatchException erro) {
            return (false);
        }
    }

    public static String formataCpf(String cpfInput){

        String cpfSoComNumeros = cpfInput
                .replace("-","")
                .replace(".","")
                .replace("/","")
                .replace(" ","");

        String parte1 = cpfSoComNumeros.substring(0,3);
        String parte2 = cpfSoComNumeros.substring(3,6);
        String parte3 = cpfSoComNumeros.substring(6,9);
        String parte4 = cpfSoComNumeros.substring(9,11);

        return (parte1 + "." + parte2 + "." + parte3 + "-" + parte4);
    }

}
