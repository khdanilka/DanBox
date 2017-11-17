import java.sql.*;

public class SQLAutorizeManager implements SecurityManager {

    private Connection connection;
    private Statement statement;

    @Override
    public String getNick(String login, String password){
        String request = "SELECT login FROM users WHERE login='" +
                login + "'AND pass='" + password + "'";
        try {
            ResultSet resultSet = statement.executeQuery(request);
            if (resultSet.next()){
                return resultSet.getString(1);
            }else return null;
        } catch (SQLException e ){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(){
        try{
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:danbox.db");
        statement = connection.createStatement();
        }catch (ClassNotFoundException | SQLException e){
            throw new RuntimeException(e);
        }

    }
    @Override
    public void dispose(){
        try{
            connection.close();
        } catch (SQLException e){
            throw  new RuntimeException(e);
        }

    }


}
