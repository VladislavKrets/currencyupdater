package online.omnia.updater;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lollipop on 20.09.2017.
 */
@Entity
@Table(name = "exchange")
public class ExchangeEntity {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "time")
    private Date time;
    @Column(name = "currency_id")
    private int currencyId;
    @Column(name = "rate")
    private double rate;
    @Transient
    private String currency;

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public String getCurrency() {
        return currency;
    }

    public double getRate() {
        return rate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeEntity{" +
                "id=" + id +
                ", time=" + time +
                ", currencyId=" + currencyId +
                ", rate=" + rate +
                ", currency='" + currency + '\'' +
                '}';
    }
}
