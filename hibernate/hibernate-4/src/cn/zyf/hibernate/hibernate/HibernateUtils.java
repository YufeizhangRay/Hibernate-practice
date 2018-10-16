package cn.zyf.hibernate.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * 此工具类的核心作用就是获取 Session 对象
 */
public class HibernateUtils {	
	//使用单例模式
	private static HibernateUtils hibernateUtils = null;
	private HibernateUtils() {}
	public static HibernateUtils getInstance() {
		if(hibernateUtils==null) {
			hibernateUtils = new HibernateUtils();
		}
		return hibernateUtils;
	}
	
	public SessionFactory getSessionFactory() {
		Configuration configuration = new Configuration().configure();
			ServiceRegistry serviceRegistry  = configuration
											.getStandardServiceRegistryBuilder()
											.build();
			return  new MetadataSources(serviceRegistry).buildMetadata()
														.buildSessionFactory();
	}
	
	public Session getSession() {
		return getSessionFactory().getCurrentSession();//获取与当前线程绑定的 Session
	}
}
