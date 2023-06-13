package com.example.lab1_server;

import com.example.dbObjects.Client;
import com.example.dbObjects.Service;

import java.sql.*;
import java.util.ArrayList;


public class DBManager {
    private Connection conn;

    public void Init() throws SQLException, ClassNotFoundException {
        conn = GetDBConnection();
    }

    private static Connection GetDBConnection() throws ClassNotFoundException, SQLException
    {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/phonestation";
        String user = "postgres";
        String password = "admin";

        Connection connection = DriverManager.getConnection(url, user, password);

        return connection;
    }

    public boolean IsUserAdmin(String email) {
        try
        {
            Statement stmt = conn.createStatement();
            String strSelect = "SELECT EXISTS(SELECT 1 FROM \"Admins\" WHERE email = '"+email+"')";
            ResultSet rs = stmt.executeQuery(strSelect);
            rs.next();
            return rs.getBoolean(1);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Client GetClient(String email) {
        try
        {
            Statement stmt = conn.createStatement();
            String strSelect = "SELECT * FROM \"Clients\" WHERE email = '"+email+"'";
            ResultSet rs = stmt.executeQuery(strSelect);
            if(rs.next()){
                int id = rs.getInt("id");
                boolean isConfirmed = rs.getBoolean("isConfirmed");
                boolean isBanned = rs.getBoolean("isBanned");
                long phonenumber = rs.getLong("phonenumber");

                Client client = new Client(id, isConfirmed, isBanned, phonenumber, email);
                return client;
            }

            return null;

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void AddNewClient(String email) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO \"Clients\" (email) VALUES (?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            try{
                pstmt.setString(1, email);
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    //System.out.println("A new row has been inserted.");
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void ConfirmClient(int id) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "UPDATE \"Clients\" SET \"isConfirmed\" = true WHERE id = "+id;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            try{
                int rowsUpdated = pstmt.executeUpdate();
                /*if (rowsUpdated > 0) {
                    System.out.println("The row has been updated.");
                }*/
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void BanClient(int id) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "UPDATE \"Clients\" SET \"isBanned\" = true WHERE id = "+id;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            try{
                int rowsUpdated = pstmt.executeUpdate();
                /*if (rowsUpdated > 0) {
                    System.out.println("The row has been updated.");
                }*/
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void UnbanClient(int id) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "UPDATE \"Clients\" SET \"isBanned\" = false WHERE id = "+id;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            try{
                int rowsUpdated = pstmt.executeUpdate();
                /*if (rowsUpdated > 0) {
                    System.out.println("The row has been updated.");
                }*/
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Service> GetAllServices() {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM \"Services\"";

            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Service> services = new ArrayList<Service>();
            while (rs.next()){
                int id = rs.getInt("id");
                int price = rs.getInt("price");
                String name = rs.getString("name");

                Service service = new Service(id, price, name);
                services.add(service);
            }
            return services;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void AddServiceToClient(int clientId, int serviceId) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO \"ClientsServices\" (\"clientId\", \"serviceId\") VALUES (?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            try{
                pstmt.setInt(1, clientId);
                pstmt.setInt(2, serviceId);
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    //System.out.println("A new row has been inserted.");
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void AddClientPayment(int clientId, int serviceId) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO \"Payments\" (\"clientId\", \"serviceId\") VALUES (?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            try{
                pstmt.setInt(1, clientId);
                pstmt.setInt(2, serviceId);
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    //System.out.println("A new row has been inserted.");
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Service> GetAllClientAddedServices(int clientId) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "SELECT DISTINCT s.* " +
                    "FROM \"Services\" s " +
                    "INNER JOIN \"ClientsServices\" cs ON s.\"id\" = cs.\"serviceId\" " +
                    "WHERE cs.\"clientId\" = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ArrayList<Service> services = new ArrayList<Service>();
            try{
                pstmt.setInt(1, clientId); // set clientId parameter
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    Service service = new Service(id, price, name);
                    services.add(service);
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }

            return services;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Service> GetAllClientUnpaidServices(int clientId) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "SELECT DISTINCT s.* FROM \"Services\" s " +
                    "LEFT JOIN \"ClientsServices\" cs ON s.\"id\" = cs.\"serviceId\" " +
                    "LEFT JOIN \"Payments\" p ON cs.\"serviceId\" = p.\"serviceId\" AND cs.\"clientId\" = p.\"clientId\" " +
                    "WHERE cs.\"clientId\" = ? AND p.\"id\" IS NULL";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ArrayList<Service> services = new ArrayList<Service>();
            try{
                pstmt.setInt(1, clientId); // set clientId parameter
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    Service service = new Service(id, price, name);
                    services.add(service);
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }

            return services;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Service> GetAllClientPaidServices(int clientId) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "SELECT DISTINCT s.* " +
                    "FROM \"Services\" s " +
                    "INNER JOIN \"Payments\" p ON s.\"id\" = p.\"serviceId\" " +
                    "WHERE p.\"clientId\" = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ArrayList<Service> services = new ArrayList<Service>();
            try{
                pstmt.setInt(1, clientId); // set clientId parameter
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    Service service = new Service(id, price, name);
                    services.add(service);
                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }

            return services;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Client> GetAllClients() {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM \"Clients\"";

            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Client> clients = new ArrayList<>();
            while (rs.next()){
                int id = rs.getInt("id");
                boolean isConfirmed = rs.getBoolean("isConfirmed");
                boolean isBanned = rs.getBoolean("isBanned");
                long phonenumber = rs.getLong("phonenumber");
                String email = rs.getString("email");

                Client client = new Client(id, isConfirmed, isBanned, phonenumber, email);
                clients.add(client);
            }
            return clients;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void SetClientPhone(int id, long phone) {
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "UPDATE \"Clients\" SET \"phonenumber\" = "+phone+" WHERE id = "+id;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            try{
                int rowsUpdated = pstmt.executeUpdate();
                /*if (rowsUpdated > 0) {
                    System.out.println("The row has been updated.");
                }*/
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
