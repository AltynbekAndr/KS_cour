package paket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MainSocket {
    //Главный серверСокет
    static ServerSocket serverSocket = null;
    public static void main(String[] args) throws IOException {

        System.out.println("start main classa");
        serverSocket = new ServerSocket(5555);
        //Начинаем ожидать подключение клиентов
        while(true){
            Socket socket = serverSocket.accept();
            System.out.println(socket.getInetAddress());
            //Когда есть новое подключение запускаем новый поток для работы с ним
            new SecondSocket(socket).start();
        }
    }

}
