package online.omnia.updater;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lollipop on 20.09.2017.
 */
@Entity
@Table(name = "currency")
public class CurrencyEntity {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "descriptions")
    private String descriptions;
    @Column(name = "count")
    private int count;
    @Column(name = "sync")
    private int sync;

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public int getSync() {
        return sync;
    }

    public int getCount() {
        return count;
    }

}
