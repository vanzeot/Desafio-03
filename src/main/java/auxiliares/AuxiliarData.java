package auxiliares;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class AuxiliarData {

    public static boolean dataNaoEhValida(String dataDeNascimento){
        return dataEhDesformatada(dataDeNascimento) || dataEhFuturaOuMenorDe18Anos(dataDeNascimento);
    }

    private static boolean dataEhDesformatada(String dataDeNascimento){
        dataDeNascimento = dataDeNascimento.trim();
        String regex = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        boolean desformatada = !Pattern.compile(regex)
                .matcher(dataDeNascimento)
                .matches();

        if(desformatada){
            System.out.print("Formato inválido. ");
        }

        return desformatada;
    }

    private static boolean dataEhFuturaOuMenorDe18Anos(String dataDeNascimento){

        SimpleDateFormat dateInput = new SimpleDateFormat("dd/MM/yyyy");
        Date dataDeHoje = Calendar.getInstance().getTime();
        boolean naoEhFutura = false;
        Date dataFormatada;

        try
        {
            dataFormatada = dateInput.parse(dataDeNascimento);
            if (dataFormatada.after(dataDeHoje)) {
                System.out.print("Data digitada é futura. ");
            } else if (ehMenorDe18Anos(dataFormatada, dataDeHoje)){
                System.out.print("Não pode ser menor de 18 anos. ");
            }
            else{
                naoEhFutura = true;
            }
        }
        catch (ParseException e)
        {
            System.out.print("Formato inválido. ");
        }

        return !naoEhFutura;
    }

    private static boolean ehMenorDe18Anos(Date dataDeNascimento, Date dataDeHoje) {

        int anos = Period.between(
                dataDeNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                dataDeHoje.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        ).getYears();

        return (anos < 18);
    }

}
