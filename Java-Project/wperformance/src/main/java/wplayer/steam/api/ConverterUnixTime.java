package wplayer.steam.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author petter
 */
public class ConverterUnixTime {
    public static String converterUnixToString(Integer unix){
        // Validando Nulo
        if(unix == null)
            return null;
        
        // Convertendo segundos para milisegundos
        long unixSeconds = unix;
        
        // Criando objeto date com base nos milisegundos
        Date date = new Date(unixSeconds*1000L); 
        
        // Aqui criamos o formato (máscara) da data
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
       
        // Inserindo Fuso horário para a data
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        
        // Criamos uma String, utilizando a data que foi criada no fuso horário escolhido
        String formattedDate = sdf.format(date);
        
        // Devolvemos a String Data
        return formattedDate;
    }
}
