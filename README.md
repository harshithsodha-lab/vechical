import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
class VehicleOverloaded extends Exception
{
	VehicleOverloaded(String s)
	{
		super(s);
	}
}
class VehicleDoesnotExist extends Exception
{
	VehicleDoesnotExist(String s)
	{
		super(s);
	}
}
abstract class vehilces
{
	String ownername;
	String vehiclename;
	int vehid;
	long m_no;
	int parkingid;
    int floorid;
	String vehicleType;
}
class VehicleInfo extends vehilces
{
    LocalDateTime in_time;
    LocalDateTime out_time;
    Scanner sc = new Scanner(System.in);
    VehicleInfo(int a, int b) 
	{
        ownername = "Empty";
        vehiclename = "Empty";
        vehid = 0;
        m_no = 0;
        parkingid = a;
        floorid = b;
        vehicleType = "Unknown";
    }
    void setData() 
	{
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
        System.out.println("Your floor ID is " + (floorid+1));
        System.out.println("Your Parking ID is " + (parkingid+1));
    }
    long calculateParkingFee(int pricePerHour) 
	{
        Duration duration = Duration.between(in_time, out_time);
        long hoursParked = duration.toHours();
        if (duration.toMinutes() % 60 != 0) 
		{
            hoursParked++;
        }
        return hoursParked * pricePerHour;
    }
}
class Main
{
    public static void main(String args[]) 
	{	
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Parking Management System");
        System.out.println("");
        System.out.println("This system is managed by the gatekeeper");
        System.out.println("");
        System.out.print("Enter the number of floors: ");
        int fnum = sc.nextInt();
        VehicleInfo[][] vehicle = new VehicleInfo[fnum][];
        for (int i = 0; i < fnum ; i++) 
		{
            System.out.print("Enter the number of Two-wheeler vehicles that can park (positive integer): ");
            int w2 = sc.nextInt();
            System.out.print("Enter the number of Four-wheeler vehicles that can park (positive integer): ");
            int w4 = sc.nextInt();
            int tw = w2 + w4;
            vehicle[i] = new VehicleInfo[tw];
        }
        for (int i = 0; i < fnum; i++) 
		{
            for (int j = 0; j < vehicle[i].length; j++) 
			{
                vehicle[i][j] = new VehicleInfo(j, i);
            }
        }
        System.out.print("Enter parking price for two-wheeler vehicles for one hour: ");
        int pp2w1h = sc.nextInt();
        System.out.print("Enter parking price for four-wheeler vehicles for one hour: ");
        int pp4w1h = sc.nextInt();
        boolean k = true;
        do 
		{
			System.out.println("Enter 1 to park a 2-wheeler vehicle");
            System.out.println("Enter 2 to park a 4-wheeler vehicle");
            System.out.println("Enter 3 to remove vehicle");
            System.out.println("Enter 4 to Exit");
            System.out.print("Enter Your choice : ");
            int numb = sc.nextInt();
            switch (numb) {
                case 1:
				try{
                    for (int i = 0; i < fnum; i++) 
					{
                        for (int j = 0; j < vehicle[i].length; j++) 
						{
                            if (vehicle[i][j].vehid == 0) 
							{
                                vehicle[i][j].setData();
                                vehicle[i][j].in_time = LocalDateTime.now();
                                System.out.println("2-wheeler vehicle parked at Floor " + (i+1) + " Parking " + (j+1));
                                break;
                            }
                        }
					}
				}
				catch(Exception e)
				{
					e.getMessage();
				}
                    break;
                case 2:
				try 
				{
                    for (int i = 0; i < fnum; i++) {
                        for (int j = 0; j < vehicle[i].length; j++) {
                            if (vehicle[i][j].vehid == 0) {
                                vehicle[i][j].setData();
                                vehicle[i][j].in_time = LocalDateTime.now();
                                System.out.println("4-wheeler vehicle parked at Floor " + i + " Parking " + j);
                                break;
                            }
                        }
                    }
				}
				catch(Exception e)
				{
					e.getMessage();
				}
                    break;
                case 3:
                    System.out.println("Enter the parking floor and space to remove vehicle (Floor ID and Parking ID): ");
                    int floorToRemove = sc.nextInt();
                    int parkToRemove = sc.nextInt();
                    if (vehicle[floorToRemove][parkToRemove].vehid != 0) 
					{
                        vehicle[floorToRemove][parkToRemove].out_time = LocalDateTime.now();
                        long fee = 0;
                        if ("2-wheeler".equalsIgnoreCase(vehicle[floorToRemove][parkToRemove].vehicleType)) 
						{
                            fee = vehicle[floorToRemove][parkToRemove].calculateParkingFee(pp2w1h);
                        }
						else if ("4-wheeler".equalsIgnoreCase(vehicle[floorToRemove][parkToRemove].vehicleType)) 
						{
                            fee = vehicle[floorToRemove][parkToRemove].calculateParkingFee(pp4w1h);
                        }
                        System.out.println("Vehicle removed. Out time: " + vehicle[floorToRemove][parkToRemove].out_time);
                        System.out.println("Total parking fee: " + fee);
                        vehicle[floorToRemove][parkToRemove].vehid = 0;
                    } else {
						try{
                        System.out.println("No vehicle found at this parking spot.");
						throw new VehicleDoesnotExist("There doesnot exist the vehicle/Car");
						}
						catch(Exception e)
						{
							e.getMessage();
						}
					
                    }
                    break;
                case 4:
                    k = false;
                    System.out.println("Exiting the Parking Management System.");
                    break;
                default:
                    System.out.println("Invalid input! Please choose a valid option.");
                    break;
            }
        } while (k);
    }
}
