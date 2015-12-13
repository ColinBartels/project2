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
				+ "JOIN LineItems on LineItems.OrderID = Orders.OrderID "
				+ "WHERE Orders.Status = 'Pending' "
				+ "ORDER BY LineItems.OrderID";
		try {
			ResultSet rs = getResults(query);
			if (rs != null) { 
				//result set exists, manipulate here
				int id = -1;
				double cost = 0.0;
				while(rs.next()){
						Order o = new Order();
						o.OrderID = rs.getInt("OrderID");
						o.Customer = new Customer();
						o.Customer.CustomerID = rs.getInt("CustomerID");
						o.Customer.Name = rs.getString("FirstName") + rs.getString("LastName");
						o.Customer.Email = rs.getString("Email");
						o.OrderDate = rs.getDate("OrderDate");
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
			
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		//return order array
		return orders.toArray(new Order[orders.size()]);
	}
	
	public static Product[] GetProducts()
	{
		ArrayList <Product> products = new ArrayList<>();
		String query = "SELECT * FROM Products";

		try {
			ResultSet rs = getResults(query);
			if (rs != null) {
				while(rs.next()) {
					Product p = new Product();
					p.ProductID = rs.getInt("ItemID");
					p.Name = rs.getString("Name");
					p.Price = rs.getDouble("Cost");
					p.Description = rs.getString("Description");
					p.InStock = rs.getInt("QuantityOnHand");
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
		String query = "SELECT * FROM Orders "
				+ "JOIN Customer on Customer.CustomerID = Orders.CustomerID "
				+ "JOIN LineItems on LineItems.OrderID = Orders.OrderID "
				+ "JOIN Products on Products.ItemID = LineItems.ProductID "
				+ "JOIN Comments on Comments.ProductID = Products.ItemID "
				+ "WHERE Orders.OrderID = " + OrderID
				+ " ORDER BY Comments.ProductID";
		
		Order o = new Order();

		try {
			ResultSet rs = getResults(query);
			if (rs != null) { 
				//result set exists, manipulate here
				double cost = 0.0;
				ArrayList<LineItem> items = new ArrayList<>();
				ArrayList<String> comments = new ArrayList<>();
				int commentId = -1;
				while(rs.next()){
					System.out.println(rs.getInt("OrderID"));
					o.OrderID = rs.getInt("OrderID");
					o.Customer = new Customer();
					o.Customer.CustomerID = rs.getInt("CustomerID");
					o.Customer.Name = rs.getString("FirstName") + rs.getString("LastName");
					o.Customer.Email = rs.getString("Email");
					o.OrderDate = rs.getDate("OrderDate");
					o.Status = rs.getString("Status");
					cost +=  rs.getDouble("PricePaid") * rs.getInt("Quantity");
					
					//assign total cost
					o.TotalCost = cost;
					o.BillingAddress = rs.getString("BillingAddress");
					o.BillingInfo = rs.getString("BillingInfo");
					o.ShippingAddress= rs.getString("ShippingAddress");
					
					//build up the lineitems
					LineItem li = new LineItem();
					li.Order = o;
					li.Quantity = rs.getInt("Quantity");
					li.PricePaid = rs.getDouble("PricePaid");
					
					//products to append to lineitem
					Product p = new Product();
					p.ProductID = rs.getInt("ItemID");
					p.InStock = rs.getInt("QuantityOnHand");
					p.Name = rs.getString("Name");
					p.Price = rs.getDouble("Cost");
					p.Description = rs.getString("Description");
					
					//comments to append to product
					String comment = rs.getString("CommentText");
					if (rs.getInt("ProductID") == commentId) {
						//comment is for the same product as previous
						comments.add(comment);
						p.UserComments = comments.toArray(new String[comments.size()]);
					} else {
						//comment for different product
						p.UserComments = new String[] {comment};
					}	
					li.Product = p;
					items.add(li);
				}
				o.LineItems = items.toArray(new LineItem[items.size()]);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		//return order array
		return o;
	}

	public static Product GetProductDetails (int ProductID)	{
		String query = "SELECT * FROM Products" + 
				" JOIN Comments ON Comments.ProductID = Products.ItemID" + 
				" WHERE ItemID = " + ProductID;
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
					comments.add(rs.getString("CommentText"));
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
		ArrayList <Order> orders = new ArrayList<>();
		String query = "SELECT * FROM Orders "
				+ "JOIN Customer on Customer.CustomerID = Orders.CustomerID "
				+ "JOIN LineItems on LineItems.OrderID = Orders.OrderID "
				+ "ORDER BY LineItems.OrderID";
		try {
			ResultSet rs = getResults(query);
			if (rs != null) { 
				//result set exists, manipulate here
				int id = -1;
				double cost = 0.0;
				while(rs.next()){
					if (rs.getInt("CustomerID") == c.CustomerID) {
						Order o = new Order();
						o.OrderID = rs.getInt("OrderID");
						o.Customer = new Customer();
						o.Customer.CustomerID = rs.getInt("CustomerID");
						o.Customer.Name = rs.getString("FirstName") + rs.getString("LastName");
						o.Customer.Email = rs.getString("Email");
						o.OrderDate = rs.getDate("OrderDate");
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
		   //return order
		   return orders.toArray(new Order[orders.size()]);
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
	                    
	public static void MakeOrder(Customer c, LineItem [] LineItems)	{
		// Show an error message if you can not make the transaction
		String query = "SELECT AddressRecord FROM Customer WHERE Customer.CustomerID = " + c.CustomerID;
		String address = "";
		Timestamp OrderDate = new Timestamp(new Date().getTime());
		String Status = "Pending";
		String BillingAddress = "";
		String ShippingAddress = "";
		String BillingInfo = "Visa";
				
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			//Set login info here
			String url = "jdbc:sqlserver://is-fleming.ischool.uw.edu";
			String user = "perry";
			String pass = "Info340C";

			Connection conn = DriverManager.getConnection(url, user, pass);
			
			try {
				conn.setAutoCommit(false);
	
				//Set database here
				conn.setCatalog("Store");
	
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				rs.next();
				address = rs.getString("AddressRecord");
	        	BillingAddress = address;
	        	ShippingAddress = address;
				
				String insertTableSQL = "INSERT INTO Orders"
						+ " (OrderDate, BillingAddress, BillingInfo, ShippingAddress, Status, CustomerID) VALUES"
						+ " (?,?,?,?,?,?)";
				PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
				preparedStatement.setTimestamp(1, OrderDate);
				preparedStatement.setString(2, BillingAddress);
				preparedStatement.setString(3, BillingInfo);
				preparedStatement.setString(4, ShippingAddress);
				preparedStatement.setString(5, Status);
				preparedStatement.setInt(6, c.CustomerID);
				preparedStatement.executeUpdate();
				
				conn.commit();
				JOptionPane.showMessageDialog(null, "Create order for " + c.Name + " for " + Integer.toString(LineItems.length) + " items.");
			}catch(SQLException se) {
				conn.rollback();
				JOptionPane.showMessageDialog(null, "Order Insert Failed");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
