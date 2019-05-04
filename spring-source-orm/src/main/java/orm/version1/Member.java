package orm.version1;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by madongyu on 2019/5/4.
 */
@Entity
@Table(name="t_member")
@Data
public class Member {
    @Id
    private Long id;
    private String name;
    private String addr;
    private Integer age;
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", age=" + age +
                '}';
    }
}
