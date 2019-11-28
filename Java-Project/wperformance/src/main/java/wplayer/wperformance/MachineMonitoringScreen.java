/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.wperformance;

import com.profesorfalken.jsensors.model.components.Gpu;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import oshi.SystemInfo;
import oshi.software.os.OSFileStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import wplayer.database.DBConnection;
import wplayer.slack.Slack;

/**
 *
 * @author Gabriel
 * pqobl3vi1eursgp
 */
public class MachineMonitoringScreen extends javax.swing.JFrame {      
    private String machineKey;
    private ArrayList<Timer> timers = new ArrayList<Timer>();
    private Boolean isFirst = true;
    private Gpu gpu = GpuMonitor.getGpu();
    SystemInfo oshi = new SystemInfo();
    ScreenGui frame = new ScreenGui();
    LoginScreen loginScreen;
    
    //método pra captar ticks do processador (deve ser global caso voce faça local ele não funciona)
    long[] ticks = oshi.getHardware().getProcessor().getSystemCpuLoadTicks();
    double load ;
    double ram;
    double discofinal;
    double gpuPercent;
    

    public void setMachineKey(String machineKey) {
        this.machineKey = machineKey;
    }

    public void setLoginScreen(LoginScreen loginScreen) {
        this.loginScreen = loginScreen;
    }

    private void counter() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getdata();
            }

        };
        Timer timer = new Timer();
        long delay = 1000L;
        long period = 1000L;
        timer.scheduleAtFixedRate(task, delay, period);
        timers.add(timer);
    }
    
    private void counterdb() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                
                insertdb(load, ram, discofinal);
                System.out.println(load + "\n");
                System.out.println(ram + "\n");
                System.out.println(discofinal + "\n");
            }

        };
        Timer timer = new Timer();
        long delay = 60000L;
        long period = 60000L;
        timer.scheduleAtFixedRate(task, delay, period);
        timers.add(timer);
    }
    
    

    private void getdata() {
        //captando dados por meio da instancia OSHI
        String cpuname = oshi.getHardware().getProcessor().getName();
        float ramspace = oshi.getHardware().getMemory().getTotal();
        OSFileStore[] disc = oshi.getOperatingSystem().getFileSystem().getFileStores();
        ramspace = (((ramspace / 1024) / 1024) / 1024);
        double discspace = 0;

        for (OSFileStore disc1 : disc) {
            discspace += disc1.getTotalSpace();
        }

        discspace = (((discspace / 1024) / 1024) / 1024);
        String dsGpu = GpuMonitor.getGpuName(gpu);
        updatedata(cpuname, ramspace, discspace, dsGpu);
        calculatedata(ramspace,discspace);
    }
    
    private void calculatedata(float ramspace,double discspace){
        OSFileStore[] disc = oshi.getOperatingSystem().getFileSystem().getFileStores();
        
        //calculos do processador
        
        load = oshi.getHardware().getProcessor().getSystemCpuLoadBetweenTicks(ticks);
        ticks = oshi.getHardware().getProcessor().getSystemCpuLoadTicks();
        
        
        //calculos da memória
        float ramavaiable=  oshi.getHardware().getMemory().getAvailable();
        ramavaiable = (((ramavaiable / 1024) / 1024) / 1024);
         ram = 1f- ramavaiable/ramspace;
        
        //calculos do disco
        double usabledisc = 0;
        for (OSFileStore disc1 : disc) {
            usabledisc += disc1.getUsableSpace();
        }
        usabledisc = (((usabledisc/1024)/1024)/1024);
        discofinal = 1f- usabledisc/discspace;
        gpuPercent = GpuMonitor.getGpuPercent(gpu);
        
        updatenumbers(load, ram, discofinal, gpuPercent);
 
    }
    
    private Boolean isFirstLoop(){
        if(isFirst){
            isFirst = false;
            return true;
        }
        return false;
    }

    private void updatedata(String cpuName, float ramSpace, double discSpace, String gpuName) {
        String dsCpu = String.format(cpuName);
        String dsRam = String.format(" %.2fGB de Ram Usável", ramSpace);
        String dsDisc = String.format("%.2fGB", discSpace);
        String dsGpu = String.format(gpuName);
        jLabel6.setText(dsCpu);
        jLabel7.setText(dsRam);
        jLabel8.setText(dsDisc);
        jLabel9.setText(dsGpu);
        
        if(isFirstLoop())
            updateHardwareInfo(dsCpu, dsRam, dsDisc, dsGpu);
    }
    
    private void updatenumbers(double load,double ram,double discofinal, double gpuPercent){
        load = load *100;
        ram = ram * 100;
        discofinal = discofinal * 100;
        
        jpcpu.setValue((int) load);
        jpram.setValue((int) ram);
        jpdisc.setValue((int) discofinal);
        jpram4.setValue((int) gpuPercent);
        checkUsage((int) load, (int) ram, (int) discofinal, (int) gpuPercent);
        
    }
    
    private void insertdb(double load,double ram,double discofinal){
        insertData(load, ram, discofinal, gpuPercent, machineKey);
    }
    
    public static void insertData(Double cpuA, Double ramA, Double discA, Double gpuA, String machineKey) {
        Connection connection = null;
        PreparedStatement statement = null;

        String insertStatement = "INSERT INTO MACHINE_PROCESS "
                               + "(MACHINE_KEY, CPU, RAM, DISC, GPU) "
                               + "VALUES (?,?,?,?,?)";
        
        Double cpu = cpuA == null? 0 : cpuA;
        Double ram = ramA == null? 0 : ramA;
        Double disc = discA == null? 0 : discA;
        Double gpu = gpuA == null? 0 : gpuA;
        
        try {
            
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(insertStatement);
            
            statement.setString(1, machineKey);
            statement.setDouble(2, cpu);
            statement.setDouble(3, ram);
            statement.setDouble(4, disc);
            statement.setDouble(5, gpu);
            
            statement.addBatch();
            statement.executeBatch();
            
        } catch (SQLException ex) {
            System.err.println("ERRO: "+ex);
        }
    }
    
    private void cancelTimers(){
        timers.forEach((timer) -> {
            timer.cancel();
        });
    }
     
    public MachineMonitoringScreen() {
        initComponents();
        Seticon();
        counter();
        counterdb(); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        kGradientPanel1 = new keeptoo.KGradientPanel();
        lblLogs3 = new javax.swing.JLabel();
        lblprocessor = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        pbarra = new keeptoo.KGradientPanel();
        lblogo10 = new javax.swing.JLabel();
        btclose = new keeptoo.KButton();
        kButton1 = new keeptoo.KButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jpcpu = new rojerusan.componentes.RSProgressCircle();
        jPanel2 = new javax.swing.JPanel();
        jpram = new rojerusan.componentes.RSProgressCircle();
        jPanel4 = new javax.swing.JPanel();
        jpdisc = new rojerusan.componentes.RSProgressCircle();
        jLabel40 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jpram4 = new rojerusan.componentes.RSProgressCircle();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        kGradientPanel1.setkEndColor(new java.awt.Color(102, 102, 102));
        kGradientPanel1.setkStartColor(new java.awt.Color(0, 0, 51));

        lblLogs3.setForeground(new java.awt.Color(0, 0, 255));
        lblLogs3.setText("<html><u>Visualizar Logs</u></html>");

        lblprocessor.setFont(new java.awt.Font("Franklin Gothic Demi", 0, 11)); // NOI18N
        lblprocessor.setForeground(new java.awt.Color(255, 255, 255));
        lblprocessor.setText("Processador:");

        jLabel41.setFont(new java.awt.Font("Franklin Gothic Demi", 0, 11)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setText("Capacidade De Disco:");

        jLabel6.setFont(new java.awt.Font("Franklin Gothic Demi", 0, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("...");

        jLabel8.setFont(new java.awt.Font("Franklin Gothic Demi", 0, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("...");

        pbarra.setForeground(new java.awt.Color(0, 51, 51));
        pbarra.setkEndColor(new java.awt.Color(0, 0, 102));
        pbarra.setkStartColor(new java.awt.Color(0, 0, 51));

        lblogo10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo2.png"))); // NOI18N

        btclose.setBackground(new java.awt.Color(0, 0, 51));
        btclose.setBorder(null);
        btclose.setForeground(new java.awt.Color(0, 0, 51));
        btclose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete-button.png"))); // NOI18N
        btclose.setAlignmentY(0.0F);
        btclose.setIconTextGap(10);
        btclose.setkBackGroundColor(new java.awt.Color(0, 0, 102));
        btclose.setkEndColor(new java.awt.Color(0, 0, 102));
        btclose.setkForeGround(new java.awt.Color(0, 0, 102));
        btclose.setkHoverColor(new java.awt.Color(0, 0, 102));
        btclose.setkHoverEndColor(new java.awt.Color(0, 0, 102));
        btclose.setkHoverForeGround(new java.awt.Color(0, 0, 102));
        btclose.setkHoverStartColor(new java.awt.Color(0, 0, 102));
        btclose.setkIndicatorColor(new java.awt.Color(0, 0, 102));
        btclose.setkPressedColor(new java.awt.Color(0, 0, 102));
        btclose.setkSelectedColor(new java.awt.Color(0, 0, 102));
        btclose.setkStartColor(new java.awt.Color(0, 0, 102));
        btclose.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btclose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pbarraLayout = new javax.swing.GroupLayout(pbarra);
        pbarra.setLayout(pbarraLayout);
        pbarraLayout.setHorizontalGroup(
            pbarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pbarraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblogo10, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btclose, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pbarraLayout.setVerticalGroup(
            pbarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pbarraLayout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .addComponent(lblogo10))
            .addComponent(btclose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        kButton1.setText("Logoff");
        kButton1.setkBackGroundColor(new java.awt.Color(0, 0, 102));
        kButton1.setkEndColor(new java.awt.Color(153, 153, 153));
        kButton1.setkHoverEndColor(new java.awt.Color(0, 0, 102));
        kButton1.setkHoverForeGround(new java.awt.Color(0, 51, 102));
        kButton1.setkHoverStartColor(new java.awt.Color(0, 51, 102));
        kButton1.setkPressedColor(new java.awt.Color(0, 0, 0));
        kButton1.setkSelectedColor(new java.awt.Color(153, 153, 153));
        kButton1.setkStartColor(new java.awt.Color(0, 0, 102));
        kButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton1ActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(0, 0, 204));
        jLabel2.setFont(new java.awt.Font("Franklin Gothic Demi Cond", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Informações Do Hardware");

        jLabel9.setFont(new java.awt.Font("Franklin Gothic Demi", 0, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("...");

        jLabel43.setFont(new java.awt.Font("Franklin Gothic Demi", 0, 11)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setText("GPU:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 166, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jpcpu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jpcpu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpram, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 166, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jpdisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 168, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jpdisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jLabel40.setFont(new java.awt.Font("Franklin Gothic Demi", 0, 11)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Memória RAM:");

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("...");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpram4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpram4, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pbarra, javax.swing.GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel1Layout.createSequentialGroup()
                                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel1Layout.createSequentialGroup()
                                        .addComponent(lblprocessor)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel6))
                                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                                        .addComponent(kButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(640, 640, 640)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblLogs3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel1Layout.createSequentialGroup()
                                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel41)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel8))
                                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                                        .addGap(26, 26, 26)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(391, 391, 391)
                                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                                                .addGap(78, 78, 78)
                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel43)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel9)
                                                .addGap(142, 142, 142)))
                                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel40)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel7))))))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(336, 336, 336)
                        .addComponent(jLabel2)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                .addComponent(pbarra, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblprocessor, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel40)
                    .addComponent(jLabel7))
                .addGap(39, 39, 39)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(47, 47, 47)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel43)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(kButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLogs3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        getContentPane().add(kGradientPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 710));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btcloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcloseActionPerformed
        // TODO add your handling code here:
        this.setState(this.ICONIFIED);
    }//GEN-LAST:event_btcloseActionPerformed

    private void kButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kButton1ActionPerformed
        // TODO add your handling code here:        
        loginScreen.setVisible(true);
        this.cancelTimers();
        this.dispose();
        
    }//GEN-LAST:event_kButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MachineMonitoringScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MachineMonitoringScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MachineMonitoringScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MachineMonitoringScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
      
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                new MachineMonitoringScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private keeptoo.KButton btclose;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private rojerusan.componentes.RSProgressCircle jpcpu;
    private rojerusan.componentes.RSProgressCircle jpdisc;
    private rojerusan.componentes.RSProgressCircle jpram;
    private rojerusan.componentes.RSProgressCircle jpram4;
    private keeptoo.KButton kButton1;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JLabel lblLogs3;
    private javax.swing.JLabel lblogo10;
    private javax.swing.JLabel lblprocessor;
    private keeptoo.KGradientPanel pbarra;
    // End of variables declaration//GEN-END:variables

    private void updateHardwareInfo(String dsCpu, String dsRam, String dsDisc, String dsGpu) {
        Connection connection = null;
        PreparedStatement statement = null;
        String table = "MACHINE";
        String updateStatement = String.format("UPDATE %s "
                                             + "SET DS_CPU = ?, "
                                             + "DS_RAM = ?, "
                                             + "DS_DISC = ? , "
                                             + "DS_GPU = ? "
                                             + "WHERE MACHINE_KEY = ?", table);
        
        try{            
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(updateStatement);
            
            statement.setString(1, dsCpu);
            statement.setString(2, dsRam);
            statement.setString(3, dsDisc);
            statement.setString(4, dsGpu);
            statement.setString(5, machineKey);
            
            statement.addBatch();
            statement.executeBatch();
            
        }catch(SQLException ex){
            System.err.println("Erro: "+ex);
        }finally{
            DBConnection.closeConnection(connection, statement);
        }
        
    }
    
    private void checkUsage(Integer cpuPercent, Integer ramPercent, Integer discPercent, Integer gpuPer){
        if(cpuPercent > 80 || ramPercent > 80 || discPercent > 80 || gpuPer > 80)
            sendSlackMessage(String.format(
                    "A máquina com o ID: %s está sobrecarregada:\n" +
                    "CPU: %d\n" +
                    "RAM: %d\n" +
                    "DISCO: %d\n" +
                    "GPU: %d.",
                    machineKey, cpuPercent, ramPercent, discPercent, gpuPer));
    }
    
    private void sendSlackMessage(String message){
        try {
            new Slack("https://hooks.slack.com/services/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")
                .text(message)
                .send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Seticon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
    }
}
