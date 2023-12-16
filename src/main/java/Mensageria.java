public class Mensageria {

    public static void bemVindo(){
        System.out.println("\n==================================");
        System.out.println("=== DESAFIO 02 - CADASTRO JAVA ===");
        System.out.println("==================================");

    }

    public static void mostrarAcoesDoMenu(){
        System.out.println("""

                1 - Listar pessoas
                2 - Incluir pessoa
                3 - Editar pessoa
                4 - Excluir pessoa
                5 - Finalizar

                Insira o número correspondente a ação desejada:""");
    }

    public static void mostrarAcoesDeEdicao(){
        System.out.println(
                """

                        1 - Nome
                        2 - Telefone
                        3 - Email
                        4 - Endereço
                        5 - CPF
                        6 - Data de nascimento
                        7 - Contatos
                        8 - Finalizar

                        Insira o número correspondente a ação desejada:""");
    }

    public static void mostrarAcoesDeContato(){
        System.out.println(
                """

                        1 - Incluir contato
                        2 - Editar contato
                        3 - Excluir contato
                        4 - Finalizar

                        Insira o número correspondente a ação desejada:""");
    }

}
