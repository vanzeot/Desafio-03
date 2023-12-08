import auxiliares.AuxiliarCpf;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class AdministradorDePessoas {

    /*
    Classe para fazer a ponte entre a classe de negócio (Pessoa) e o banco de dados.
     */

    Connection con;

    static ArrayList<Pessoa> pessoas;   // TODO: REMOVER, substituído pelo banco de dados

    public AdministradorDePessoas(){
        pessoas = new ArrayList<>();
        Mensageria mensageria = new Mensageria();
    }

    // MÉTODOS DE AÇÕES DO MENU

    public void imprimirTabela(){

        // SELECT no banco de dados
        con=ConexaoBD.criarConexao();
        String query="SELECT nome, telefone, email, endereco, cpf, data_de_nascimento FROM \"desafio-03\".public.pessoa";

        try{
            Statement statement=con.createStatement();
            ResultSet resultSet= statement.executeQuery(query);

            System.out.printf("\n| %-25s | %-13s | %-25s | %-35s | %-14s | %-12s | %-120s |\n",
                    "NOME", "TELEFONE", "EMAIL", "ENDEREÇO", "CPF", "DATA NASC.", "CONTATOS");

            int i = 0;

            while (resultSet.next()){

                System.out.printf("| %-25s | %-13s | %-25s | %-35s | %-14s | %-12s | %-120s |\n",
                        resultSet.getString(1), //nome
                        resultSet.getString(2), //telefone
                        resultSet.getString(3), //email
                        resultSet.getString(4), //endereço
                        resultSet.getString(5), //cpf
                        resultSet.getString(6),  //data de nascimento
                        "CONTATOS" // TODO: STRING DOS CONTATOS
                );

                i++;
            }
            if (i == 0){
                System.out.println("\nNenhuma pessoa está cadastrada.");
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void adicionarPessoa(){

        Pessoa pessoa = new Pessoa();
//        inserirDados(pessoa);
        testarInserindoDados(pessoa);
        con = ConexaoBD.criarConexao();

        // INSERÇÃO no banco de dados
        String queryDaPessoa="INSERT INTO pessoa (nome, telefone, email, endereco, cpf, data_de_nascimento) VALUES(?,?,?,?,?,?);";

        // PESSOA
        try (PreparedStatement preparedStatement=con.prepareStatement(queryDaPessoa, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,pessoa.getNome());
            preparedStatement.setString(2,pessoa.getTelefone());
            preparedStatement.setString(3,pessoa.getEmail());
            preparedStatement.setString(4,pessoa.getEndereco());
            preparedStatement.setString(5,pessoa.getCpf());
            preparedStatement.setString(6,pessoa.getDataDeNascimento());

            System.out.println(preparedStatement.toString());
            int contagem = preparedStatement.executeUpdate();


            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {

                keys.next();
                String queryDoContato = "INSERT INTO contato (id_pessoa, nome, telefone, email) VALUES("+keys.getString("id_pessoa")+",?,?,?);";
                PreparedStatement preparedStatement2 = con.prepareStatement(queryDoContato, Statement.RETURN_GENERATED_KEYS);
                // CONTATOS
                for (int i=0; i < pessoa.getContatos().size(); i++){


                    preparedStatement2.setString(1,pessoa.getContatos().get(i).getNome());
                    preparedStatement2.setString(2,pessoa.getContatos().get(i).getTelefone());
                    preparedStatement2.setString(3,pessoa.getContatos().get(i).getEmail());
                    System.out.println(preparedStatement2.toString());

                    preparedStatement2.executeUpdate();
                }

                System.out.println("\nDados incluídos com sucesso.\n");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Pessoa inserirDados(Pessoa pessoa){
        // TODO: MOVER ISSO PRA CLASSE "PESSOA"
        pessoa.selecionarNome()
                .selecionarTelefone()
                .selecionarEmail()
                .selecionarEndereco()
                .selecionarCpf()
                .selecionarDataDeNascimento()
                .adicionarContatos()

        ;
        return pessoa;
    }

    public static Pessoa testarInserindoDados(Pessoa pessoa){
        return pessoa.setarTudo();
    }

    public static void editarPessoa(){

        int indice = solicitarCpfParaEncontrarPessoa();

        Pessoa pessoa = pessoas.get(indice);

        System.out.println("A pessoa é o(a): " + pessoa.getNome() +
                "\nCaso naõ deseje alterar algum dado, apenas tecle 'Enter' para pular.");

        pessoa.acoesDeEdicao();

    }

    public static void excluirPessoa(){

        Scanner scanner = new Scanner(System.in);
        int indice = solicitarCpfParaEncontrarPessoa();
        Pessoa pessoa = pessoas.get(indice);

        System.out.println("A pessoa é o(a): " + pessoa.getNome() +
                "\nCaso deseje excluir a pessoa, favor digitar 'confirma': ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equals("confirma")) {
            pessoas.remove(indice);
            System.out.println("\nDados alterados com sucesso.\n");
        } else {
            System.out.println("\nOperação abortada.\n");
        }

    }


    //=====================
    // MÉTODOS AUXILIARES
    //=====================


    // Lida com a interface entre o usuário e o método de busca por cpf

    private static int solicitarCpfParaEncontrarPessoa() {
        Scanner scanner = new Scanner(System.in);
        boolean buscaFinalizada = false;
        Pessoa resultadoDaBusca = null;
        String cpfFormatado = "";
        int indice = 0;

        while (!buscaFinalizada){
            System.out.println("Digite o CPF da pessoa:");
            String cpfDigitado = scanner.nextLine();

            boolean ehValido = AuxiliarCpf.cpfEhValido(cpfDigitado);

            if (ehValido){
                cpfFormatado = AuxiliarCpf.formataCpf(cpfDigitado);
                System.out.println("cpfFormatado:" + cpfFormatado);

                indice = getIndicePorCpf(cpfFormatado);
            }

            try {
                resultadoDaBusca = pessoas.get(indice);
                buscaFinalizada = true;
            } catch (Exception e){
                System.out.print("Pessoa não encontrada. Essa é a lista de pessoas:");

                // TODO: RESOLVER CONTEXTO STATIC PARA VISUALIZAR TABELA
                //imprimirTabela();
            }

        }

        return indice;
    }

    // Realiza uma varredura no Array até encontrar o que possui o CPF

    public static int getIndicePorCpf(String cpf) {

        boolean naoAchou = true;
        int indice = 0;

        while(naoAchou && indice < pessoas.size()){

            if (Objects.equals( pessoas.get(indice).getCpf(), cpf)){
                naoAchou = false;
            } else {
                indice++;
            }
        }

        return indice;

    }


    //////////////



    public boolean apresentarMenuEColetarAcao(){

        Scanner scanner = new Scanner(System.in);
        boolean estaEmServico = true;

        String opcaoSelecionada = scanner.nextLine();


        switch (opcaoSelecionada){

            case "1":
                this.imprimirTabela();
                break;
            case "2":
                this.adicionarPessoa();
                break;
            case "3":
                AdministradorDePessoas.editarPessoa();
                break;
            case "4":
                AdministradorDePessoas.excluirPessoa();
                break;
            case "5":
                estaEmServico = false;
                break;
            default:
                System.out.println("Opção não reconhecida. Por favor, tente de novo...");
        }

        return estaEmServico;
    }


}
