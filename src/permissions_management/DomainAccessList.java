package permissions_management;
import java.util.ArrayList;

//uzytkownik + lista modyfikatorow dostepu

public class DomainAccessList
{
	public UserAccount user;
	public ArrayList<AccessMode> access_collection = new ArrayList<AccessMode>();
	
	DomainAccessList(UserAccount name)
	{
		this.user = name;
	}
	
	DomainAccessList(UserAccount name, ArrayList<AccessMode> l)
	{
		this.user =  name;
		this.access_collection = l;
	}
	
	DomainAccessList(UserAccount name, AccessMode mode)
	{
		this.user = name;
		this.access_collection.add(mode);
	}
	
	public String get_name()
	{
		return this.user.get_name();
	}
	
	public void add_right(AccessMode right)
	{
		if(!access_collection.contains(right)) access_collection.add(right);
	}
	
	public void remove_right(AccessMode right)
	{
		if(access_collection.contains(right)) access_collection.remove(right);
	}
	
	public String show_right()
	{
		String toReturn = new String();
		toReturn += user.get_name() + " ";
		for(AccessMode e : access_collection)
		{
			toReturn += (e.name() + " ");
		}
		toReturn += "\n";
	return toReturn;
	}

}
