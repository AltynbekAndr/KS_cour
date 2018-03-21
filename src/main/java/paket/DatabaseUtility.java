package paket;

import java.beans.PropertyVetoException;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class DatabaseUtility {
    static String connectionUrl = "jdbc:sqlserver://217.29.21.60:6889;databaseName=KOVER-SAMOLET;user=sa;password=Afina954120";
    public static ComboPooledDataSource getDataSource() throws PropertyVetoException{

        ComboPooledDataSource c = new ComboPooledDataSource();
        c.setJdbcUrl(connectionUrl);
        c.setInitialPoolSize(3);
        c.setMinPoolSize(3);
        c.setAcquireIncrement(3);
        c.setMaxPoolSize(100);
        c.setMaxStatements(500);

        return c;
    }

}
