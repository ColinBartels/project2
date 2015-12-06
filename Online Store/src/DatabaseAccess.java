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
		Order[] pendingOrders = new Order[]{};
		String query = "SELECT * FROM Customer";
		try {
			ResultSet rs = getResults(query);
			if (rs != null) { //result set exists, manipulate here


				//While results has next, print name
				/*while(rs.next()){
					System.out.print(rs.getString("name"));
					System.out.println();
				}
				*/

			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		// DUMMY DATA!
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

		return new Order[]{ o };
	}
	
	public static Product[] GetProducts()
	{
		// TODO:  Retrieve all the information about the products.
		
		// DUMMY VALUES
		Product p = new Product();
		p.Description = "A great monitor";
		p.Name = "Monitor, 19 in";
		p.InStock = 10;
		p.Price = 196;
		p.ProductID = 1;
		return new Product [] { p } ;
	}

	public static Order GetOrderDetails(int OrderID)
	{
		// TODO:  Query the database to get the flight information as well as all 
		// the reservations.
		
		String query = "SELECT * FROM Orders WHERE OrderID = " + OrderID;
		Order o = new Order();

		try {
			ResultSet rs = getResults(query);
			if (rs != null) {
				//result set exists, manipulate here
				while(rs.next()){
					o.OrderID = rs.getInt("OrderID");
					o.Status = rs.getString("Status");
					o.Customer = new Customer(); 	//Dummy
					o.TotalCost = 0.0; 				//Dummy
					o.LineItems = new LineItem[1]; 	//Dummy
					o.ShippingAddress = rs.getString("ShippingAddress");
					o.BillingAddress = rs.getString("BillingAddress");
					o.BillingInfo = rs.getString("BillingInfo");
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		//return array of customers
		return o;
	}

	public static Product GetProductDetails (int ProductID)	{
		String query = "SELECT * FROM Products WHERE ItemID = " + ProductID + 
				" JOIN Comments ON Comments.ProductID = Products.ItemID";
		Product p = new Product();
		ArrayList<String> comments = new ArrayList<>();
		
		try {
			ResultSet rs = getResults(query);
			if (rs != null) {
				//result set exists, manipulate here
				while(rs.next()){
					p.ProductID = rs.getInt("ItemID");
					p.InStock = 0; //Dummy
					p.Name = rs.getString("Name");
					p.Price = rs.getDouble("Cost");
					p.Description = rs.getString("Description");
					comments.add(rs.getString("Comment Text"));
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		p.UserComments = comments.toArray(new String[comments.size()]);
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
