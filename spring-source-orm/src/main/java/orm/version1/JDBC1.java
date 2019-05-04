package orm.version1;

import com.spring.orm.demo.entity.Member;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madongyu on 2019/5/4.
 */
public class JDBC1 {
    public static void main(String[] args) {

    }

    private static List<Member> select(String sql) {
        List<Member> result = new ArrayList<Member>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
//1、加载驱动类
            Class.forName("com.mysql.jdbc.Driver");
//2、建立连接
            con =
                    DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/gp-vip-spring-db-demo","root","123456");
//3、创建语句集
            pstm = con.prepareStatement(sql);
//4、执行语句集
            rs = pstm.executeQuery();
            while (rs.next()){
                Member instance = new Member();
                instance.setId(rs.getLong("id"));
                instance.setName(rs.getString("name"));
                instance.setAge(rs.getInt("age"));
                instance.setAddr(rs.getString("addr"));
                result.add(instance);
            }
//5、获取结果集
        }catch (Exception e){
            e.printStackTrace();
        }
//6、关闭结果集、关闭语句集、关闭连接
        finally {
            try {
                rs.close();
                pstm.close();
                con.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }
}
