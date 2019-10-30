package cn.smbms.service.role;

import java.sql.Connection;
import java.util.List;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.role.RoleDao;
/*import cn.smbms.dao.role.RoleDaoImpl;*/
import cn.smbms.pojo.Role;
import cn.smbms.tools.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("roleService")
public class RoleServiceImpl implements RoleService{
	@Autowired
	private RoleDao roleDao;
	//private SqlSession session;
	/*public RoleServiceImpl(){
		roleDao = new RoleDaoImpl();
	}*/
	
	@Override
	public List<Role> getRoleList() {
		List<Role> roleList = null;
		try {
			//session = MybatisUtils.getSession();
			//roleList = session.getMapper(RoleDao.class).getRoleList();
			roleList = roleDao.getRoleList();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return roleList;
	}
	
}
