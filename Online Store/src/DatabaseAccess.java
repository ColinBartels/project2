import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;


public class DatabaseAccess {

	private static ResultSet getResults(String query) throws SQLException {
		// Access database and run query

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			//Set login info here
			String url = "jdbc:sqlserver://is-fleming.ischool.uw.edu";
			String user = "perry";
			String pass = "Info340C";

			Connection conn = DriverManager.getConnection(url, user, pass);

			//Set database here
			conn.setCatalog("Store");

			//Call query and store in memory as rs
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			return rs;

		} catch (Exception e) {
			e.printStackTrace();
		}

		//Result set failed, return null
		return null;
	}
	
	public static Order [] GetPendingOrders()
	{
		ArrayList <Order> pendingOrders = new ArrayList<>();
		String query = "SELECT * FROM Customer";

		try {
			ResultSet rs = getResults(query);
			if (rs != null) {
				//result set exists, manipulate here
				while(rs.next()){
					Order o = new Order();
					o.OrderID = 1;
					o.Customer = new Customer();
					o.Customer.CustomerID = 1;
					o.Customer.Name = "Kevin";
					o.Customer.Email = "kevin@pathology.washington.edu";
					o.OrderDate = new Date();
					o.Status = "ORDERED";
					o.TotalCost = 520.20;
					o.BillingAddress = "1959 NE Pacific St, Seattle, WA 98195";
					o.BillingInfo	 = "PO 12345";
					o.ShippingAddress= "1959 NE Pacific St, Seattle, WA 98195";
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		//return array of customers
				return pendingOrders.toArray(new Order[pendingOrders.size()]);
	}
	
		// TODO:  Retrieve all the information about the products.
	public static Product[] GetProducts()
	{
		ArrayList <Product> products = new ArrayList<>();
		String query = "SELECT * FROM Product";
			
		try {
				ResultSet rs = getResults(query);
				if (rs != null) {
					while(rs.next()) {
						Product p = new Product();
						p.ProductID = rs.getInt("ItemID");
						p.Name = rs.getString("Name");
						p.Price = rs.getDouble("Cost");
						p.Description = rs.getString("Description");
                  p.InStock = rs.getInt("QualityOnHand");
						products.add(p);
					}
				}
			} catch (SQLException e) {
					e.printStackTrace();
			}
				return products.toArray(new Product[products.size()]);
			}

	public static Order GetOrderDetails(int OrderID)
	{
		// TODO:  Query the database to get the flight information as well as all 
		// the reservations.
		
		ArrayList <Customer> customers = new ArrayList<>();
		String query = "SELECT * FROM Customer";

		try {
			ResultSet rs = getResults(query);
			if (rs != null) {
				//result set exists, manipulate here
				while(rs.next()){
					Customer c = new Customer();
					c.CustomerID = rs.getInt("CustomerID");
					c.Name = rs.getString("FirstName") + " " + rs.getString("LastName");
					c.Email = rs.getString("Email");
					customers.add(c);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		//return array of customers
		//return customers.toArray(new Customer[customers.size()]);
		return null;
	}

	public static Product GetProductDetails (int ProductID)
	{
		Product p = new Product();
		p.Description = "A great monitor";
		p.Name = "Monitor, 19 in";
		p.InStock = 10;
		p.Price = 196;
		p.ProductID = ProductID;
		p.UserComments = new String [] { "I bought this product last year and it's still the best monitor I've had.", "After 6 months the color started going out, not sure if it was just mine or all of them" };
		
		return p;
		
	}
	
	public static Customer [] GetCustomers () {
		ArrayList <Customer> customers = new ArrayList<>();
		String query = "SELECT * FROM Customer";

		try {
			ResultSet rs = getResults(query);
			if (rs != null) {
				//result set exists, manipulate here
				while(rs.next()){
					Customer c = new Customer();
					c.CustomerID = rs.getInt("CustomerID");
					c.Name = rs.getString("FirstName") + " " + rs.getString("LastName");
					c.Email = rs.getString("Email");
					customers.add(c);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		//return array of customers
		return customers.toArray(new Customer[customers.size()]);
	}
	
	public static Order [] GetCustomerOrders (Customer c)
	{
		Order o = new Order();
		o.OrderID = 1;
		o.Customer = new Customer();
		o.Customer.CustomerID = 1;
		o.Customer.Name = "Kevin";
		o.Customer.Email = "kevin@pathology.washington.edu";
		o.OrderDate = new Date();
		o.Status = "ORDERED";
		o.TotalCost = 520.20;
		o.BillingAddress = "1959 NE Pacific St, Seattle, WA 98195";
		o.BillingInfo	 = "PO 12345";
		o.ShippingAddress= "1959 NE Pacific St, Seattle, WA 98195";

		return new Order [] { o };
	}
	
	public static Product [] SearchProductReviews(String query)
	{
		// DUMMY VALUES
		Product p = new Product();
		p.Description = "A great monitor";
		p.Name = "Monitor, 19 in";
		p.InStock = 10;
		p.Price = 196;
		p.ProductID = 1;
		p.Relavance = 0.7;
		return new Product [] { p} ;
	}
	                    
	public static void MakeOrder(Customer c, LineItem [] LineItems)
	{
		// TODO: Insert data into your database.
		// Show an error message if you can not make the reservation.
		
		JOptionPane.showMessageDialog(null, "Create order for " + c.Name + " for " + Integer.toString(LineItems.length) + " items.");
	}
}
