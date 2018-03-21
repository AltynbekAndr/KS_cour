package paket;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.TimeUnit;

class SecondSocket extends Thread{
    Socket l_socket = null;
    Connection connection = null;
    CallableStatement cstmt = null;
    String userId = null;
    String line = null;
    String line2 = null;
    private static final Logger log = Logger.getLogger(SecondSocket.class);
    Document document = null;
    Elements xmlTag = null;
    Element e = null;
    public SecondSocket(Socket socket){
        l_socket = socket;
    }
    @Override
    public void run() {

            try(
                PrintWriter out = new PrintWriter(l_socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(l_socket.getInputStream(), Charset.forName("cp1251")))
                ){

            line = in.readLine();
            try{
                ComboPooledDataSource dataSource = DatabaseUtility.getDataSource();
                connection = dataSource.getConnection();
            }
            catch (Exception e){
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            try {
                cstmt = connection.prepareCall("{call xml_parser(?,?)}");
                cstmt.setString(1, line);
                cstmt.registerOutParameter(2, Types.VARCHAR);
                cstmt.execute();
                document = Jsoup.parse(cstmt.getString(2));
                xmlTag = document.getElementsByTag("codeid");
                try{
                    e = xmlTag.get(0);
                    userId = e.text();
                }catch (Exception e){}

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(userId!=null){
                    line2 = line;
                try {
                    cstmt = connection.prepareCall("{call xml_parser_new(?,?)}");
                    cstmt.setInt(1, Integer.valueOf(userId));
                    cstmt.registerOutParameter(2, Types.INTEGER);
                    out.println("echo");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    while ((line = in.readLine()) != null) {
                    if(line==null||line.equals("")||line.length()==0){
                        connection.close();
                        connection = null;
                        cstmt.close();
                        cstmt = null;
                        l_socket.close();
                        line2 = null;
                        line = null;
                        userId = null;
                        e = null;
                        xmlTag = null;
                        document = null;
                        log.info("все обьекты уничтожены.сокет закрыт,поток мертв");
                        break;
                    }
                    System.out.println(line);
                            cstmt.execute();
                            if (cstmt.getString(2).equals("1")) {
                                out.println(cstmt.getString(2));
                                out.flush();
                            }else {
                                out.println("0");
                                out.flush();
                            }
                    }
                }catch(Exception e){
                    try {
                        connection.close();
                        connection = null;
                        cstmt.close();
                        cstmt = null;
                        l_socket.close();
                        line2 = null;
                        line = null;
                        userId = null;
                        e = null;
                        xmlTag = null;
                        document = null;
                        log.info("все обьекты уничтожены.сокет закрыт,поток мертв");
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                }
            }


            System.out.println("Cahello JAKE, doesn't work!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}