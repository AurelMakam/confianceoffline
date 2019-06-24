/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confianceoffline;

import com.jcraft.jsch.jce.MD5;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Aurel
 */
public class Connection {

    public static String connectionFile;
    public static String connectionFilePath = "C:\\Confiance";
    private String ConnectionUrl;
    private String email;
    private String password;
    private String module;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
    
    

    public String getConnectionUrl() {
        return ConnectionUrl;
    }

    public void setConnectionUrl(String ConnectionUrl) {
        this.ConnectionUrl = ConnectionUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection(String email, String password, String module, String connectionURL) {
        this.email = email;
        this.password = password;
        this.ConnectionUrl = connectionURL;
        this.module = module;
        connectionFile = connectionFilePath+File.separator+module+File.separator+"connection.txt";
    }

//    public boolean Connect_file(){
//        File tempfile = new File()
//    }
    public boolean Connect() throws MalformedURLException, UnsupportedEncodingException, IOException {
        File connectionfile = new File(connectionFile);
        if (!connectionfile.exists()) {
            (new File(connectionFilePath+File.separator+module)).mkdirs();
            // essayer de se connecter sur confianceonline
            System.out.println("here");
            URL url = new URL(ConnectionUrl);
//            URL url = new URL(ConnectionUrl+"?email="+email+"&pwd="+password+"&desktop=true");
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("email", email);
            params.put("pwd", password);
            params.put("desktop", "true");
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            System.out.println("post data = " + postData.toString()+"  url = "+ConnectionUrl);
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setRequestProperty( "charset", "utf-8");
            conn.setDoOutput(true);
            conn.setUseCaches( false );
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String result = "";
            for (int c; (c = in.read()) >= 0;){
                System.out.print((char)c);
                result = result+""+(char)c ;
            }
//            String result = "" + in.read();

            System.out.println("result = "+result);
//            System.out.println("result = " + (in.read()));
            switch (result) {
                
                case "connected":
                    (new File(connectionFilePath+File.separator+module)).mkdirs();
                    connectionfile.createNewFile();
                    Config cf = new Config(connectionFile);
                    cf.add("email", email);
                    cf.add("pass", password);
                    return true;
                default:
                    JOptionPane.showMessageDialog(null, result);
                    return false;
            }
        } else {
            Config cf = new Config(connectionFile);
            String emailfile = cf.get("email", null);
            String passfile = cf.get("pass", null);
            return (emailfile == null ? email == null : emailfile.equals(email)) && (passfile == null ? password == null : passfile.equals(password));
        }

    }

    public static boolean checkInternet() {
        try {
            URL url = new URL("https://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
