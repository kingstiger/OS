package files_catalogs_management;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.lang.String;

import process_management.*;
import permissions_management.*;
import processor_management.SrtAlgorithm;

/*
disc_size - rozmiar dysku w bajtach
block_size - rozmiar bloku w bajtach
block_number - ilość bloków
main_catalog - katalog główny
FAT -tablica FAT

Metody podstawowe:
check_file_exist - sprawdza czy istnieje dany plik
check_space  - sprawdza czy jest miejsce na plik
first_empty_block - zwraca adres pierwwszego wolnego bloku z tablicy FAT

create_file - tworzy plik
rename_file - zmiana nazwy
read_file - odczytuje plik i zraca zawartość typu string
delate_file - usuwa plik

plus jakieś funkcje doo sprawdzania czy my się dostęp do otwarcia usunięcia danego pliku
*/
public class FilesCatalogsManager
{

    private static int disc_size = 1024;
    private static int block_size = 32;
    private static int block_number = disc_size / block_size;
    private static byte[] hdd = new byte[disc_size];
    public static List<CatalogFile> main_catalog = new ArrayList<CatalogFile>();
    private static int FAT[] = new int[32];


    public static void generate()
    {
        for (int i = 0; i < disc_size; i++) hdd[i] = '0';
        for (int i = 0; i < 32; i++) FAT[i] = -2;

    }


    public static int check_catalog_index(String name)
    {
        if (check_file_exist(name))
        {
            for (int i = 0; i < main_catalog.size(); i++)
            {
                if (main_catalog.get(i).getFile_name().equals(name))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int get_size_of_file(String name)
    {
        if (check_file_exist(name))
        {
            int x = main_catalog.get(check_catalog_index(name)).getSize();
            return x;
        }
        return 0;
    }
    public static int get_ind(String name)
    {
        if (check_file_exist(name))
        {
            int x = main_catalog.get(check_catalog_index(name)).getIndicator();
            return x;
        }
        else
        {
            System.out.println("[FilesCatalogsManager]There is no file of this name");
        }
        return 0;
    }
    public static void set_ind(String name,int x)
    {
        if (check_file_exist(name))
        {
            main_catalog.get(check_catalog_index(name)).setIndicator(x);
        }
        else
        {
            System.out.println("[FilesCatalogsManager]There is no file of this name");
        }
    }

    public static boolean check_file_exist(String file_name)
    {
        for (int i = 0; i < main_catalog.size(); i++)
        {//przegląda katalog główny w poszukiwaniu pdanego pliku
            if (main_catalog.get(i).getFile_name().equals(file_name))
            {//porównuje nazwy plików
                return true;//kiedy plik istnieje zwraca prawdę
            }
        }
        return false;//zwraca fałsz jeśli plik o takiej nazwie nie istnieje
    }

    private static boolean check_space(int file_size)
    {
        int needed_blocks = file_size / 32 + 1;
        int empty_blocks = 0;
        for (int i = 0; i < block_number; i++)
        {
            if (FAT[i] == -2)
            {
                empty_blocks++;
            }
        }
        if (needed_blocks <= empty_blocks)
        {//sprawdza czy starczy miejsca
            return true;//zwraca prawdę jeśli jest tyle wolnych bloków
        }
        return false;//kiedy nie ma tylu wolnych bloków
    }

    private static int first_empty_block() throws FileException
    {
        for (int i = 0; i < block_number; i++)
        {
            if (FAT[i] == -2)
            {
                return i; //zwraca adres pierwszego wolnego bloku
            }
        }
        throw new FileException("Not enough space");
    }

    private static int check_file_index(String file_name)
    {
        for (int i = 0; i < main_catalog.size(); i++)
        {
            if (main_catalog.get(i).getFile_name().equals(file_name))
            {
                return i;
            }
        }
        System.out.println("[FilesCatalogsManager]Nie ma pliku o tej nazwie");
        return -1;//jeśli nie ma pliku o danej nazwie
    }

    public static List<String> list_of_files()
    {
        List<String> Files = new ArrayList<String>();
        for (int i = 0; i < main_catalog.size(); i++)
        {
            Files.add(main_catalog.get(i).getFile_name());
        }
        return Files;
    }

    public static void view_of_files()
    {
        System.out.println("File name\tFile size\tFile first FAT");
        for (int i = 0; i < main_catalog.size(); i++)
        {
            System.out.println(main_catalog.get(i).getFile_name() + "\t" + main_catalog.get(i).getSize() + "\t" + main_catalog.get(i).getFirst_FAT());
        }
    }

    public static void disk_view()
    {
        System.out.println("Disk view:\n");
        for (int i = 0; i < disc_size; i++)
        {
            System.out.println(hdd[i]);
        }
    }

    public static void FAT_view()
    {
        System.out.println("FAT table view:\n");
        for (int i = 0; i < block_number; i++)
        {
            System.out.println(i+":\t"+FAT[i]);
        }
        System.out.println("\n");
    }

    private static void create_file(String file_name, int file_size)throws  FileException, PermissionException
    {
        if(UsersList.curr_user==null)
            throw new FileException("[FilesCatalogsManager]User is logged out");
        if(!file_name.equals("")) {
            if (check_space(file_size) == true) {
                if (check_file_exist(file_name) == false) {
                    int actual_size = file_size; //rozmiar pliku
                    int first_block_index;
                    int block_index;
                    int block_counter = 0;

                    first_block_index = first_empty_block();
                    block_index = first_block_index;

                    CatalogFile new_file = new CatalogFile(file_name, first_block_index, file_size);
                    main_catalog.add(new_file);
                    main_catalog.get(check_file_index(file_name)).file_users.add_domain_own(UsersList.curr_user);
                    if (actual_size == 0) {
                        FAT[block_index] = -1;
                    }
                    while (actual_size > 0) {
                        FAT[block_index] = -1;
                        for (int i = 0; i < block_size; i++) {
                            if ((file_size - actual_size) != file_size && (file_size - actual_size) >= 0) {
                                hdd[block_index * block_size + i] = '0';
                                actual_size--;
                            } else {
                                break;
                            }
                        }
                        block_counter++;
                        if (actual_size > 0) {
                            FAT[block_index] = first_empty_block();
                            block_index = FAT[block_index];
                        } else {
                            FAT[block_index] = -1;
                        }
                        main_catalog.get(check_file_index(file_name)).file_users.add_domain_own(UsersList.curr_user);
                    }
                    System.out.println("[FilesCatalogsManager]Created file\n");
                } else {
                    throw new FileException("There is already file of name ");
                }
            } else {
                throw new FileException("No space for this file");
            }
        }
        else
        {
            throw new FileException("File name cannot be empty");
        }
    }
    public static void create(String name)throws FileException
    {
        try
        {
            create_file(name, 0);
        } catch (PermissionException e)
        {
            throw new FileException("File couldn't be created: " + e.toString());
        }
    }

    public static void create(String name,String content)throws FileException
    {
        try
        {
            create_file(name, 0);
            open_file(name);
            write_file_string_end(name,content);
            close_file(name);
        }catch (PermissionException e)
        {
            throw new FileException("File couldn't be created: " + e.toString());
        }
    }
    public static void write_to_new_file(String file_name, String text_to_write)throws FileException//wywoływane po utworzeniu pliku
    {
        int indicator_position = 0;
        int write_position = 0;
        int block_index = 0;
        byte[] text = text_to_write.getBytes();
        int text_size;
        int file_size = 0;


        if (check_file_exist(file_name) == true)
        {
            if(UsersList.curr_user==null)
                throw new FileException("User is logged out");
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE)|| UsersList.curr_user.is_admin())
            {
                for (int i = 0; i < main_catalog.size(); i++)
                {
                    if (main_catalog.get(i).getFile_name().equals(file_name))
                    {
                        block_index = main_catalog.get(i).getFirst_FAT();
                        file_size = main_catalog.get(i).getSize();
                        break;
                    }
                }
                text_size = text.length;
                while (indicator_position < file_size)
                {
                    hdd[block_index * block_size + write_position] = text[indicator_position];
                    if (write_position == (block_size - 1) && indicator_position != file_size - 1)
                    {
                        block_index = FAT[block_index];
                        write_position = 0;
                    } else
                    {
                        write_position++;
                    }
                    indicator_position++;
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+file_name + " does not exist.");
        }
    }

    private static void write_to_old_file(String file_name, byte one_byte_to_save) throws FileException//tylko jeden bajt na końcu pliku
    {
        int block_index = -1;
        int helper;
        int file_size = 0;

        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE)|| UsersList.curr_user.is_admin())
            {
                for (int i = 0; i < main_catalog.size(); i++)
                {
                    if (main_catalog.get(i).getFile_name().equals(file_name))
                    {
                        file_size = main_catalog.get(i).getSize();
                        main_catalog.get(i).setSize(file_size + 1);
                        block_index = main_catalog.get(i).getFirst_FAT();
                        break;
                    }
                }
                while (FAT[block_index] != -1)
                {
                    block_index = FAT[block_index];
                    file_size = file_size - block_size;
                }
                if (FAT[block_index] == -1 && file_size == 32)
                {
                    file_size = file_size - block_size;
                    if (file_size == 0)
                    {
                        helper = first_empty_block();
                        FAT[block_index] = helper;
                        FAT[helper] = -1;
                        block_index = helper;
                    }
                }

                int indicator_position = file_size;
                if (indicator_position < block_size)
                {
                    hdd[(block_index * block_size) + indicator_position] = one_byte_to_save;
                } else
                {
                    throw new FileException("Out of range");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+file_name + " does not exist.");
        }
    }

    public static void write_file_string_end(String file_name, String to_write) throws FileException
    {
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name))
        {

            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE)|| UsersList.curr_user.is_admin()) {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked()) {
                    char[] tab = new char[to_write.length()];
                    to_write.getChars(0, to_write.length(), tab, 0);
                    for (int i = 0; i < to_write.length(); i++) {
                        write_to_old_file(file_name, (byte) (tab[i]));
                    }
                    System.out.println("[FilesCatalogsManager]File was writen");
                }
                else
                {
                    throw new FileException("File must be first opened");
                }
            }
            else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+file_name + " does not exist.");
        }
    }
    public static void write_file_string_end(String file_name, String to_write,Pcb pcb) throws FileException
    {
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name))
        {

            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE)|| UsersList.curr_user.is_admin()) {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked(pcb)) {
                    char[] tab = new char[to_write.length()];
                    to_write.getChars(0, to_write.length(), tab, 0);
                    for (int i = 0; i < to_write.length(); i++) {
                        write_to_old_file(file_name, (byte) (tab[i]));
                    }
                    System.out.println("[FilesCatalogsManager]File was writen");
                }
                else
                {
                    throw new FileException("File must be first opened");
                }
            }
            else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+file_name + " does not exist.");
        }
    }

    public static char read_by_idicator(String name) throws FileException
    {
        char x = '`';
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(name))
        {
            if (main_catalog.get(check_file_index(name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ) || UsersList.curr_user.is_admin())
            {
                if(main_catalog.get(check_file_index(name)).zamek.is_locked()) {
                    {
                        try {
                            x = (char) (read_one_byte_from_file(name, get_ind(name)) & 0xFF);
                        } catch (FileException e) {
                            throw new FileException(e.toString());
                        }
                        if (get_ind(name) < get_size_of_file(name)) {
                            set_ind(name, get_ind(name) + 1);

                        } else {
                            System.out.println("[FilesCatalogsManager]End of file");
                        }
                    }
                }
                else
                {
                    throw new FileException("File must be first opened");
                }
            }
            else
                {
                    throw new FileException("User does not have permission");
                }
            return x;
        }
        else
        {
            throw new FileException("File of name "+ name + " does not exist.");
        }
    }

    public static String read_all_file(String file_name) throws FileException
    {
        int block_index = 0;
        int size_of_file = 0;
        String file = "";

        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ) || UsersList.curr_user.is_admin())
            {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked())
                {
                    for (int i = 0; i < main_catalog.size(); i++)
                    {
                        if (main_catalog.get(i).getFile_name().equals(file_name))
                        {
                            block_index = main_catalog.get(i).getFirst_FAT();
                            size_of_file = main_catalog.get(i).getSize();
                            break;
                        }
                    }
                    int i = 0, j = size_of_file;
                    while (i < block_size && j > 0)
                    {
                        file += (char) (hdd[block_index * block_size + i] & 0xFF);
                        if (i == block_size - 1 && j > 1)
                        {
                            if (FAT[block_index] > 0 && FAT[block_index] < 32)
                            {
                                block_index = FAT[block_index];
                            }
                            i = 0;
                        } else
                        {
                            i++;
                        }
                        j--;
                    }
                    System.out.println("[FilesCatalogsManager]File readed");
                    return file;
                } else
                {
                    throw new FileException("File must be first opened");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }

    public static ArrayList<Byte> read_all_file_as_array(String file_name) throws FileException
    {
        int block_index = 0;
        int size_of_file = 0;
        ArrayList<Byte> file = new ArrayList<>();

        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ) || UsersList.curr_user.is_admin())
            {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked())
                {
                    for (int i = 0; i < main_catalog.size(); i++)
                    {
                        if (main_catalog.get(i).getFile_name().equals(file_name))
                        {
                            block_index = main_catalog.get(i).getFirst_FAT();
                            size_of_file = main_catalog.get(i).getSize();
                            break;
                        }
                    }
                    int i = 0, j = size_of_file;
                    while (i < block_size && j > 0)
                    {
                        file.add(hdd[block_index * block_size + i]);
                        if (i == block_size - 1 && j > 1)
                        {
                            if (FAT[block_index] > 0 && FAT[block_index] < 32)
                            {
                                block_index = FAT[block_index];
                            }
                            i = 0;
                        } else
                        {
                            i++;
                        }
                        j--;
                    }
                    System.out.println("[FilesCatalogsManager]File readed");
                    return file;
                } else
                {
                    throw new FileException("File must be first opened");
                }
            } else
            {

                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }

    public static byte read_one_byte_from_file(String file_name, int indicator) throws FileException
    {
        int block_index = 0;
        int size_of_file = 0;

        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ) || UsersList.curr_user.is_admin())

            {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked())
                {
                    for (int i = 0; i < main_catalog.size(); i++)
                    {
                        if (main_catalog.get(i).getFile_name().equals(file_name))
                        {
                            block_index = main_catalog.get(i).getFirst_FAT();
                            size_of_file = main_catalog.get(i).getSize();
                            break;
                        }
                    }
                    int i = 0, j = 0;
                    if (main_catalog.get(check_catalog_index(file_name)).getSize() >= indicator)
                    {
                        while (i < block_size && j < size_of_file)
                        {
                            if (j == indicator)
                            {

                                return hdd[block_index * block_size + i];

                            }
                            if (i == block_size - 1)
                            {
                                if (FAT[block_index] >= 0 && FAT[block_index] < 32)
                                {
                                    block_index = FAT[block_index];
                                }
                                i = 0;
                            } else
                            {
                                i++;
                            }
                            j++;
                        }
                    } else
                    {
                        throw new FileException("Out of range");
                    }
                } else
                {
                    throw new FileException("File must be first opened");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
        return '`';
    }

    public static byte read_one_byte_from_file(String file_name, int indicator,Pcb pcb) throws FileException
    {
        int block_index = 0;
        int size_of_file = 0;

        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ) || UsersList.curr_user.is_admin())

            {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked(pcb))
                {
                    for (int i = 0; i < main_catalog.size(); i++)
                    {
                        if (main_catalog.get(i).getFile_name().equals(file_name))
                        {
                            block_index = main_catalog.get(i).getFirst_FAT();
                            size_of_file = main_catalog.get(i).getSize();
                            break;
                        }
                    }
                    int i = 0, j = 0;
                    if (main_catalog.get(check_catalog_index(file_name)).getSize() > indicator)
                    {
                        while (i < block_size && j < size_of_file)
                        {
                            if (j == indicator)
                            {

                                return hdd[block_index * block_size + i];

                            }
                            if (i == block_size - 1)
                            {
                                if (FAT[block_index] >= 0 && FAT[block_index] < 32)
                                {
                                    block_index = FAT[block_index];
                                }
                                i = 0;
                            } else
                            {
                                i++;
                            }
                            j++;
                        }
                    } else
                    {
                        throw new FileException("Out of range");
                    }
                } else
                {
                    throw new FileException("File must be first opened");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
        return '\0';
    }

    public static String read_file_from_to(String name, int from, int to) throws FileException
    {
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(name))
        {
            if (main_catalog.get(check_file_index(name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ) || UsersList.curr_user.is_admin())
            {
                if(main_catalog.get(check_file_index(name)).zamek.is_locked())
                {
                    if (get_size_of_file(name) >= (from + to)) {
                        String file = "";
                        for (int i = from; i < from + to; i++) {
                            file += (char) (read_one_byte_from_file(name, i) & 0xFF);
                        }
                        return file;
                    } else {
                        throw new FileException("Out of range");
                    }
                }
                else
                {
                    throw new FileException("File must be opened first");
                }
            }
            else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ name + " does not exist.");
        }
    }

    public static void open_file(String file_name) throws FileException
    {
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ)
                    || main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE) ||UsersList.curr_user.is_admin())
            {
                if (!main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                {
                    main_catalog.get(check_file_index(file_name)).setIndicator(0);
                    System.out.println("[FilesCatalogsManager]File opened");
                } else
                {
                    throw new FileException("This file is already opened");
                }
                main_catalog.get(check_file_index(file_name)).zamek.lock();
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }

    public static void open_file(String file_name,Pcb pcb) throws FileException
    {
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ)
                    || main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE) ||UsersList.curr_user.is_admin())
            {
                main_catalog.get(check_file_index(file_name)).zamek.lock(pcb);
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }
    public static void close_file(String file_name) throws FileException
    {
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ)
                    || main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE) ||UsersList.curr_user.is_admin())
            {
                if (main_catalog.get(check_file_index(file_name)).zamek.is_locked())
                {
                    main_catalog.get(check_file_index(file_name)).setIndicator(0);
                    main_catalog.get(check_file_index(file_name)).zamek.unlock();
                    System.out.println("[FilesCatalogsManager]File closed");
                }
                else
                {
                    if(main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                    {
                        throw new FileException("You cannot close this file");
                    }
                    throw new FileException("This file is already closed");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }

    public static void close_file(String file_name,Pcb pcb) throws FileException
    {
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ)
                    || main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE) ||UsersList.curr_user.is_admin())
            {
                if (main_catalog.get(check_file_index(file_name)).zamek.is_locked(pcb))
                {
                    main_catalog.get(check_file_index(file_name)).setIndicator(0);
                    System.out.println("[FilesCatalogsManager]File closed");
                }
                main_catalog.get(check_file_index(file_name)).zamek.unlock(pcb);
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }
    public static void rename_file(String old_name, String new_name) throws FileException
    {
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(old_name))
        {
            if (main_catalog.get(check_file_index(old_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.EXECUTE) || UsersList.curr_user.is_admin())
            {
                if(!main_catalog.get(check_file_index(old_name)).zamek.is_locked_for_all()) {
                    for (int i = 0; i < main_catalog.size(); i++) {
                        if (main_catalog.get(i).getFile_name().equals(old_name)) {
                            main_catalog.get(i).setFile_name(new_name);
                            System.out.println("[FilesCatalogsManager]File's name has been changed successfully");
                            break;
                        }
                    }
                }
                else{
                    throw new FileException("File must be closed to be renamed");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ old_name + " does not exist.");
        }
    }

    public static boolean stepWork(String[] command)
    {

        if(command[0].equals("FIL"))
        {
            if(command.length == 2)
            {

                if (command[1].equals("FAT"))
                {
                    FAT_view();
                }
                else if (command[1].equals("LIST"))
                {
                    view_of_files();
                }
                else if (command[1].equals("DISC"))
                {
                    display_disc();
                }
                else
                {
                    System.out.println("[FilesCatalogsManager]Invalid arguments");
                }
            }
            else
            {
                System.out.println("[FilesCatalogsManager]Invalid arguments lenght");
            }
            return true;
        }
        return false;
    }

    public static void delete_file(String file_name) throws FileException
    {
        int actual_index = 0;
        int block_index = 0;
        int size = 0;

        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.EXECUTE) || UsersList.curr_user.is_admin())
            {
                if(!main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                {
                    for (int i = 0; i < main_catalog.size(); i++)
                    {
                        if (main_catalog.get(i).getFile_name().equals(file_name))
                        {
                            block_index = main_catalog.get(i).getFirst_FAT();
                            size = main_catalog.get(i).getSize();
                            main_catalog.remove(i);
                            break;
                        }
                    }

                    while (size >= 0)
                    {
                        if (size > block_size)
                        {
                            actual_index = FAT[block_index];
                            FAT[block_index] = -2;
                            block_index = actual_index;
                            size = size - block_size;
                        } else
                        {
                            FAT[block_index] = -2;
                            size = size - block_size;
                        }

                    }
                    System.out.println("[FilesCatalogsManager]File was deleted successfully");
                } else
                {
                    throw new FileException("You have to close file before deleting");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }

    public static List<String> files_rights()
    {
        List<String> pom  = new ArrayList<String>();
        pom.add("Every admin get full files rights\n");
        for(int i=0;i<main_catalog.size();i++)
        {
            pom.add(main_catalog.get(i).getFile_name() + " " + main_catalog.get(i).file_users.show_access_list());
        }
        return pom;
    }

    public static void copyright(String file_name, String user_name, AccessMode mode)throws FileException
    {
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.OWNER))
            {
                if(!main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                {
                    try
                    {
                        main_catalog.get(check_file_index(file_name)).file_users.copy_right(UsersList.curr_user.get_name(),user_name,mode);
                        System.out.println("[FilesCatalogsManager]Rights copied");
                    }catch (PermissionException e)
                    {
                        throw  new FileException(" File Manager couldn't copy rights: " + e.toString());
                    }
                }
                else
                    throw new FileException("File must be first closed");
            }
            else
                throw new FileException("User does not have permission");
        }
        else
            throw new FileException("There is no file of this name");
    }

    public static void deleteright(String file_name, String user_name, AccessMode mode)throws FileException, PermissionException
    {
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.OWNER) || UsersList.curr_user.is_admin())
            {
                if(!main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                {
                    main_catalog.get(check_file_index(file_name)).file_users.delete_right(user_name,mode);
                    System.out.println("[FilesCatalogsManager]Rights deleted");
                }
                else
                    throw new FileException("File must be first closed");
            }
            else
                throw new FileException("User does not have permission");
        }
        else
            throw new FileException("There is no file of this name");
    }
    public static void moveright(String file_name, String user_name, AccessMode mode)throws FileException
    {
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.OWNER))
            {
                if(!main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                {
                    try
                    {
                        main_catalog.get(check_file_index(file_name)).file_users.move_right(UsersList.curr_user.get_name(),user_name,mode);
                        System.out.println("[FilesCatalogsManager]Rights moved");
                    } catch (PermissionException e)
                    {
                        throw new FileException("Right couldn't be moved: " + e.toString());
                    }
                }
                else
                    throw new FileException("File must be first closed");
            }
            else
                throw new FileException("User does not have permission");
        }
        else
            throw new FileException("There is no file of this name");
    }

    public static String display_file_blocks(String file_name) throws FileException
    {
        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name))
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.READ) || UsersList.curr_user.is_admin())
            {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked())
                {
                    int x=get_size_of_file(file_name)+get_size_of_file(file_name)%32;
                    String file = "";
                    for (int i = 0; i <x ; i++) {
                        file += (char) (read_one_byte_from_file(file_name, i) & 0xFF);
                    }
                    return file;
                }
                else
                {
                    throw new FileException("File must be opened first");
                }
            }
            else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }
    public static void display_disc()
    {
        char x;
        for(int i=0;i<32;i++)
        {
            System.out.print("Block : "+i+"\t");
            for (int j=0;j<32;j++)
            {
                x=(char)(hdd[i*32+j] & 0xFF);
                System.out.print(x);
            }
            System.out.println();
        }
    }

    public static void clear_file(String file_name) throws FileException
    {
        int actual_index = 0;
        int block_index = 0;
        int size = 0;

        if(UsersList.curr_user==null)
            throw new FileException("User is logged out");
        if (check_file_exist(file_name) == true)
        {
            if (main_catalog.get(check_file_index(file_name)).file_users.have_right(UsersList.curr_user.get_name(), AccessMode.WRITE) || UsersList.curr_user.is_admin())
            {
                if(main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                {
                    for (int i = 0; i < main_catalog.size(); i++)
                    {
                        if (main_catalog.get(i).getFile_name().equals(file_name))
                        {
                            block_index = main_catalog.get(i).getFirst_FAT();
                            size = main_catalog.get(i).getSize();
                            break;
                        }
                    }

                    while (size >= 0)
                    {
                        if (size > block_size)
                        {
                            actual_index = FAT[block_index];
                            FAT[block_index] = -2;
                            block_index = actual_index;
                            size = size - block_size;
                        } else
                        {
                            FAT[block_index] = -2;
                            size = size - block_size;
                        }
                    }
                    main_catalog.get(check_catalog_index(file_name)).setFirst_FAT(first_empty_block());
                    FAT[main_catalog.get(check_catalog_index(file_name)).getFirst_FAT()]=-1;
                    main_catalog.get(check_catalog_index(file_name)).setSize(0);
                    System.out.println("[FilesCatalogsManager]File was cleared successfully");
                } else
                {
                    throw new FileException("You have to open file before clearing");
                }
            } else
            {
                throw new FileException("User does not have permission");
            }
        } else
        {
            throw new FileException("File of name "+ file_name + " does not exist.");
        }
    }
    public static void shareright(String file_name, String user_name, AccessMode mode)throws FileException
    {
        if (check_file_exist(file_name) == true)
        {
            if (UsersList.curr_user.is_admin())
            {
                if(!main_catalog.get(check_file_index(file_name)).zamek.is_locked_for_all())
                {
                    try
                    {
                        main_catalog.get(check_file_index(file_name)).file_users.share_right(user_name,mode);
                        System.out.println("[FilesCatalogsManager]Rights shared");
                    }catch (PermissionException e)
                    {
                        throw  new FileException(" File Manager couldn't share rights: " + e.toString());
                    }
                }
                else
                    throw new FileException("File must be first closed");
            }
            else
                throw new FileException("User does not have permission");
        }
        else
            throw new FileException("There is no file of this name");
    }
    public static void close_all_files(Pcb pcb)
    {
        for (int i=0;i<main_catalog.size();i++)
        {
            if(main_catalog.get(i).zamek.is_locked(pcb))
            {
                try {
                    System.out.print("[FilesCatalogsManager]Zamknieto : "+main_catalog.get(i).getFile_name()+" : ");
                    close_file(main_catalog.get(i).getFile_name(), pcb);
                }catch (FileException e)
                {
                    System.out.println(e.toString());
                }
            }
        }
    }
}

