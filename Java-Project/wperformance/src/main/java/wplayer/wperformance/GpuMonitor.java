/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.wperformance;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Gpu;

/**
 *
 * @author Acer
 */
public class GpuMonitor {
    public static String getGpuName(Gpu gpu){
        if(gpu != null)
        return gpu.name;
        
        return "No avaiable";
    }
    
    public static Double getGpuPercent(Gpu gpu){
        if(gpu != null)
        return gpu.sensors.loads.get(0).value;
        
        return 0.0;
    }
    
    public static Gpu getGpu(){
        return JSensors.get.components().gpus.size() >= 1? JSensors.get.components().gpus.get(0) : null;
    }
}
