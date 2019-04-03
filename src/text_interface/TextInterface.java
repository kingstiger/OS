package text_interface;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import assembler_interpreter.Interpreter;
import files_catalogs_management.*;
import interprocess_comunication.ProcessCommunication;
import process_management.PcbException;
import process_management.ProcessManager;
import processor_management.SrtAlgorithm;
import ram_memory_management.RAM;
import synchronization_mechanisms.Mutex;
import virtual_memory_management.*;
import permissions_management.*;
import web_management.WebException;
import web_management.WebManager;
//
//Komendy będą rozpoznawane na takich samych zasadach jak w rzeczywistym shellu:
//- brak rozrozniania malych i duzych znakow np. ExiT -- poprawne
//- mozliwa dowolna liczba przerw pomiedzy komenda, a parametrem np, CD      123
//- jesli instrukcja przyjmuje dwa parametry, to inne ich ilosci nie beda egzekwowane np.
//RENAME ABC DEF		-- poprawne
//RENAME ABC			-- niepoprawne
//RENAME ABC DEF GHI 	-- niepoprawne
//

public class TextInterface implements InterfaceOutput
{
	private static Scanner sc = new Scanner(System.in);
	private static AtomicBoolean run = new AtomicBoolean(true);

	private static String[] makeInstruction(String input)
	{

		if(input.matches("\\s+")) //MB spacje!!!!!
		{
			return new String[] {""};
		}

		String[] toReturn = input.split("\\s+");
		toReturn[0] = toReturn[0].toUpperCase();
		return toReturn;	// oddzielamy parametry od komend

	}
    private static boolean checkArgsLenght(int... args) throws ShellException
    {
        if(args.length<2)
            throw new ShellException("Invalid number of arguments");
        int instructionlenght = args[0];
        for(int i=1; i<args.length; i++)
        {
        if(args[i] + 1 == instructionlenght)
            return true;
        }
		throw new ShellException("Invalid number of arguments");

    }

	private static void executeInstruction(String[] instruction) throws ShellException, FileException,
            UserException, PermissionException, PcbException
	{
		switch (CommandList.indexOf(instruction[0]))		//pierwszy blok instrukcji zawiera zawsze komende
		{
			//region PLIKI
			case 0:	//CREATEFILE
			{
				checkArgsLenght(instruction.length,1,2);

				if(instruction.length == 3)
				{
					FilesCatalogsManager.create(instruction[1], instruction[2]);
				}
				else if(instruction.length == 2)
				{
					FilesCatalogsManager.create(instruction[1]);
				}
				else
					throw new ShellException("An unrecognized argument");

			} break;
			case 1:	//DELETEFILE
			{
				checkArgsLenght(instruction.length,1);
				if (instruction[1].toLowerCase().matches("-all")
						&& !FilesCatalogsManager.check_file_exist("-all"))
				{
					for (String name : FilesCatalogsManager.list_of_files())
					{
						try
						{
							System.out.print(CommandList.padRight(name));
							FilesCatalogsManager.delete_file(name);
						}
						catch (FileException fexc)
						{
							System.out.print(fexc);
						}
					}
				}
				else
				{
					FilesCatalogsManager.delete_file(instruction[1]);
				}
			} break;
			case 2:	//READFILE
			{
				checkArgsLenght(instruction.length,1);
				if (instruction[1].toLowerCase().matches("-all")
						&& !FilesCatalogsManager.check_file_exist("-all"))
				{
					for (String name : FilesCatalogsManager.list_of_files())
					{
						try
						{
							System.out.print(CommandList.padRight(name));
							System.out.println(FilesCatalogsManager.read_all_file(name));
						}
						catch(FileException fexc)
						{
							System.out.println(fexc);
						}
					}
				}
				else
					System.out.println(FilesCatalogsManager.read_all_file(instruction[1]));
			} break;
			case 3:	//WRITEFILE
			{
				checkArgsLenght(instruction.length,2,3);
				if (instruction.length == 3 && instruction[2].toLowerCase().matches("-clear"))
				{
					FilesCatalogsManager.clear_file(instruction[1]);
				}
				else if (instruction.length == 4 && instruction[3].toLowerCase().matches("-clear"))
				{
					FilesCatalogsManager.clear_file(instruction[1]);
					FilesCatalogsManager.write_file_string_end(instruction[1],instruction[2]);
				}
				else if(instruction.length == 3)
					FilesCatalogsManager.write_file_string_end(instruction[1],instruction[2]);
				else
					throw new ShellException("An unrecognized argument");

			} break;
			case 4:	//OPENFILE
			{
				checkArgsLenght(instruction.length,1);
				if (instruction[1].toLowerCase().matches("-all") && !FilesCatalogsManager.check_file_exist("-all"))
				{
					for (String name : FilesCatalogsManager.list_of_files())
					{
						try
						{
							System.out.print(CommandList.padRight(name));
							FilesCatalogsManager.open_file(name);
						}
						catch(FileException fexc)
						{
							System.out.println(fexc);
						}
					}
				}
				else
					FilesCatalogsManager.open_file(instruction[1]);
			} break;
			case 5:	//CLOSEFILE
			{
				checkArgsLenght(instruction.length,1);
				if (instruction[1].toLowerCase().matches("-all") && !FilesCatalogsManager.check_file_exist("-all"))
				{
					for (String name : FilesCatalogsManager.list_of_files())
					{
						try
						{
							System.out.print(CommandList.padRight(name));
							FilesCatalogsManager.close_file(name);
						}
						catch(FileException fexc)
						{
							System.out.println(fexc);
						}
					}
				}
				else
					FilesCatalogsManager.close_file(instruction[1]);
			} break;
			case 6:	//COPYFILE
			{
				checkArgsLenght(instruction.length,2,3);
				if (instruction.length == 4 && instruction[3].toLowerCase().matches("-del") &&
						!FilesCatalogsManager.check_file_exist("-del"))
				{
					FilesCatalogsManager.delete_file(instruction[2]);
					FilesCatalogsManager.create(instruction[2], FilesCatalogsManager.read_all_file(instruction[1]));
				}
				else if(instruction.length == 3 && !FilesCatalogsManager.check_file_exist(instruction[2]))
				{
					FilesCatalogsManager.create(instruction[2], FilesCatalogsManager.read_all_file(instruction[1]));
				}
				else if(instruction.length == 3)
					System.out.println("A file with this name already exists");
				else
					throw new ShellException("An unrecognized argument");

			} break;
			case 7:	//FILELISTT
			{
				checkArgsLenght(instruction.length,0,1);
				if (instruction.length == 2 && instruction[1].toLowerCase().matches("-attrib"))
				{

					for (String str: FilesCatalogsManager.files_rights()) {
						System.out.print(str);
					}
				}
				else if(instruction.length == 1)
					ShellUtillites.ReadStringList(FilesCatalogsManager.list_of_files());
				else
					throw new ShellException("An unrecognized argument");
			} break;
			//endregion
			//region UZYTKOWNICY
			case 8:	//TASKLIST
			{
				checkArgsLenght(instruction.length,0);
				SrtAlgorithm.printName();
			}
			break;
			case 9:	//RENAME
			{
				checkArgsLenght(instruction.length,2);
				if (instruction.length == 3 && instruction[2].toLowerCase().matches("-user"))
				{
					UsersList.curr_user.set_name(instruction[1]);
				}
				else
					FilesCatalogsManager.rename_file(instruction[1], instruction[2]);
			} break;
			case 10://USERINFO
			{
				checkArgsLenght(instruction.length,0,1);
				if (instruction.length == 2 && instruction[1].toLowerCase().matches("-all")) {
					System.out.println(UsersList.allUserInfo());
				}
				else if(instruction.length == 1)
					UsersList.show_curr_user();
				else
					throw new ShellException("An unrecognized argument");
			} break;
			case 11://EMPTY COMMAND
			{
				//pusta komenda

			} break;
			case 12://ADMIN
			{
				checkArgsLenght(instruction.length,1);
				try
				{
					if (UsersList.users.get(UsersList.find_user(instruction[1])).is_admin())
					{
						UsersList.set_normal_mode(instruction[1]);
					} else
						UsersList.set_admin_mode(instruction[1]);
				}
				catch (IndexOutOfBoundsException ioexc)
				{
					System.out.println("Invalid name");
				}
			} break;
			case 13://ADDUSER
			{
				checkArgsLenght(instruction.length,1,2);
				if(instruction.length == 2)
				{
					UsersList.add_user(instruction[1],false);
				}
				else
				{
					if (instruction[2].toLowerCase().matches("-admin"))
						UsersList.add_user(instruction[1], true);
					else
						throw new ShellException("An unrecognized argument");
				}
			} break;
			case 14://DELETEUSER
			{
				checkArgsLenght(instruction.length, 1,2 );
				if(UsersList.curr_user.is_admin())
				{
					if (instruction.length == 2 && instruction[1].toLowerCase().matches("-all"))
					{
						for (String user : UsersList.usersList())
						{
							if (!user.equals(UsersList.curr_user.get_name()))
							{
								UsersList.delete_user(user);
							}
						}
					} else
						UsersList.delete_user(instruction[1]);
				}
				else
					System.out.print("Only admin can delete accounts");

			} break;
			case 15://LOGIN
			{
				checkArgsLenght(instruction.length, 1);
				UsersList.login(instruction[1]);

			} break;
			case 16://LOGOUT
			{
				checkArgsLenght(instruction.length,0);
				UsersList.logout();

			} break;
				//endregion
			//region PROCESY
			case 17://CREATEPROCESS
			{
				checkArgsLenght(instruction.length,2);
				ProcessManager.createProcess(instruction[1],instruction[2]);
			} break;
			case 18://KILL
			{
				checkArgsLenght(instruction.length, 1,2);
				if(instruction.length == 2)
				{
					ProcessManager.terminateProcess(instruction[1]);
				}
				else if(instruction.length == 3 && instruction[2].toLowerCase().matches("-all"))
				{
					ProcessManager.terminateProcess(instruction[1]);
				}
				else
					throw new ShellException("An unrecognized argument");

			} break;
				//endregion
			//region SIEC
			case 19://WEBON
			{
				checkArgsLenght(instruction.length,0);
				WebManager.NetOn();
			} break;
			case 20://WEBLISTEN
			{
				checkArgsLenght(instruction.length,1);
				WebManager.Listen(Integer.parseInt(instruction[1]));
			} break;
			case 21://WEBOFF
			{
				checkArgsLenght(instruction.length,0);
				WebManager.NetOff();
			} break;
			case 22://WEBSEND
			{
				try
				{
					checkArgsLenght(instruction.length, 3, 4);
					if(instruction.length == 4 && instruction[2].toLowerCase().matches("-file"))
					{

						if (FilesCatalogsManager.check_file_exist(instruction[3]))
						{
							WebManager.sendFile(instruction[1],instruction[3]);
						}
						else
							System.out.println("A file with this name does not exist");

					}

					else if (instruction.length == 4)
						WebManager.Send(instruction[1], Integer.parseInt(instruction[2]), instruction[3]);
					else
						WebManager.Send(instruction[1], Integer.parseInt(instruction[2]), instruction[3]);
				}
				catch (NumberFormatException exc)
				{
					System.out.println("Not a number");
				} catch (WebException e) {
					System.out.println(e.getMessage());
				}
			} break;
			case 23://PING
			{
				checkArgsLenght(instruction.length,1);
				try
				{
					WebManager.Ping(instruction[1]);
				} catch (WebException e) {
					System.out.println(e.getMessage());
				}
			} break;
			case 24://NETSTAT
			{
				checkArgsLenght(instruction.length,0);
				WebManager.Netstat();
			} break;
			//endregion
			//region INTERFACE
			case 25://HELP
			{
				checkArgsLenght(instruction.length,0,1);
				if(instruction.length == 1)
				{
					CommandList.read_commands();
				}
				else
					CommandList.read_description(instruction[1]);

			} break;
			case 26://EXIT
			{
				checkArgsLenght(instruction.length,0);
				UsersList.logout();
				run.set(false);
				System.exit(0);

			} break;
			//endregion
			//region NEW COMMANDS

			case 27://COPYRIGHT
            {
                checkArgsLenght(instruction.length, 3);

                if (instruction[3].toLowerCase().matches("-all"))
                {
                    FilesCatalogsManager.copyright(instruction[1], instruction[2], AccessMode.READ);
                    FilesCatalogsManager.copyright(instruction[1], instruction[2], AccessMode.WRITE);
                    FilesCatalogsManager.copyright(instruction[1], instruction[2], AccessMode.EXECUTE);

                }
                else
                {
                    instruction[3] = instruction[3].toUpperCase();
                    if(instruction[3].equals("READ") || instruction[3].equals("WRITE") || instruction[3].equals("EXECUTE"))
                    {
                        FilesCatalogsManager.copyright(instruction[1], instruction[2],
                                AccessMode.valueOf(instruction[3]));
                    }
                    else
                    {
                        throw new ShellException("Unrecognized permissson");
                    }
                }
            }
			break;
			case 28://MOVERIGHT
			{
				checkArgsLenght(instruction.length,3,4);
				if (instruction[3].toLowerCase().matches("-all"))
                {
                    FilesCatalogsManager.moveright(instruction[1], instruction[2], AccessMode.READ);
                    FilesCatalogsManager.moveright(instruction[1], instruction[2], AccessMode.WRITE);
                    FilesCatalogsManager.moveright(instruction[1], instruction[2], AccessMode.EXECUTE);

                }
                else
                {
                    instruction[3] = instruction[3].toUpperCase();
                    if(instruction[3].equals("READ") || instruction[3].equals("WRITE") || instruction[3].equals("EXECUTE"))
                    {
                        FilesCatalogsManager.moveright(instruction[1],instruction[2],
                            AccessMode.valueOf(instruction[3]));
                    }
                    else
                    {
                        throw new ShellException("Unrecognized permissson");
                    }
                }
			}
			break;
			case 29://DELETERIGHT
			{
				checkArgsLenght(instruction.length,3);
				if (instruction[3].toLowerCase().matches("-all"))
                {
                    FilesCatalogsManager.deleteright(instruction[1],instruction[2], AccessMode.READ);
                    FilesCatalogsManager.deleteright(instruction[1],instruction[2], AccessMode.WRITE);
                    FilesCatalogsManager.deleteright(instruction[1],instruction[2], AccessMode.EXECUTE);

                }
                else
                {
                    instruction[3] = instruction[3].toUpperCase();
                    if(instruction[3].equals("READ") || instruction[3].equals("WRITE") || instruction[3].equals("EXECUTE"))
                    {
                        FilesCatalogsManager.deleteright(instruction[1],instruction[2],
                                AccessMode.valueOf(instruction[3]));
                    }
                    else
                    {
                        throw new ShellException("Unrecognized permissson");
                    }
			    }
			}
			break;

			case 30://SHARERIGHT
			{
				checkArgsLenght(instruction.length,3);
				if (instruction[3].toLowerCase().matches("-all"))
				{
					FilesCatalogsManager.shareright(instruction[1],instruction[2], AccessMode.READ);
					FilesCatalogsManager.shareright(instruction[1],instruction[2], AccessMode.WRITE);
					FilesCatalogsManager.shareright(instruction[1],instruction[2], AccessMode.EXECUTE);
				}
				else
				{
					instruction[3] = instruction[3].toUpperCase();
					if(instruction[3].equals("READ") || instruction[3].equals("WRITE") ||
							instruction[3].equals("EXECUTE"))
					{
						FilesCatalogsManager.shareright(instruction[1], instruction[2],
								AccessMode.valueOf(instruction[3]));
					}
					else
					{
						throw new ShellException("Unrecognized permissson");
					}
				}
			}
			break;
			//endregion

			default:
				throw new ShellException("Unrecognized command");

		}
	}
	private static String read_from_keyboard()
	{
		return sc.nextLine();
	}

	public static void read_console_messages()
	{
		for (String m: ConsoleMessages)
		{
			System.out.print(m);
		}
	}

	public static void run() throws Exception, PcbException, WebException, FileException {
		String[] command = {""};
		boolean stepwork = false;
		while (run.get())
		{
			try
			{
				command = makeInstruction(read_from_keyboard());
				executeInstruction(command);
			}
			catch (ShellException shellexc)
			{
				if(command.length == 2)
				{
					command[1]=command[1].toUpperCase();
				}
				stepwork = Interpreter.stepWork(command)
				| FilesCatalogsManager.stepWork(command)
				| ProcessCommunication.stepWork(command)
				| PermissionManager.stepWork(command)
				| ProcessManager.stepWork(command)
				| SrtAlgorithm.stepWork(command)
				| RAM.stepWork(command)
				| Mutex.stepWork(command)
				| VirtualMemoryManager.stepWork(command)
				| WebManager.stepWork(command);

				if(command[0].equals(""))
				{
					Interpreter.step();
					stepwork = true;
				}
				else if(stepwork==false)
				{
					System.out.println(shellexc.getMessage());
					if(!shellexc.getMessage().equals("Unrecognized command"))
					System.out.println("HELP " + command[0] + " for more");
				}
				//System.out.println("Do stuff: " + line);
			}
			catch (NullPointerException | UserException | PermissionException | PcbException | FileException exc)
			{
				System.out.println(exc.getMessage());
			}
            catch (IllegalArgumentException iaexc)
            {
                System.out.println("Unrecognized permission");
            }
		}
	}

 }
