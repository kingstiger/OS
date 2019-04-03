package permissions_management;

//READ - odczyt
//WRITE - zapis, edycja
//EXECUTE - usuwanie?
//OWNER - wlaciciel (on utworzyl plik)
//*_COPY - mozna przekazywac uprawnienia pomiedzy uzytkownikami (tylko administarcja i wlasciciel, ale juz bez '_COPY')
// czyli READ_COPY -> READ 
//PRINT - drukowanie (czysto dla kodu, u nas nie uzywane)
//SWITCH, CONTROL - nie pytajcie :(

public enum AccessMode
{
	
NO_RIGHTS(0), READ(1), READ_COPY(6), WRITE(10), WRITE_COPY(15), EXECUTE(20), EXECUTE_COPY(25), PRINT(30),
PRINT_COPY(35), SWITCH(50), CONTROL(55), OWNER(100);
	
	int value;
	AccessMode(int n)
	{
		this.value = n;
	}
}
