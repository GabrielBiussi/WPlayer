/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

/**
 *
 * @author Acer
 */
public class RequestAPI {
    public static JSONObject getJSON(String url) throws MalformedURLException, IOException{
        // Criação da variável do tipo URL  
        URL http = new URL(url);
        
        // Criação da variável InputStreamReader, abrindo conexão com a API)
        InputStreamReader request = new InputStreamReader(http.openStream());
        
        // Criação da variável do tipo BufferedReader, para fazer a leitura dos dados recebidos
        BufferedReader requestData = new BufferedReader(request);
        
        // Criação da variável String que contém os dados capturados da API
        String Data = requestData.readLine();
       
        // Retornando Objeto JSON que recebe os dados
        return new JSONObject(Data);
    }
}
