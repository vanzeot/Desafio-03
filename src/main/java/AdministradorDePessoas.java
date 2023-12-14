import auxiliares.AuxiliarCpf;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AdministradorDePessoas {

    /*
    Classe para fazer a ponte entre a classe de negócio (Pessoa) e o banco de dados.
     */

    Connection con = ConexaoBD.con;

    public AdministradorDePessoas(){
    }

    // MÉTODOS DE AÇÕES DO MENU

    public void imprimirTabela(){

        String formatacaoDasColunasDaTabela = "| %-25s | %-13s | %-25s | %-35s | %-14s | %-12s | %-120s |\n";

        // Query para buscar todas pessoas
        String queryPessoas="SELECT id_pessoa, nome, telefone, email, endereco, cpf, data_de_nascimento FROM \"desafio-03\".public.pessoa";

        try( Statement statementDasPessoas = con.createStatement() ){

            ResultSet resultSetDasPessoas= statementDasPessoas.executeQuery(queryPessoas);

            // Imprime o cabeçalho da tabela, formatado com a tabulação mínima de cada coluna
            System.out.println();
            System.out.printf(formatacaoDasColunasDaTabela, "NOME", "TELEFONE", "EMAIL", "ENDEREÇO", "CPF", "DATA NASC.", "CONTATOS");

            int i = 0;

            // Loop para cada pessoa obtida na query das tabelas
            while (resultSetDasPessoas.next()){

                Long id_pessoa = resultSetDasPessoas.getLong("id_pessoa");
                // Query para buscar todos os contatos de uma pessoa específica
                String queryContatos="SELECT nome, email, telefone FROM \"desafio-03\".public.contato where id_pessoa = " + id_pessoa;

                try ( Statement statementDosContatos = con.createStatement() ){

                    ResultSet resultSetDosContatos = statementDosContatos.executeQuery(queryContatos);
                    StringBuilder contatos = new StringBuilder();

                    // Loop para cada contato obtido na query das tabelas
                    while ( resultSetDosContatos.next() ){
                        contatos.append("< ")
                                .append(resultSetDosContatos.getString("nome"))
                                .append(" / ").append(resultSetDosContatos.getString("email"))
                                .append(" / ").append(resultSetDosContatos.getString("telefone"))
                                .append(" > ");
                    }

                    // Imprime cada campo da tabela já com a formatação da largura mínima da coluna
                    System.out.printf(formatacaoDasColunasDaTabela, // formatação das colunas
                            resultSetDasPessoas.getString(2), //nome
                            resultSetDasPessoas.getString(3), //telefone
                            resultSetDasPessoas.getString(4), //email
                            resultSetDasPessoas.getString(5), //endereço
                            resultSetDasPessoas.getString(6), //cpf
                            resultSetDasPessoas.getString(7), //data de nascimento
                            contatos.toString()
                    );
                }

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
        pessoa.inserirDados();

        enviarQueryDeInsertDaPessoa(pessoa);
//        testarInserindoDados(pessoa);

    }



    public static Pessoa testarInserindoDados(Pessoa pessoa){
        return pessoa.setarTudo();
    }

    public void editarPessoa(){

        Pessoa pessoa = solicitarCpfParaEncontrarPessoa();

        System.out.println("A pessoa é o(a): " + pessoa.getNome() +
                "\nCaso naõ deseje alterar algum dado, apenas tecle 'Enter' para pular.");

        pessoa.acoesDeEdicao();

    }

    public void excluirPessoa(){

        Scanner scanner = new Scanner(System.in);
        Pessoa pessoa  = solicitarCpfParaEncontrarPessoa();

        System.out.println("A pessoa é o(a): " + pessoa.getNome() +
                "\nCaso deseje excluir a pessoa, favor digitar 'confirma': ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equals("confirma")) {

//            con = ConexaoBD.criarConexao();

            // REMOÇÃO no banco de dados
            String queryContatos="DELETE FROM contato WHERE id_pessoa = ?;";
            String queryPessoa="DELETE FROM pessoa WHERE id_pessoa = ?;";

            // PESSOA
            //TODO: DIVIDIR EM DUAS QUERYS/STATEMENTS
            try (
                    PreparedStatement preparedStatementContatos=con.prepareStatement(queryContatos, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement preparedStatementPessoa=con.prepareStatement(queryPessoa, Statement.RETURN_GENERATED_KEYS);
                 ) {

                preparedStatementContatos.setLong(1,pessoa.getId());
                preparedStatementPessoa.setLong(1,pessoa.getId());

                System.out.println(preparedStatementContatos.toString());
                preparedStatementContatos.executeUpdate();
                System.out.println(preparedStatementPessoa.toString());
                preparedStatementPessoa.executeUpdate();

            } catch (SQLException e) {
                System.out.println("\nOperação malsucedida. Avaliar erro:\n");
                throw new RuntimeException(e);
            }


            System.out.println("\nDados alterados com sucesso.\n");
        } else {
            System.out.println("\nOperação abortada.\n");
        }

    }


    //=====================
    // MÉTODOS AUXILIARES
    //=====================


    // Lida com a interface entre o usuário e o método de busca por cpf

    private Pessoa solicitarCpfParaEncontrarPessoa() {
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

                try {
                    resultadoDaBusca = getPessoaPorCpf(cpfFormatado);
                    buscaFinalizada = true;
                } catch (Exception e){
                    System.out.print("Pessoa não encontrada. Essa é a lista de pessoas:");

                    // TODO: RESOLVER CONTEXTO STATIC PARA VISUALIZAR TABELA
                    //imprimirTabela();
                }
            }



        }

        return resultadoDaBusca;
    }

    public Pessoa getPessoaPorCpf(String cpf){
        // SELECT no banco de dados
        con=ConexaoBD.criarConexao();
        String queryPessoaPorCpf="SELECT * FROM \"desafio-03\".public.pessoa where cpf = '" + cpf + "';";

        try{
            Statement statementDaPessoa = con.createStatement();
            ResultSet resultSetDaPessoa= statementDaPessoa.executeQuery(queryPessoaPorCpf);

            resultSetDaPessoa.next();

            Long id_pessoa = resultSetDaPessoa.getLong("id_pessoa");
            String queryContatos="SELECT nome, email, telefone FROM \"desafio-03\".public.contato where id_pessoa = " + id_pessoa + ";";
            Statement statementDosContatos = con.createStatement();
            ResultSet resultSetDosContatos = statementDosContatos.executeQuery(queryContatos);

            // Cria array com contatos da pessoa

            ArrayList<Contato> contatosDaPessoa = new ArrayList<>();

            while (resultSetDosContatos.next()){

                contatosDaPessoa.add(
                        new Contato(
                                resultSetDosContatos.getString("nome"),
                                resultSetDosContatos.getString("email"),
                                resultSetDosContatos.getString("telefone")
                        )
                );

            }

           return new Pessoa(
                   resultSetDaPessoa.getLong(1), //id
                   resultSetDaPessoa.getString(2), //nome
                   resultSetDaPessoa.getString(3), //telefone
                   resultSetDaPessoa.getString(4), //email
                   resultSetDaPessoa.getString(5), //endereço
                   resultSetDaPessoa.getString(6), //cpf
                   resultSetDaPessoa.getString(7), //data de nascimento
                   contatosDaPessoa
           );


        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
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
                this.editarPessoa();
                break;
            case "4":
                this.excluirPessoa();
                break;
            case "5":
                estaEmServico = false;
                break;
            default:
                System.out.println("Opção não reconhecida. Por favor, tente de novo...");
        }

        return estaEmServico;
    }

    public void enviarQueryDeInsertDaPessoa(Pessoa pessoa){

        // INSERÇÃO no banco de dados
        String queryDaPessoa="INSERT INTO pessoa (nome, telefone, email, endereco, cpf, data_de_nascimento) VALUES(?,?,?,?,?,?);";

        // PESSOA
        try (PreparedStatement preparedStatement= con.prepareStatement(queryDaPessoa, Statement.RETURN_GENERATED_KEYS)) {
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
                String queryDoContato = "INSERT INTO contato (id_pessoa, nome, telefone, email) VALUES(?,?,?,?);";
                PreparedStatement preparedStatement2 = con.prepareStatement(queryDoContato, Statement.RETURN_GENERATED_KEYS);
                // CONTATOS
                for (int i=0; i < pessoa.getContatos().size(); i++){


                    preparedStatement2.setLong(1,keys.getLong("id_pessoa"));
                    preparedStatement2.setString(2,pessoa.getContatos().get(i).getNome());
                    preparedStatement2.setString(3,pessoa.getContatos().get(i).getTelefone());
                    preparedStatement2.setString(4,pessoa.getContatos().get(i).getEmail());
                    System.out.println(preparedStatement2.toString());

                    preparedStatement2.executeUpdate();
                }

                System.out.println("\nDados incluídos com sucesso.\n");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void popularTabelas(){

        Contato contato1otavio = new Contato(
                "Elis",
                "elis@gmail.com",
                "3234-4243"
        );

        Contato contato2otavio = new Contato(
                "Antonio",
                "antonio@gmail.com",
                "3234-4243"
        );

        Contato contato1danielle = new Contato(
                "Lucia",
                "lucia@gmail.com",
                "3245-8942"
        );

        Contato contato2danielle = new Contato(
                "Fátima",
                "fatima@gmail.com",
                "3234-4256"
        );

        ArrayList<Contato> contatosOtavio = new ArrayList<>();
        ArrayList<Contato> contatosDanielle = new ArrayList<>();
        contatosOtavio.add(contato1otavio);
        contatosOtavio.add(contato2otavio);
        contatosDanielle.add(contato1danielle);
        contatosDanielle.add(contato2danielle);

        Pessoa otavio = new Pessoa(
                "Otávio dos Santos",
                "98888-0141",
                "otavio1@gmail.com",
                "Rua das Pedras, 217, Goiânia",
                "954.706.010-48",
                "11/08/1994",
                contatosOtavio
        );

        Pessoa danielle = new Pessoa(
                "Danielle Ribeiro",
                "98856-0100",
                "danielle12@gmail.com",
                "Rua das Gameleiras, 43, Goiânia",
                "013.340.140-51",
                "27/12/1993",
                contatosDanielle
        );

        enviarQueryDeInsertDaPessoa(otavio);
        enviarQueryDeInsertDaPessoa(danielle);
    }


}
