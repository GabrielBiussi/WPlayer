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
        return gpu.name;
    }
    
    public static Double getGpuPercent(Gpu gpu){
        return gpu.sensors.loads.get(0).value;
    }
    
    public static Gpu getGpu(){
        return JSensors.get.components().gpus.get(0);
    }
}
