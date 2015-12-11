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
		ArrayList<Order> orders = new ArrayList<>();
		String query = "SELECT * FROM Orders "
				+ "JOIN Customer on Customer.CustomerID = Orders.CustomerID "
				+ "JOIN LineItems on LineItems.OrderID = Orders.OrderID"
				+ "ORDER BY LineItems.OrderID";
		try {
			ResultSet rs = getResults(query);
			if (rs != null) { 
				//result set exists, manipulate here
				int id = -1;
				double cost = 0.0;
				while(rs.next()){
					if (rs.getString("Status") == "pending") {
						System.out.println(rs.getInt("OrderID"));
						Order o = new Order();
						o.OrderID = rs.getInt("OrderID");
						o.Customer = new Customer();
						o.Customer.CustomerID = rs.getInt("CustomerID");
						o.Customer.Name = rs.getString("FirstName") + rs.getString("LastName");
						o.Customer.Email = rs.getString("Email");
						o.OrderDate = new Date();
						o.Status = rs.getString("Status");
						if (o.OrderID == id) {
							//Current lineitem row is in the same order as the previous one
							// -> add to cost
							cost +=  rs.getDouble("PricePaid") * rs.getInt("Quantity");
						} else {
							//This lineitem is for a different order than the previous one
							// -> overwrite cost
							cost =  rs.getDouble("PricePaid") * rs.getInt("Quantity");
						}
						
						//assign total cost
						o.TotalCost = cost;
						o.BillingAddress = rs.getString("BillingAddress");
						o.BillingInfo = rs.getString("BillingInfo");
						o.ShippingAddress= rs.getString("ShippingAddress");
						id = o.OrderID;
						orders.add(o);
					}
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		//return order array
		return orders.toArray(new Order[orders.size()]);
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
					p.InStock = rs.getInt("QuantityOnHand");
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
