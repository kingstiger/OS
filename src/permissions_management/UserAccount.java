package permissions_management;
public class UserAccount
{

	public enum status {admin, normal };
	private String user_name;
	//private String password;
	private status type; //admin, normal, ...
	//admina nie mozna usunac, musi byc co najmniej jeden
	
	public UserAccount(String name)
	{
		  this.user_name = name;
		  this.type = status.normal;
		  ///this.user_name = password;
	}
	
	public UserAccount(String name, boolean admin)
	{
		  this.user_name = name;
		 /// this.user_name = password;
		  if(admin == true) this.type = status.admin;
		  else this.type = status.admin;
	}
	
	public String get_name()
	{
		return this.user_name;
	}
	
	public void set_name(String new_name)
	{
		user_name = new_name;
	}
	
	public void set_type(status value)
	{
		this.type = value;
	}
	
	public boolean is_admin() // true, gdy jest adminem
	{
		return type == status.admin;
	}
	
}
