package cn.smbms.tools;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

public class MybatisUtils {

    private static SqlSessionFactory factory;
    static {
        try {
            InputStream is= Resources.getResourceAsStream("mybatis-config.xml");
            factory=new SqlSessionFactoryBuilder().build(is);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取sqlsession对象
     * @return
     */
    public static SqlSession getSession(){
        return factory.openSession(false);
    }

    /**
     * 关闭的方法
     * @param session
     */
    public static void closeSession(SqlSession session){
        if(session!=null){
            session.close();
        }
    }
}
