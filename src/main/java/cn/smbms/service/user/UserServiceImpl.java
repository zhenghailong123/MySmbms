package cn.smbms.service.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.user.UserDao;
import cn.smbms.pojo.User;
import cn.smbms.tools.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * service层捕获异常，进行事务处理
 * 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 * @author Administrator
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService{
	@Autowired
	private UserDao userDao;
	/*public UserServiceImpl(){
		userDao = new UserDaoImpl();
	}*/
	//private SqlSession session;
	@Override
	public boolean add(User user) {
		boolean flag = false;
		try {
			//session = MybatisUtils.getSession();
			//int updateRows = session.getMapper(UserDao.class).add(user);
			if(userDao.add(user) > 0){
				flag = true;
				System.out.println("add success!");
			}else{
				System.out.println("add failed!");
			}
			//session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			//session.rollback();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return flag;
	}
	@Override
	public User login(String userCode, String userPassword) {
		User user = null;
		try {
			//session=MybatisUtils.getSession();
			//user = session.getMapper(UserDao.class).getLoginUser(userCode);
			user = userDao.getLoginUser(userCode);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		//匹配密码
		if(null != user){
			if(!user.getUserPassword().equals(userPassword))
				user = null;
		}
		return user;
	}
	@Override
	public List<User> getUserList(String queryUserName,int queryUserRole,int currentPageNo, int pageSize) {
		List<User> userList = null;
		currentPageNo = (currentPageNo-1)*pageSize;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		System.out.println("currentPageNo ---- > " + currentPageNo);
		System.out.println("pageSize ---- > " + pageSize);
		try {
			//session = MybatisUtils.getSession();
			//userList = session.getMapper(UserDao.class).getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
			userList = userDao.getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return userList;
	}
	@Override
	public User selectUserCodeExist(String userCode) {
		User user = null;
		try {
			//session=MybatisUtils.getSession();
			//user=session.getMapper(UserDao.class).getLoginUser(userCode);
			user = userDao.getLoginUser(userCode);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return user;
	}
	@Override
	public boolean deleteUserById(Integer delId) {
		boolean flag = false;
		try {
			//session=MybatisUtils.getSession();
			//int delRows = session.getMapper(UserDao.class).deleteUserById(delId);
			if(userDao.deleteUserById(delId) > 0){
				flag = true;
			}
			//session.commit();
		} catch (Exception e) {
			//session.rollback();;
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return flag;
	}
	@Override
	public User getUserById(String id) {
		User user = null;
		try{
			//session = MybatisUtils.getSession();
			//user = session.getMapper(UserDao.class).getUserById(id);
			user = userDao.getUserById(id);
		}catch (Exception e) {
			e.printStackTrace();
			user = null;
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return user;
	}
	@Override
	public boolean modify(User user) {
		boolean flag = false;
		try {
			//session = MybatisUtils.getSession();
			//int updateRows = session.getMapper(UserDao.class).modify(user);
			if(userDao.modify(user) > 0) {
				flag = true;
			}
			//session.commit();
		} catch (Exception e) {
			//session.rollback();
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return flag;
	}
	@Override
	public boolean updatePwd(int id, String pwd) {
		boolean flag = false;
		try{
			//session = MybatisUtils.getSession();
			//int updateRows = session.getMapper(UserDao.class).updatePwd(id, pwd);
			if(userDao.updatePwd(id, pwd) > 0) {
				flag = true;
			}
			//session.commit();
		}catch (Exception e) {
			e.printStackTrace();
			//session.rollback();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return flag;
	}
	@Override
	public int getUserCount(String queryUserName, int queryUserRole) {
		int count = 0;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		try {
			//session = MybatisUtils.getSession();
			//count = session.getMapper(UserDao.class).getUserCount(queryUserName,queryUserRole);
			count = userDao.getUserCount(queryUserName, queryUserRole);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return count;
	}
	
}
