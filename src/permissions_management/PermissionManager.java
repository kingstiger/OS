package permissions_management;

public class PermissionManager
{
	/* Kontrola dostêpu do plików i konta u¿ytkowników 
	 * 
	 * Lista plików
	 * ka¿dy plik -> nazwa + lista(u¿ytkownik + lista(modyfikatorów dostêpu))
	 * Nie czepiac siê nazw !!!!
	 * Trochê tego jest, du¿o bêdzie w obs³udze plików, przy tworzeniu pliku, usuwaniu, edytowaniu etc.
	 * 
	 * 
	 * KOMENDY: 
	 * add(process p1) - dodaje plik do listy
	 * delete(process p1) - usuwa plik z listy
	 * show_process_list() - wyœwietla wszystko
	 * show_file_permission(String name) - wyœwietla prawa dostêpu dla wybranego pliku
	 * 
	 * add_domain_own(UserAccount name)-  dodaje u¿ytkownika + prawa dostêpu + prawo OWNER
	 * add_domain(UserAccount name) - dodaje u¿ytkownika
	 * delete_domain(UserAccount name) - usuwa u¿ytkownika
	 * 
	 * !!!! wa¿ne dla pana od plików i folderów !!!!!
	 * have_right(String name, AccessMode value) - czy u¿ytkownik posiada dane prawo (po wybraniu konkretnego pliku)
	 * 
	 * add_right(String name,AccessMode value) - dodaj do u¿ytkownika prawo
	 * copy_right(String name_from,String name_to,AccessMode value) - skopiuj prawo
	 * move_right(String name_from,String name_to,AccessMode value) - przenieœ prawo
	 * share_right(String name, AccessMode value) - nadaj prawo (dostepne dla admina)
	 * delete_right(String name,AccessMode value) - usuñ prawo
	 * 
	 */
	public static boolean stepWork(String[] command)
	{
		return false;
	}
	
	public void testy() throws PermissionException
	{
		UsersList ulist = new UsersList();
		//file_permission_list p1 =  new file_permission_list();
		FilePermission f1 = new FilePermission();
		FilePermission f2 = new FilePermission();
		//p1.add(f1);
		//p1.add(f2);
		//p1.add(new FilePermission("File3"));
		//p1.delete("File3");
			f1.add_domain_own(ulist.curr_user);
			if(ulist.curr_user.is_admin())
			{
				f1.add_right(ulist.curr_user.get_name(), AccessMode.PRINT);
			}
	}
		//p1.show_process_list();
}
