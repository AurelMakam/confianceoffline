/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confianceoffline;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Aurel
 */
public class Commons {

    private final String post_url = "http://confianceonline.com/centre_certification/upload";

    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(5, 0);

        return dateFormat.format(cal.getTime());
    }

    public static String getTodayDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        cal.add(5, 0);

        return dateFormat.format(cal.getTime());
    }

    public static boolean upload_file_ftp(String local_file, String distant_file, String[] ftp_params)
            throws JSchException, SftpException {
        JSch jsch = new JSch();
        com.jcraft.jsch.Session session = null;
        String ftp_host = ftp_params[0];
        String ftp_user = ftp_params[1];
        String ftp_pass = ftp_params[2];
        int ftp_port = Integer.valueOf(ftp_params[3]);
        try {
            System.out.println("depot du fichier :" + distant_file + " sur le SFTP");
            session = jsch.getSession(ftp_user, ftp_host, ftp_port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(ftp_pass);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.put(local_file, distant_file);
            System.out.println("fichier " + local_file + " depose sur le serveur FTP ");
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return true;
    }

    public int upload_file_http(String local_file) throws MalformedURLException, IOException {
        String charset = "UTF-8";
        String param = "value";
        File textFile = new File(local_file);
        File binaryFile = new File("/path/to/file.bin");
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        URLConnection connection = new URL(post_url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);) {
            // Send normal param.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(param).append(CRLF).flush();

            // Send text file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"textFile\"; filename=\"" + textFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
            writer.append(CRLF).flush();
            Files.copy(textFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
        }

// Request is lazily fired whenever you need to obtain information about response.
        int responseCode = ((HttpURLConnection) connection).getResponseCode();
        System.out.println(responseCode); // Should be 200
        return responseCode;
    }

    public void download_file_http(String remote_file, String local_file) throws MalformedURLException, IOException {
        URL url = new URL(remote_file);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(local_file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    public static ArrayList<String> listHttpFiles(String remotedir) throws IOException {
        Document doc = Jsoup.connect(remotedir).get();
        ArrayList<String> ars = new ArrayList<>();
        doc.select("td.right td a").stream().map((file) -> {
            System.out.println(file.attr("href"));
            return file;
        }).forEach((file) -> {
            ars.add(file.attr("href"));
        });
        return ars;
    }

    public static String fileToString(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder builder = new StringBuilder();
        String line;

        // For every line in the file, append it to the string builder
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        return builder.toString();
    }

    public static void writeTextFile(String fileName, String input, String encoding, String writeMethod) throws IOException {
        // Method overwrite
        if ("o".equals(writeMethod)) {

            try (Writer out = new OutputStreamWriter(new FileOutputStream(fileName), encoding)) {
                out.write(input);
            }
        }
    }

    public static String CalculateCRC(String chaine) {
        byte bytes[] = chaine.getBytes();
        Checksum checksum = new CRC32();
        // update the current checksum with the specified array of bytes
        checksum.update(bytes, 0, bytes.length);

        // get the current checksum value
        long checksumValue = checksum.getValue();
        return "" + checksumValue;
    }

    public static String[][] mesDocuments(String Chemin) {
        String[][] liste_fichier = null;
        File dossier = new File(Chemin);
        File[] liste_dossier = dossier.listFiles();
        ArrayList<String[]> ars = new ArrayList<String[]>();
        int i = 1;
        for (File f : liste_dossier) {
            if (f.isDirectory()) {
                String fname = f.getName();
                File[] liste_files = f.listFiles();
                for (File unf : liste_files) {
                    if (!unf.isDirectory()) {
                        String extension = unf.getName().substring(unf.getName().length() - 3);
                        ars.add(new String[]{"" + i, fname, unf.getName(),extension});
                        i++;
                    }
                }
            }
        }
        liste_fichier = new String[ars.size()][4];
        for (int j = 0; j < ars.size(); j++) {
            liste_fichier[j][0] = ars.get(j)[0];
            liste_fichier[j][1] = ars.get(j)[1];
            liste_fichier[j][2] = ars.get(j)[2];
            liste_fichier[j][3] = ars.get(j)[3];;
        }
        return liste_fichier;
    }
}
