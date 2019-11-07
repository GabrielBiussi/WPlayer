
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;
import oshi.util.Util;
import wplayer.database.DBConnection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gabriel
 */
public class TesteOshiMethods {

    public static Double previousTime;

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            collectdata();
            Util.sleep(5000);
        }
    }

    public static void collectdata() {
        SystemInfo systemInfo = new SystemInfo();
        long[] ticks = systemInfo.getHardware().getProcessor().getSystemCpuLoadTicks();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        int cpuNumber = processor.getLogicalProcessorCount();

        OSProcess[] process = systemInfo.getOperatingSystem().getProcesses(0, oshi.software.os.OperatingSystem.ProcessSort.CPU, true);
       
        for(OSProcess p : process){
        String processString = p.getName();
        Double cpu = p.calculateCpuPercent();
        Double ram=Double.valueOf(p.getResidentSetSize())/1024/1024;
        Double disc =Double.valueOf(p.getBytesWritten())/1073741824;
        Double gpu=0.0;
        

        System.out.format("K: %d, U: %d, diff: %d, CPU: %.1f%n", p.getKernelTime(), p.getUserTime(),
               0, cpu);
                
               insertData(cpu, ram, disc, null, processString, "01");
               
        }
        
    }

    public static void insertData(Double cpuA, Double ramA, Double discA, Double gpuA, String process, String machineId) {
        Connection connection = null;
        PreparedStatement statement = null;

        String insertStatement = "INSERT INTO MACHINE_PROCESS "
                               + "(MACHINE_ID, NAME_PROCESS, CPU, RAM, DISC, GPU) "
                               + "VALUES (?,?,?,?,?,?)";
        
        Double cpu = cpuA == null? 0 : cpuA;
        Double ram = ramA == null? 0 : ramA;
        Double disc = discA == null? 0 : discA;
        Double gpu = gpuA == null? 0 : gpuA;
        
        try {
            
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(insertStatement);
            
            statement.setString(1, machineId);
            statement.setString(2, process);
            statement.setDouble(3, cpu);
            statement.setDouble(4, ram);
            statement.setDouble(5, disc);
            statement.setDouble(6, gpu);
            
            statement.addBatch();
            statement.executeBatch();
            
        } catch (SQLException ex) {
            System.err.println("ERRO: "+ex);
        }
    }
}
