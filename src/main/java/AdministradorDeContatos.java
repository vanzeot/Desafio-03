import java.util.ArrayList;
import java.util.Scanner;

public class AdministradorDeContatos {

    ArrayList<Contato> contatos;
    Scanner scanner = new Scanner(System.in);

    public AdministradorDeContatos(ArrayList<Contato> contatos){
        this.contatos = contatos;
    }

    public static int selecionarContato(ArrayList<Contato> contatos){

        boolean loop = true;
        int indice = 0;

        while (loop){

            for (int i = 0; i < contatos.size(); i++) {
                System.out.println((i+1) + " - " + contatos.get(i).getNome());
            }

            System.out.println("Insira o número correspondente ao contato desejado:");
            indice = Integer.parseInt(
                    new Scanner(System.in).nextLine()
            ) - 1 ; // Remove 1 pois array começa na posição 0

            // Busca contato a partir do valor inserido
            try {
                contatos.get(indice);
                loop = false;
            } catch (Exception e){
                System.out.println("Valor inválido. ");
                indice = -1;
            }
        }

        return indice;
    }

    public ArrayList<Contato> acoesDeContatos(){

        boolean emServico = true;
        int indice;

        while (emServico){

            Mensageria.mostrarAcoesDeContato();
            String acao = scanner.nextLine();

            switch (acao) {
                case "1" -> contatos.add(new Contato().inserirDados());
                case "2" -> {
                    indice = AdministradorDeContatos.selecionarContato(contatos);
                    if (indice != -1) {
                        contatos.set(indice, new Contato().inserirDados());
                    }
                }
                case "3" -> {
                    if (this.contatos.size() > 2) {
                        indice = AdministradorDeContatos.selecionarContato(contatos);
                        if (indice != -1) {
                            contatos.remove(indice);
                        }
                    } else {
                        System.out.println("Não há como remover, pois deve haver no mínimo 2.");
                    }
                }
                case "4" -> emServico = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        return contatos;
    }
}
