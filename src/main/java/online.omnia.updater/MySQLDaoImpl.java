package online.omnia.updater;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Map;

/**
 * Created by lollipop on 22.09.2017.
 */
public class MySQLDaoImpl {
    private static Configuration masterDbConfiguration;
    private static SessionFactory masterDbSessionFactory;

    private static MySQLDaoImpl instance;

    static {
        masterDbConfiguration = new Configuration()
                .addAnnotatedClass(ExchangeEntity.class)
                .addAnnotatedClass(CurrencyEntity.class)
                .configure("/hibernate.cfg.xml");
        Map<String, String> properties = FileWorkingUtils.iniFileReader();

        masterDbConfiguration.setProperty("hibernate.connection.password", properties.get("master_db_password"));
        masterDbConfiguration.setProperty("hibernate.connection.username", properties.get("master_db_username"));
        masterDbConfiguration.setProperty("hibernate.connection.url", properties.get("master_db_url"));
        while (true) {
            try {
                masterDbSessionFactory = masterDbConfiguration.buildSessionFactory();
                break;
            } catch (PersistenceException e) {
                try {
                    e.printStackTrace();
                    System.out.println("Can't connect to master db");
                    System.out.println("Waiting for 30 seconds");
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public List<CurrencyEntity> getCurrencies() {
        Session session = masterDbSessionFactory.openSession();
        List<CurrencyEntity> currencyEntities = session.createQuery("from CurrencyEntity where sync=:sync", CurrencyEntity.class)
                .setParameter("sync", 1)
                .getResultList();
        session.close();
        return currencyEntities;
    }

    public void addExchange(ExchangeEntity exchangeEntity) {
        Session session = masterDbSessionFactory.openSession();
        session.beginTransaction();
        session.save(exchangeEntity);
        session.getTransaction().commit();
        session.close();
    }

    private MySQLDaoImpl() {
    }

    public static SessionFactory getMasterDbSessionFactory() {
        return masterDbSessionFactory;
    }

    public static MySQLDaoImpl getInstance() {
        if (instance == null) instance = new MySQLDaoImpl();
        return instance;
    }
}
