/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import wplayer.logtxt.CriarLog;

/**
 *
 * @author Acer
 */
public class RequestAPI {
    public static JSONObject getJSON(String url) throws MalformedURLException, IOException{
        // Criação da variável do tipo URL  
        URL http = new URL(url);
        
        HttpURLConnection conn = (HttpURLConnection) http.openConnection();
        
        if(conn.getResponseCode() == 429)
            try {
                System.out.println("Pausa para comer");
                Thread.sleep(120000);
        } catch (InterruptedException ex) {
                CriarLog.WriteLog("Erro! Falha na Requisição da API"+ex);
                System.err.println("dale");
        }
        // Criação da variável InputStreamReader, abrindo conexão com a API) http.openStream()
        InputStreamReader request = new InputStreamReader(conn.getInputStream(), "UTF-8");
        
        // Criação da variável do tipo BufferedReader, para fazer a leitura dos dados recebidos
        BufferedReader requestData = new BufferedReader(request);
        
        // Criação da variável String que contém os dados capturados da API
        String Data = requestData.readLine();
       
        // Retornando Objeto JSON que recebe os dados
        return new JSONObject(Data);
    }
}
