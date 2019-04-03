package permissions_management;
import java.util.ArrayList;

/*nazwa pliku (nie mo�e si� powt�rzyc)
 * lista u�ytkownik�w z dostepami do pliku
 * admin ma z definicji ca�y dost�p
 */

public class FilePermission
{
	public ArrayList <DomainAccessList> lista = new ArrayList<DomainAccessList>();
	
	public FilePermission(){}
	
	public boolean is_owner(String user_name)
	{
		for(int i=0;i<lista.size();i++)
		{
			if(lista.get(i).user.get_name().equals(user_name)) 
			{
				if(lista.get(i).access_collection.contains(AccessMode.OWNER)) return true;
				else return false;
			}
		}
		return false;
	}
	
	public boolean have_right(String user_name, AccessMode value)
	{
		int n = -1;
		for(int i=0;i<lista.size();i++)
		{
			if(lista.get(i).user.get_name().equals(user_name)) 
			{
				n = i;
				break;
			}
		}
		if(n == -1) return false;
		switch(value)
		{
		case READ:
		{
			if(lista.get(n).access_collection.contains(AccessMode.READ) ||
					lista.get(n).access_collection.contains(AccessMode.READ_COPY) ) return true;
			return false;
		}
		case EXECUTE:
		{
			if(lista.get(n).access_collection.contains(AccessMode.EXECUTE) ||
					lista.get(n).access_collection.contains(AccessMode.EXECUTE_COPY) ) return true;
			return false;
		}
		case WRITE:
		{
			if(lista.get(n).access_collection.contains(AccessMode.WRITE) ||
					lista.get(n).access_collection.contains(AccessMode.WRITE_COPY) ) return true;
			return false;
		}
		default:
		{
			if(lista.get(n).access_collection.contains(value)) return true;
			return false;
		}
		}
	}
	
	public boolean exist_name(String name)
	{
		for(DomainAccessList e : lista)
			{
				if(e.user.get_name().equals(name)) return true;
			}
		return false;
	}
	
	public void add_domain_own(UserAccount name) throws PermissionException
	{
		if(!exist_name(name.get_name()))
			{
			lista.add(new DomainAccessList(name, AccessMode.OWNER));
			lista.get(lista.size()-1).add_right(AccessMode.READ_COPY);
			lista.get(lista.size()-1).add_right(AccessMode.WRITE_COPY);
			lista.get(lista.size()-1).add_right(AccessMode.EXECUTE_COPY);
			System.out.println("Add owner to access list");
			}
		else throw new PermissionException("Invalid name");
	}
	
	public void add_domain(UserAccount name) throws PermissionException
	{
		if(!exist_name(name.get_name()))
			{
				lista.add(new DomainAccessList(name));
				System.out.println("Add new user to access list");
			}
		else throw new PermissionException("Invalid name");
	}
	
	public void delete_domain(UserAccount name) throws PermissionException
	{
		for(int i=0;i<lista.size();i++)
		{
			if(lista.get(i).get_name().equals(name.get_name()))
			{
				lista.remove(i);
				System.out.println("Deleted user from access list");
				return;
			}	
		}
	}
	
	
	public void add_right(String name, AccessMode value) throws PermissionException
	{
		for(int i=0;i<lista.size();i++)
		{
			if(lista.get(i).get_name().equals(name))
			{
				lista.get(i).add_right(value);
				System.out.println("Add " + value + "mode to " + name);
				return;
			}
		}
	}
	
	public void copy_right(String name_from, String name_to, AccessMode value) throws PermissionException
	{
		int from = -1;
		int to = -1;
		for(int i=0;i<lista.size();i++)
		{
			if(lista.get(i).get_name().equals(name_from))
			{
				from = i;
			}
			if(lista.get(i).get_name().equals(name_to))
			{
				to = i;
			}
		}
		if(from == to) throw new PermissionException("The user names are same");
		if(from == -1) throw new PermissionException("Cannot find owner user name");
		if(to == -1) 
		{
			try 
			{ 
				add_domain(UsersList.users.get(UsersList.find_user(name_to)));
			}
			catch(Exception e) 
			{
				throw new PermissionException("Cannot find user named: " + name_to);
			}
			to = lista.size()-1;
		}
		if((UsersList.curr_user.get_name().equals(name_from) && is_owner(name_from)))
		{
		switch(value)
		{
		case READ: 
		{
			if(lista.get(from).access_collection.contains(AccessMode.READ_COPY))
			{
				//System.out.println(AccessMode.READ_COPY);
				if(!lista.get(to).access_collection.contains(value) && !lista.get(to).access_collection.contains(AccessMode.READ_COPY)) 
					{
						lista.get(to).access_collection.add(value);
						System.out.println("Copy READ mode to " + name_to);
					}
			}
		};break;
		case WRITE:
		{
			if(lista.get(from).access_collection.contains(AccessMode.WRITE_COPY))
			{
				//System.out.println(AccessMode.WRITE_COPY);
				if(!lista.get(to).access_collection.contains(value) && !lista.get(to).access_collection.contains(AccessMode.WRITE_COPY)) 
					{
						lista.get(to).access_collection.add(value);
						System.out.println("Copy WRITE mode to " + name_to);
					}
			}
		};break;
		case EXECUTE:
		{
			if(lista.get(from).access_collection.contains(AccessMode.EXECUTE_COPY))
			{
				//System.out.println(AccessMode.EXECUTE_COPY);
				if(!lista.get(to).access_collection.contains(value) && !lista.get(to).access_collection.contains(AccessMode.EXECUTE_COPY)) 
					{
						lista.get(to).access_collection.add(value);
						System.out.println("Copy EXECUTE mode to " + name_to);
					}
			}
		}
		default: break;
		}
		}
	}
	
	public void move_right(String name_from, String name_to, AccessMode value) throws PermissionException
	{
		int from = -1;
		int to = -1;
		for(int i=0;i<lista.size();i++)
		{
			if(lista.get(i).get_name().equals(name_from))
			{
				from = i;
			}
			if(lista.get(i).get_name().equals(name_to))
			{
				to = i;
			}
		}
		if(from == to) throw new PermissionException("The user names are same");
		if(from == -1) throw new PermissionException("Cannot find owner user name");
		if(to == -1)
			{
			try 
			{ 
				add_domain(UsersList.users.get(UsersList.find_user(name_to)));
			}
			catch(Exception e) 
			{
				throw new PermissionException("Cannot find user named: " + name_to);
			}
			to = lista.size()-1;
			}
		if((UsersList.curr_user.get_name().equals(name_from) && is_owner(name_from)))
		{
		switch(value)
		{
		case READ: 
		{
			if(lista.get(from).access_collection.contains(AccessMode.READ_COPY))
			{
			//System.out.println(AccessMode.READ_COPY);
				
					lista.get(to).access_collection.add(AccessMode.READ_COPY);
					lista.get(to).access_collection.remove(AccessMode.READ);
					lista.get(from).access_collection.remove(AccessMode.READ_COPY);
					System.out.println("Moved READ mode to " + name_to);
			}
		};break;
		case WRITE:
		{
			if(lista.get(from).access_collection.contains(AccessMode.WRITE_COPY))
			{
				//System.out.println(AccessMode.WRITE_COPY);
				lista.get(to).access_collection.add(AccessMode.WRITE_COPY);
				lista.get(to).access_collection.remove(AccessMode.WRITE);
				lista.get(from).access_collection.remove(AccessMode.WRITE_COPY);
				System.out.println("Moved WRITE mode to " + name_to);
			}
		};break;
		case EXECUTE:
		{
			if(lista.get(from).access_collection.contains(AccessMode.EXECUTE_COPY))
			{
				//System.out.println(AccessMode.EXECUTE_COPY);
				
				lista.get(to).access_collection.add(AccessMode.EXECUTE_COPY);
				lista.get(to).access_collection.remove(AccessMode.EXECUTE);
				lista.get(from).access_collection.remove(AccessMode.EXECUTE_COPY);
				System.out.println("Moved EXECUTE mode to " + name_to);
			}
		}
		default: break;
		}
		}
	}
	
	public void share_right(String name, AccessMode value) throws PermissionException
	{
		if(UsersList.curr_user.is_admin())
		{
			int to = -1;
			for(int i=0;i<lista.size();i++)
			{
				if(lista.get(i).get_name().equals(name))
				{
					to = i;
				}
			}
			if(to == -1) 
			{
				try 
				{ 
					add_domain(UsersList.users.get(UsersList.find_user(name)));
					to = lista.size() - 1;
				}
				catch(Exception e) 
				{
					throw new PermissionException("Cannot find user named: " + name);
				}
			}
			switch(value)
			{
			case READ: 
			{
				lista.get(to).add_right(AccessMode.READ);
				System.out.println("Shared READ mode to " + name);
			};break;
			case WRITE:
			{
				lista.get(to).add_right(AccessMode.WRITE);
				System.out.println("Shared WRITE mode to " + name);
			};break;
			case EXECUTE:
			{
				lista.get(to).add_right(AccessMode.EXECUTE);
				System.out.println("Shared EXECUTE mode to " + name);
			}
			default: break;
			}
		}
	}
	
	public void delete_right(String name, AccessMode value) throws PermissionException
	{
		int n = -1;
		if( UsersList.curr_user.is_admin() || is_owner(UsersList.curr_user.get_name()))
		{
			for(int i=0;i<lista.size();i++)
			{
				if(lista.get(i).get_name().equals(name))
				{
					n = i;
					break;
				}
			}
			if(n >= 0) 
				{
					if(lista.get(n).access_collection.contains(AccessMode.OWNER))
					{
						throw new PermissionException("Cannot delete right from owner of the file");
					}
					else
					{
						switch(value)
						{
						case READ: 
						{
							lista.get(n).remove_right(AccessMode.READ);
							lista.get(n).remove_right(AccessMode.READ_COPY);
							System.out.println("Delete READ mode from " + name);
						};break;
						case WRITE:
						{
							lista.get(n).remove_right(AccessMode.WRITE);
							lista.get(n).remove_right(AccessMode.WRITE_COPY);
							System.out.println("Delete WRITE mode fromo " + name);
						};break;
						case EXECUTE:
						{
							lista.get(n).remove_right(AccessMode.EXECUTE);
							lista.get(n).remove_right(AccessMode.EXECUTE_COPY);
							System.out.println("Delete EXECUTE mode from " + name);
						}
						default: break;
						}
					}
				}
			else throw new PermissionException("Invalid name");
		}
		else throw new PermissionException("You have no permission to delete right");
	}
	
	public String show_access_list()
	{
		String toReturn = new String();
		for(DomainAccessList e : lista)
		{
			toReturn += e.show_right();
		}
		return toReturn;
	}
}
