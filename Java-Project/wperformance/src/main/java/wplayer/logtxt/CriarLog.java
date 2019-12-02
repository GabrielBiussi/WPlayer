/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.logtxt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Aluno
 */
public class CriarLog {
    
    
   private static void VerifyExistence(File arquivo) {
       
    File directory = new File ("C:/temp");
    boolean drExists = directory.exists();
    boolean exists = arquivo.exists();
 
    
    try{
        if(!drExists) {
            directory.mkdir();
            
        }
           
    if (!exists) {
        
        arquivo.createNewFile();

    }else{
        arquivo.createNewFile();
    }
    
    }catch (IOException erro){
        System.out.println("Erro 01!");
    }
    
    
    
   }

public static void WriteLog(String Message) {
    
    try{
        File arquivo = new File("C://temp/file1.txt");
       
        
        Format formater = new SimpleDateFormat("dd/MM/yyyy HH/mm/ss");
        Date data = new Date();
        String logDados = formater.format(data);
        
        VerifyExistence(arquivo);


        FileWriter filewriter = new FileWriter(arquivo, true);
        BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
        
        bufferedwriter.write("" + logDados + " | " + Message);
        bufferedwriter.newLine();
        
        bufferedwriter.close();
        filewriter.close();
     
    }catch (IOException error){
        System.out.println("Erro 02!");
        
    }
}


}

