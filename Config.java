/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package confianceoffline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author AUREL
 */
public class Config {
   String chemin;
    public Config(String file_path){
        chemin = file_path;
        if(new File(chemin).exists()){
//            System.out.println("nom du fichier = "+chemin);
        }
        else{
            
            System.out.println("le fichier "+chemin+" est introuvable");
        }
    }
   
	
	public  void checkConfigFile() {
		File f  = new File(chemin);
		if (!(f.exists())) {
			System.out.println("Question file :"+chemin+ " not found!");
			System.exit(1);
		}
	}
	
	public  String get(String attribute, String default_val) {
		try {
                    
                    //System.out.println("chemin = "+chemin);
			Properties prop = new Properties();
		    InputStream is = new FileInputStream(chemin);
	
		    prop.load(is);
                  
			return prop.getProperty(attribute,default_val);
		} 
		catch(IOException e) {
			return null;
		}
        }
    public  void set(String attribute,String new_val){
        try{
            Properties prop = new Properties();
            InputStream is = new FileInputStream(chemin);
           
            prop.load(is);
            prop.setProperty(attribute, new_val);
            prop.store(new FileOutputStream(chemin), null);
        
        }
        
        catch(IOException e){
            
        }
    }   
    public  void add(String attribute,String new_val){
        try{
           Properties prop = new Properties();
           InputStream is = new FileInputStream(chemin);
//           OutputStream os = new FileOutputStream(chemin);
           prop.load(is);
           prop.setProperty(attribute, new_val);
           prop.store(new FileOutputStream(chemin), null);
        }
        
        catch(IOException e){
            
        }
    }   
}
