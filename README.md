# Online Store Database Implementation
##Backend
This GUI serves as a front end to the MSSQL DB my group implemented on our class' server. The database keeps track of products, orders, and customers. Products consist of information about the product and its price. Orders consist of billing and shipping information, products and quantity, and the customer who is ordering. Customers have accounts on our system and will want to track their orders as well as have a “wish list”.
##Java front-end
The program implements Object-Relation Mapping (ORM) from the database to the java classes. The user can see their order history, wishlist, availible products, and can make dummy "orders" that show up in their account. 
##Security
Concurrency issues are addressed when dealing with INSERT statements, and prepared statements prevent SQL injection.

