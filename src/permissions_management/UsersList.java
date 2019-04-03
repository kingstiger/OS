package permissions_management;
import java.util.ArrayList;

import files_catalogs_management.FilesCatalogsManager;
import files_catalogs_management.CatalogFile;
import files_catalogs_management.FileException;
import graphic_interface.Alerts.AlertUtilities;
import process_management.*;

public class UsersList
{
	
	public static UserAccount curr_user;
	public static ArrayList<UserAccount> users = new ArrayList<UserAccount>();
	private static int count_admin = 0;
	
	public static void init_users()
	{
		curr_user = new UserAccount("ADMIN",true);
		users.add(curr_user);
		count_admin++;
	}
	
	public static int find_user(String name)
	{
		for(int i=0;i<users.size();i++)
		{
			if(users.get(i).get_name().equals(name)) return i;
		}
		return -1;
	}
	
	public static void add_user(String name,boolean admin) throws UserException
	{
		if(UsersList.curr_user == null)
			throw new UserException("User is logged out");
		if(curr_user.is_admin())
		{
			if(find_user(name) == -1)
			{
				if(admin == false)
				{
					users.add(new UserAccount(name));
					System.out.println("Account created: "+ name);
				}

				else
				{
					users.add(new UserAccount(name,true));
					count_admin++;
					System.out.println("Account created: "+ name);
				}
			}
			else throw new UserException("Cannot create an account - existing account with name: " + name);
		}
		else throw new UserException("Cannot create an account - no administrator priviliges ");
	}
	
	public static void set_own_name(String newName) throws UserException
	{
		if(UsersList.curr_user==null)
			throw new UserException("User is logged out");
		for(UserAccount e : users)
		{
			if(e.get_name().equals(newName)) throw new UserException("This name is used by another account");
		}
		curr_user.set_name(newName);
		System.out.println("User name has been changed");
	}
	
	public static void set_new_name(String user_name, String newName) throws UserException
	{
		if(UsersList.curr_user==null)
			throw new UserException("User is logged out");
		if(curr_user.is_admin())
		{
			for(UserAccount e : users)
			{
				if(e.get_name().equals(newName)) throw new UserException("This name is used by another account");
			}
			users.get(find_user(user_name)).set_name(newName);
			System.out.println("User name has been changed");
		}
		else throw new UserException("Cannot change name - no administrator priviliges");
	}
	
	public static void delete_user(String name) throws UserException, PermissionException
	{
		if(UsersList.curr_user==null)
			throw new UserException("User is logged out");
		
		if(curr_user.get_name().equals(name)) 
		{
			int i = find_user(name);
			if(i == -1) throw new UserException("Invalid name");
			if(curr_user.is_admin() ) 
			{
				if(count_admin > 1)
				{
					for(CatalogFile e : FilesCatalogsManager.main_catalog)
					{
						e.file_users.delete_domain(UsersList.users.get(i));
					}
					count_admin--;
					logout();
					users.remove(i);
					System.out.println("Account: "+ name +" removed");
					return;
				}
				else throw new UserException("Cannot remove the admin last account");
			}
			else
			{
				for(CatalogFile e : FilesCatalogsManager.main_catalog)
				{
					e.file_users.delete_domain(UsersList.users.get(i));
				}
				logout();
				users.remove(i);
				System.out.println("Account: "+ name +" removed");
				return;
			}
		}
		
		if(curr_user.is_admin())
		{
		int i = find_user(name);
		if(i==-1) throw new UserException("Invalid name");
		if(users.get(i).is_admin()) 
			{
				if(count_admin > 1)
					{
						for(CatalogFile e : FilesCatalogsManager.main_catalog)
						{
							e.file_users.delete_domain(UsersList.users.get(i));
						}
						count_admin--;
						users.remove(i);
						System.out.println("Account: "+ name +" removed");
						return;
					}
				else throw new UserException("Cannot remove the admin last account");
			}
			else
			{
				for(CatalogFile e : FilesCatalogsManager.main_catalog)
				{
					e.file_users.delete_domain(UsersList.users.get(i));
				}
				users.remove(i);
				System.out.println("Account: "+ name +" removed");
				return;
			} 
		}
		else throw new UserException("Cannot remove account - no administrator priviliges");
	}
	
	public static void set_admin_mode(String name) throws UserException
	{
		if(UsersList.curr_user==null)
			throw new UserException("User is logged out");
		if(curr_user.is_admin())
		{
			int i = find_user(name);
			if(!users.get(i).is_admin())
				{
				users.get(i).set_type(UserAccount.status.admin);
				count_admin++;
				System.out.println("Set administrator priviliges to: " + name);
				}
			else throw new UserException("This user has already admin priviliges");
		}
		else throw new UserException("Cannot set administrator priviliges for this account:" + name + ". Logged user is not an administrator");
	}
	
	public static void set_normal_mode(String name) throws UserException//zdjecie admina
	{
		if(UsersList.curr_user==null)
			throw new UserException("User is logged out");
		if(curr_user.get_name().equals(name))
			{
			throw new UserException("Cannot remove administrator privileges for yourself");
			}
		if(curr_user.is_admin())
		{
			int i = find_user(name);
			if(users.get(i).is_admin())
			{
				if(count_admin > 1)
				{
					users.get(i).set_type(UserAccount.status.normal);
					count_admin--;
					System.out.println("Privileges removed from: " + name);
				}
			}
			else throw new UserException("This user is not an administrator");
		}
		else
		{
			throw new UserException("You have not permission to do this");
		}
	}
	
	public static void login(String name) throws UserException
	{
		if(curr_user==null)
		{
			int i = find_user(name);
			if(i!=-1)
			{
				curr_user = users.get(i);
				System.out.println("Logged in! Welcome " + name);
				AlertUtilities.logged.set(true);
			}
			else
			{
				throw new UserException("Invalid user name");
			}
		}
		else throw new UserException("User is already logged in");
	}
	
	//po wylogowaniu trzeba sie zalogowac, inaczej nikt nie moze nic zrobic
	public static void logout() throws UserException
	{
		if(UsersList.curr_user==null)
			throw new NullPointerException("User is already logged out");
		for(CatalogFile e : FilesCatalogsManager.main_catalog)
		{
			if(e.is_open())
			{
				try
				{
					FilesCatalogsManager.close_file(e.getFile_name());
				}
				catch(FileException ex)
				{
					throw new UserException("Cannot close file: " + e.getFile_name());
				}
			}
		}
		ProcessManager.terminateProcessAll();
		curr_user = null;
		AlertUtilities.logged.set(false);
		System.out.println("Logged out!");
	}
	
	//change
	public static void show_all_users()
	{
		if(UsersList.curr_user==null)
			throw new NullPointerException("User is already logged out");
		for(UserAccount e : users)
		{
			System.out.println(e.get_name() + " " + ((e.is_admin()) ? "(administrator)" : "" ));
		}
	}
	
	public static void show_curr_user() throws UserException
	{
		if(UsersList.curr_user==null)
			throw new NullPointerException("User is logged out");
		System.out.println("Logged in as: " + curr_user.get_name() + " " + ((curr_user.is_admin()) ? "(administrator)" : "" ));
	}

	//dodatkowe funckje, ktora przyda mi sie w GUI - Patryk
	public static ArrayList<String> usersList()
	{
		if(UsersList.curr_user==null)
			throw new NullPointerException("User is logged out");
		ArrayList<String> toReturn = new ArrayList<String>();
		for(UserAccount e : users)
		{
			toReturn.add(e.get_name());
		}
		return toReturn;
	}

	public static String userInfo()
	{
		if(UsersList.curr_user==null)
			throw new NullPointerException("User is logged out");
		String toReturn = curr_user.get_name() + "\t" + ((curr_user.is_admin()) ? "(administrator)" : "" );
		return toReturn;
	}

	public static String allUserInfo()
	{
		if(UsersList.curr_user==null)
			throw new NullPointerException("User is logged out");
		String toReturn="";
		for(UserAccount e : users)
		{
			toReturn += e.get_name() + "\t" + ((e.is_admin()) ? "(administrator)" : "" ) + System.lineSeparator();
		}
		return toReturn;
	}
	
}
