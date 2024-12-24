package listener;

import factory.EntityManagerHolder;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //DataSourceHolder.getDataSource();
        //HibernateHolder.getSessionFactory();
        EntityManagerHolder.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //HibernateHolder.close();
        EntityManagerHolder.close();
    }
}
