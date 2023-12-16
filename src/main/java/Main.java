public class Main {
    public static void main(String[] args) {

        boolean estaEmServico = true;

        AdministradorDePessoas administradorDePessoas = new AdministradorDePessoas();

        ConexaoBD.criarTabelas();

        Mensageria.bemVindo();
        administradorDePessoas.popularTabelas();

        while ( estaEmServico ){

            Mensageria.mostrarAcoesDoMenu();

            estaEmServico = administradorDePessoas.apresentarMenuEColetarAcao();

        }

    }
}