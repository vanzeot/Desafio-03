import auxiliares.AuxiliarCpf;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AdministradorDePessoas {

    /*
    Classe para fazer a ponte entre a classe de negócio (Pessoa) e o banco de dados.
     */

    public AdministradorDePessoas(){
    }

    // MÉTODOS DE AÇÕES DO MENU

    public void imprimirTabela(){

        String formatacaoDasColunasDaTabela = "| %-25s | %-13s | %-25s | %-35s | %-14s | %-12s | %-160s |\n";

        // Query para buscar todas pessoas
        String queryPessoas="SELECT id_pessoa, nome, telefone, email, endereco, cpf, data_de_nascimento FROM \"desafio-03\".public.pessoa ORDER BY id_pessoa";

        try(
                Connection con = ConexaoBD.criarConexao();
                Statement statementDasPessoas = con.createStatement();
                ) {

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

    }

    public void editarPessoa(){

        Pessoa pessoa = solicitarCpfParaEncontrarPessoa();

        System.out.println("A pessoa é o(a): " + pessoa.getNome() +
                "\nCaso naõ deseje alterar algum dado, apenas tecle 'Enter' para pular.");

        boolean emServico = true;

        while (emServico){

            Mensageria.mostrarAcoesDeEdicao();
            Scanner scanner = new Scanner(System.in);
            String acao = scanner.nextLine();
            String query;

            switch(acao){

                case "1":
                    pessoa.selecionarNome();
                    enviarQueryDeUpdateStringDaPessoa("nome", pessoa.getNome(), pessoa.getId());
                    break;

                case "2":
                    pessoa.selecionarTelefone();
                    enviarQueryDeUpdateStringDaPessoa("telefone", pessoa.getTelefone(), pessoa.getId());
                    break;

                case "3":
                    pessoa.selecionarEmail();
                    enviarQueryDeUpdateStringDaPessoa("email", pessoa.getEmail(), pessoa.getId());
                    break;

                case "4":
                    pessoa.selecionarEndereco();
                    enviarQueryDeUpdateStringDaPessoa("endereco", pessoa.getEndereco(), pessoa.getId());
                    break;

                case "5":
                    pessoa.selecionarCpf();
                    enviarQueryDeUpdateStringDaPessoa("cpf", pessoa.getCpf(), pessoa.getId());
                    break;

                case "6":
                    pessoa.selecionarDataDeNascimento();
                    enviarQueryDeUpdateStringDaPessoa("data_de_nascimento", pessoa.getDataDeNascimento(), pessoa.getId());
                    break;

                case "7":
                    apresentarAcoesdeContatosEEnviarQuery(pessoa);
                    break;

                case "8":
                    emServico = false;
                    break;

                default:
                    System.out.println("Opção inválida.");

            }
        }
    }

    public void excluirPessoa(){

        Scanner scanner = new Scanner(System.in);
        Pessoa pessoa  = solicitarCpfParaEncontrarPessoa();

        System.out.println("A pessoa é o(a): " + pessoa.getNome() +
                "\nCaso deseje excluir a pessoa, favor digitar 'confirma': ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equals("confirma")) {

            // REMOÇÃO no banco de dados
            String queryContatos="DELETE FROM contato WHERE id_pessoa = ?;";
            String queryPessoa="DELETE FROM pessoa WHERE id_pessoa = ?;";

            try (
                    Connection con = ConexaoBD.criarConexao();
                    PreparedStatement preparedStatementContatos=con.prepareStatement(queryContatos, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement preparedStatementPessoa=con.prepareStatement(queryPessoa, Statement.RETURN_GENERATED_KEYS);
                 ) {

                preparedStatementContatos.setLong(1,pessoa.getId());
                preparedStatementPessoa.setLong(1,pessoa.getId());

                // Deleta os contatos da pessoa
                preparedStatementContatos.executeUpdate();
                // Deleta a pessoa
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
        int indice = 0;

        while (!buscaFinalizada){
            System.out.println("Digite o CPF da pessoa:");
            String cpfDigitado = scanner.nextLine();

            if (AuxiliarCpf.cpfEhValido(cpfDigitado)){

                // Formata CPF antes de buscar (query)
                resultadoDaBusca = getPessoaPorCpf(AuxiliarCpf.formataCpf(cpfDigitado));

                if (resultadoDaBusca != null) {
                    buscaFinalizada = true;
                }

            }

            if (!buscaFinalizada){
                System.out.print("Pessoa não encontrada. Essa é a lista de pessoas:");
                imprimirTabela();
            }

        }

        return resultadoDaBusca;
    }

    public Pessoa getPessoaPorCpf(String cpf){
        // SELECT no banco de dados
        String queryPessoaPorCpf="SELECT * FROM \"desafio-03\".public.pessoa where cpf = '" + cpf + "';";

        try(
                Connection con = ConexaoBD.criarConexao();
                Statement statementDaPessoa = con.createStatement();
                ){

            ResultSet resultSetDaPessoa= statementDaPessoa.executeQuery(queryPessoaPorCpf);

            if (resultSetDaPessoa.next()){

                System.out.println("next = true");

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
            }
            else {
                System.out.println("next = false");
                return null;
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

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

    private void apresentarAcoesdeContatosEEnviarQuery(Pessoa pessoa) {
        AdministradorDeContatos administradorDeContatos = new AdministradorDeContatos(pessoa.getContatos());

        ArrayList<Contato> contatosAntesDaEdicao = new ArrayList<>(pessoa.getContatos());
        ArrayList<Contato> contatosAtualizados = administradorDeContatos.acoesDeContatos();

                    // Se a lista de contatos se alterou, realiza update
                    System.out.println("ANTES:");
                    for (int i = 0; i < contatosAntesDaEdicao.size(); i++) {
                        System.out.println(contatosAntesDaEdicao.get(i).getNome());
                    }
                    System.out.println("DEPOIS:");
                    for (int i = 0; i < contatosAtualizados.size(); i++) {
                        System.out.println(contatosAtualizados.get(i).getNome());
                    }
                    System.out.println("diferentes? = "+!contatosAtualizados.equals(contatosAntesDaEdicao));

        if (!contatosAtualizados.equals(contatosAntesDaEdicao)) {
            enviarQueryDeUpdateContatosDaPessoa(contatosAtualizados, pessoa.getId());
        }
    }

    public void enviarQueryDeInsertDaPessoa(Pessoa pessoa){

        // INSERIR na tabela de "pessoa"
        String queryDaPessoa="INSERT INTO pessoa (nome, telefone, email, endereco, cpf, data_de_nascimento) VALUES(?,?,?,?,?,?);";

        // Cria a pessoa
        try (
                Connection con = ConexaoBD.criarConexao();
                PreparedStatement preparedStatement= con.prepareStatement(queryDaPessoa, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1,pessoa.getNome());
            preparedStatement.setString(2,pessoa.getTelefone());
            preparedStatement.setString(3,pessoa.getEmail());
            preparedStatement.setString(4,pessoa.getEndereco());
            preparedStatement.setString(5,pessoa.getCpf());
            preparedStatement.setString(6,pessoa.getDataDeNascimento());

            preparedStatement.executeUpdate();

            // Cria os contatos
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {

                keys.next();
                String queryDoContato = "INSERT INTO contato (id_pessoa, nome, telefone, email) VALUES(?,?,?,?);";
                PreparedStatement preparedStatement2 = con.prepareStatement(queryDoContato, Statement.RETURN_GENERATED_KEYS);

                // Para cada contato do objeto "pessoa", realiza um INSERT na tabela "contato"
                for (int i=0; i < pessoa.getContatos().size(); i++){

                    preparedStatement2.setLong(1,keys.getLong("id_pessoa"));
                    preparedStatement2.setString(2,pessoa.getContatos().get(i).getNome());
                    preparedStatement2.setString(3,pessoa.getContatos().get(i).getTelefone());
                    preparedStatement2.setString(4,pessoa.getContatos().get(i).getEmail());

                    preparedStatement2.executeUpdate();
                }

                System.out.println("\nDados incluídos com sucesso.\n");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarQueryDeUpdateStringDaPessoa(String campo, String valor, Long id_pessoa){
        String query = "UPDATE pessoa SET " + campo + " = ? WHERE id_pessoa = ?;";

        try (
                Connection con = ConexaoBD.criarConexao();
                PreparedStatement preparedStatement = con.prepareStatement(query);
                ) {

            preparedStatement.setString(1,valor);
            preparedStatement.setLong(2,id_pessoa);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void enviarQueryDeUpdateContatosDaPessoa(ArrayList<Contato> contatos, Long id_pessoa) {

        String queryDeDelete    = "DELETE FROM contato WHERE id_pessoa = ?;";
        String queryDeInput     = "INSERT INTO contato (id_pessoa, nome, telefone, email) VALUES(?,?,?,?);";

        try (
                Connection con = ConexaoBD.criarConexao();
                PreparedStatement preparedStatementDelete = con.prepareStatement(queryDeDelete);
                PreparedStatement preparedStatementInput = con.prepareStatement(queryDeInput, Statement.RETURN_GENERATED_KEYS);
        ) {

            preparedStatementDelete.setLong(1, id_pessoa);
            preparedStatementDelete.executeUpdate();

            // Para cada contato do objeto "pessoa", realiza um INSERT na tabela "contato"
            for (int i=0; i < contatos.size(); i++){

                preparedStatementInput.setLong(1,id_pessoa);
                preparedStatementInput.setString(2,contatos.get(i).getNome());
                preparedStatementInput.setString(3,contatos.get(i).getTelefone());
                preparedStatementInput.setString(4,contatos.get(i).getEmail());

                preparedStatementInput.executeUpdate();
            }

            System.out.println("\nDados atualizados com sucesso.\n");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Inserir os dados iniciais da tabela, apenas para facilitar o teste
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
