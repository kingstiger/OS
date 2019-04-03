package files_catalogs_management;
import java.lang.String;

import permissions_management.FilePermission;
import synchronization_mechanisms.*;
public class CatalogFile
{
    private String file_name;
    private int first_FAT;
    private int size;
    public FilePermission file_users;
    private int indicator;
    public Mutex zamek;

    public CatalogFile(String file_name, int first_FAT, int size) {
        this.file_name = file_name;
        this.first_FAT = first_FAT;
        this.size = size;
        this.indicator=0;
        this.zamek=new Mutex();
        this.file_users=new FilePermission();
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public int getIndicator() {
        return indicator;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getFirst_FAT() {
        return first_FAT;
    }

    public int getSize() {
        return size;
    }

    public boolean is_open()
    {
        if(this.zamek.is_locked())
            return true;
        else
            return false;
    }

    public void setFirst_FAT(int first_FAT) {
        this.first_FAT = first_FAT;
    }
}
