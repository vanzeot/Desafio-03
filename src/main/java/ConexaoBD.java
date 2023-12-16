import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConexaoBD {
    static Connection con;
    public static Connection criarConexao(){

        try{
            //load driver
            Class.forName("org.postgresql.Driver");
            //get connection
            String url="jdbc:postgresql://localhost:5432/desafio-03";
            String username="postgres";
            String password="admin";
            con= DriverManager.getConnection(url,username,password);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return con;

    }

    public static void criarTabelas(){

        String query = "DROP TABLE IF EXISTS \"desafio-03\".public.pessoa CASCADE; " +
                "DROP TABLE IF EXISTS \"desafio-03\".public.contato CASCADE; " +

                "CREATE TABLE \"desafio-03\".public.pessoa (" +
                "id_pessoa serial," +
                "nome varchar(40)," +
                "telefone varchar(20), " +
                "email varchar(40), " +
                "endereco varchar(60), " +
                "cpf varchar(14), " +
                "data_de_nascimento varchar(40)," +
                "PRIMARY KEY(id_pessoa)" +
                "); " +

                "CREATE TABLE \"desafio-03\".public.contato (" +
                "id_contato serial, " +
                "id_pessoa bigint, " +
                "nome varchar(40), " +
                "email varchar(40), " +
                "telefone varchar(20), " +
                "PRIMARY KEY(id_contato), " +
                "CONSTRAINT fk_pessoa FOREIGN KEY(id_pessoa) REFERENCES pessoa(id_pessoa)" +
                ");";

        try (
                Connection con = criarConexao();
                Statement statement = con.createStatement();
                ){

            statement.execute(query);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
