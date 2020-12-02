package com.notification_service.backend.Services.DBServices;

import com.notification_service.backend.Services.InitService;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

@Service public class MySQLDBServiceImpl implements InitService, MySQLDBService {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/notification";
    private static final String USER = "root";
    private static final String PASS = "";

    @Override public void initService() {
        Connection conn = null;
        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Creating Script Runner statement...");
            ScriptRunner sr = new ScriptRunner(conn);
            InputStream inputStreamQuartzFile = MySQLDBServiceImpl.class.getClassLoader().getResourceAsStream("quartz.sql");
            if(inputStreamQuartzFile==null) {
                System.out.println("CANNOT FIND FILE");
                throw new Exception();
            }
            InputStreamReader inputStreamReaderQuartzFile = new InputStreamReader(inputStreamQuartzFile);
            sr.runScript(inputStreamReaderQuartzFile);
            conn.close();
        } catch(Exception se){
            se.printStackTrace();
        } finally{
            try{
                if(conn!=null)
                    conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Tables Created !!!");
    }
}
