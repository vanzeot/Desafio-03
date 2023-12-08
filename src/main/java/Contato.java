import auxiliares.AuxiliarEmail;
import auxiliares.AuxiliarTelefone;

import java.util.Scanner;

public class Contato {

    private String nome;
    private String email;
    private String telefone;

    Scanner scanner = new Scanner(System.in);

    public Contato(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public Contato() {

    }

    public Contato selecionarNome(){

        System.out.println("Digite o nome da pessoa: ");
        String input = scanner.nextLine().trim();
        this.nome = lidarComInputEmBranco(input);

        return this;
    }

    public Contato selecionarEmail(){
        System.out.println("Digite o email da pessoa: ");
        String email = scanner.nextLine().trim();

        while (AuxiliarEmail.emailNaoEhValido(email)){
            System.out.println("O email não é válido. Tente novamente: ");
            email = scanner.nextLine().trim();
        }
        this.email = email;

        return this;
    }

    public Contato selecionarTelefone(){
        System.out.println("Digite o telefone da pessoa: ");
        String telefone = scanner.nextLine().trim();

        while (AuxiliarTelefone.telefoneNaoEhValido(telefone)){
            System.out.println("O telefone não é válido. Tente novamente: ");
            telefone = scanner.nextLine().trim();
        }
        this.telefone = AuxiliarTelefone.formatarTelefone(telefone);

        return this;
    }

    public Contato inserirDados() {
        this.selecionarNome().selecionarEmail().selecionarTelefone();
        return this;
    }

    //=====================
    // MÉTODOS AUXILIARES
    //=====================

    private String lidarComInputEmBranco(String input) {
        while (input.trim().isBlank()){
            System.out.println("Valor em branco. Digite novamente: ");
            input = scanner.nextLine().trim();
        }

        return input;
    }

    //=====================
    //       GETTERS
    //=====================

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

}
