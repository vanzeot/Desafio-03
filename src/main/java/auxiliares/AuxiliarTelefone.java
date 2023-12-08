package auxiliares;

public class AuxiliarTelefone {

    public static boolean telefoneNaoEhValido(String telefone) {

        // Validação simples de 8 dígitos no mínimo (telefone fixo sem DDD)

        String apenasNumeros = telefone.replaceAll("\\D+","");

        return (apenasNumeros.length() < 8);
    }

    public static String formatarTelefone(String telefone){

        String apenasNumeros = telefone.replaceAll("\\D+","");
        int digitos = apenasNumeros.length();

        if (digitos < 8){
            return "Número inválido";
        } else if (digitos == 8) {
            // Fixo sem DDD, ex.: 3222-2222
            String parte1 = apenasNumeros.substring(0,4);
            String parte2 = apenasNumeros.substring(4,8);
            return parte1 + "-" + parte2;
        } else if (digitos == 9) {
            // Celular sem DD, ex.: 98888-8888
            String parte1 = apenasNumeros.substring(0,5);
            String parte2 = apenasNumeros.substring(5,9);
            return parte1 + "-" + parte2;
        } else if (digitos == 10) {
            // Fixo com DDD, ex.: 62 3222-2222
            String parte1 = apenasNumeros.substring(0,2);
            String parte2 = apenasNumeros.substring(2,6);
            String parte3 = apenasNumeros.substring(6,10);
            return parte1 + " " + parte2 + "-" + parte3;
        }
        else if (digitos == 11){
            // Fixo com DDD ou maiores, ex.: 62 98888-8888
            String parte1 = apenasNumeros.substring(0,2);
            String parte2 = apenasNumeros.substring(2,7);
            String parte3 = apenasNumeros.substring(7,11);
            return parte1 + " " + parte2 + "-" + parte3;
        } else {
            // Forma genérica para números internacionais, ex.: 5562 3222-2222
            String parte1 = apenasNumeros.substring(0,4);
            String parte2 = apenasNumeros.substring(4,digitos-4);
            String parte3 = apenasNumeros.substring(digitos-4,digitos);
            return parte1 + " " + parte2 + "-" + parte3;
        }
    }

}
