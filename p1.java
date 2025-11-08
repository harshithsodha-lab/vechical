import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.sql.*;
class VehicleOverloaded extends Exception {
    VehicleOverloaded(String s) {
        super(s);
    }
}

class VehicleDoesnotExist extends Exception {
    VehicleDoesnotExist(String s) {
        super(s);
    }
}

abstract class vehilces {
    String ownername;
    String vehiclename;
    int vehid;
    long m_no;
    int parkingid;
    int floorid;
    String vehicleType;
}

class VehicleInfo extends vehilces {
    LocalDateTime in_time;
    LocalDateTime out_time;
    Scanner sc = new Scanner(System.in);

    VehicleInfo(int a, int b) {
        ownername = "Empty";
        vehiclename = "Empty";
        vehid = 0;
        m_no = 0;
        parkingid = a;
        floorid = b;
        vehicleType = "Unknown";
    }

    void setData(Connection conn) {
        System.out.print("Enter your name: ");
        ownername = sc.nextLine();
        System.out.print("Enter your vehicle model name: ");
        vehiclename = sc.next();
        System.out.print("Enter your vehicle's last four digits of the number plate: ");
        vehid = sc.nextInt();
        System.out.print("Enter your mobile number: ");
        m_no = sc.nextLong();
        System.out.print("Enter the type of vehicle (2-wheeler or 4-wheeler): ");
        vehicleType = sc.next();
        System.out.println("Your floor ID is " + (floorid + 1));
        System.out.println("Your Parking ID is " + (parkingid + 1));

        in_time = LocalDateTime.now();
        try {
            String sql = "{CALL insertVehicle(?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setString(1, ownername);
            stmt.setString(2, vehiclename);
            stmt.setInt(3, vehid);
            stmt.setLong(4, m_no);
            stmt.setInt(5, parkingid + 1);
            stmt.setInt(6, floorid + 1);
            stmt.setString(7, vehicleType);
            stmt.setTimestamp(8, Timestamp.valueOf(in_time));
            stmt.execute();
            stmt.close();
            System.out.println("Vehicle data inserted into database.");
        } catch (SQLException e) {
            System.err.println("Error inserting vehicle data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    boolean getVehicleData(Connection conn, int vehid) {
        try {
            String sql = "{CALL getVehicle(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, vehid);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.BIGINT);
            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.registerOutParameter(6, Types.INTEGER);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.registerOutParameter(8, Types.TIMESTAMP);
            stmt.registerOutParameter(9, Types.TIMESTAMP);
            stmt.execute();
            this.parkingid = stmt.getInt(5);
            this.floorid = stmt.getInt(6);
            this.vehicleType = stmt.getString(7);
            this.in_time = stmt.getTimestamp(8).toLocalDateTime();
            Timestamp outTimeStamp = stmt.getTimestamp(9);
            this.out_time = (outTimeStamp != null) ? outTimeStamp.toLocalDateTime() : null;
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicle data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    long calculateParkingFee(int pricePerHour) {
        Duration duration = Duration.between(in_time, out_time);
        long hoursParked = duration.toHours();
        if (duration.toMinutes() % 60 != 0) {
            hoursParked++;
        }
        return hoursParked * pricePerHour;
    }
}

class Main {
    public static void main(String args[]) {
        String url = "jdbc:mysql://localhost:3306/vechile_data";
        String user = "root";
        String password = "";
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Parking Management System");
        System.out.println("");
        System.out.println("This system is managed by the gatekeeper");
        System.out.println("");
        System.out.print("Enter the number of floors: ");
        int fnum = sc.nextInt();
        VehicleInfo[][] vehicle = new VehicleInfo[fnum][];
        for (int i = 0; i < fnum; i++) {
            System.out.print("Enter the number of Two-wheeler vehicles that can park (positive integer): ");
            int w2 = sc.nextInt();
            System.out.print("Enter the number of Four-wheeler vehicles that can park (positive integer): ");
            int w4 = sc.nextInt();
            int tw = w2 + w4;
            vehicle[i] = new VehicleInfo[tw];
        }
        for (int i = 0; i < fnum; i++) {
            for (int j = 0; j < vehicle[i].length; j++) {
                vehicle[i][j] = new VehicleInfo(j, i);
            }
        }
        System.out.print("Enter parking price for two-wheeler vehicles for one hour: ");
        int pp2w1h = sc.nextInt();
        System.out.print("Enter parking price for four-wheeler vehicles for one hour: ");
        int pp4w1h = sc.nextInt();
        boolean k = true;
        do {
            System.out.println("Enter 1 to park a 2-wheeler vehicle");
            System.out.println("Enter 2 to park a 4-wheeler vehicle");
            System.out.println("Enter 3 to remove vehicle");
            System.out.println("Enter 4 to Exit");
            System.out.print("Enter Your choice : ");
            int numb = sc.nextInt();
            switch (numb) {
                case 1:
                    try {
                        for (int i = 0; i < fnum; i++) {
                            for (int j = 0; j < vehicle[i].length; j++) {
                                if (vehicle[i][j].vehid == 0) {
                                    vehicle[i][j].setData(conn);
                                    vehicle[i][j].vehicleType = "2-wheeler";
                                    vehicle[i][j].in_time = LocalDateTime.now();
                                    System.out.println("2-wheeler vehicle parked at Floor " + (i + 1) + " Parking " + (j + 1));
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    break;
                case 2:
                    try {
                        for (int i = 0; i < fnum; i++) {
                            for (int j = 0; j < vehicle[i].length; j++) {
                                if (vehicle[i][j].vehid == 0) {
                                    vehicle[i][j].setData(conn);
                                    vehicle[i][j].vehicleType = "4-wheeler";
                                    vehicle[i][j].in_time = LocalDateTime.now();
                                    System.out.println("4-wheeler vehicle parked at Floor " + (i + 1) + " Parking " + (j + 1));
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    break;
                case 3:
                    System.out.print("Enter the vehicle's last four digits of the number plate to remove: ");
                    int vehidToRemove = sc.nextInt();
                    VehicleInfo tempVehicle = new VehicleInfo(0, 0); 
                    if (tempVehicle.getVehicleData(conn, vehidToRemove)) {
                        tempVehicle.out_time = LocalDateTime.now();
                        long fee = 0;
                        if ("2-wheeler".equalsIgnoreCase(tempVehicle.vehicleType)) {
                            fee = tempVehicle.calculateParkingFee(pp2w1h);
                        } else if ("4-wheeler".equalsIgnoreCase(tempVehicle.vehicleType)) {
                            fee = tempVehicle.calculateParkingFee(pp4w1h);
                        }
                        try {
                            String updateSql = "UPDATE vehicles SET out_time = ? WHERE vehid = ?";
                            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                            updateStmt.setTimestamp(1, Timestamp.valueOf(tempVehicle.out_time));
                            updateStmt.setInt(2, vehidToRemove);
                            updateStmt.executeUpdate();
                            updateStmt.close();
                        } catch (SQLException e) {
                            System.err.println("Error updating out_time: " + e.getMessage());
                        }
                        System.out.println("Vehicle removed. Out time: " + tempVehicle.out_time);
                        System.out.println("Total parking fee: " + fee);
                        // Reset in-memory array spot (find by parkingid and floorid)
                        for (int i = 0; i < fnum; i++) {
                            for (int j = 0; j < vehicle[i].length; j++) {
                                if (vehicle[i][j].parkingid == tempVehicle.parkingid - 1 && vehicle[i][j].floorid == tempVehicle.floorid - 1) {
                                    vehicle[i][j].vehid = 0;
                                    break;
                                }
                            }
                        }
                    } else {
                        try {
                            throw new VehicleDoesnotExist("Vehicle does not exist in the database.");
                        } catch (VehicleDoesnotExist e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 4:
                    k = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (k);
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
