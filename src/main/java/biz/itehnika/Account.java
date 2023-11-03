package biz.itehnika;


import javax.persistence.*;

@Entity
@Table(name = "accounts")
@NamedQuery(name = "getAllAccounts", query = "SELECT c FROM Account c")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String currencyName;
    @Column(nullable = false)
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client = new Client();

    public Account() {
    }

    public Account(String currencyName, Long amount) {
        this.currencyName = currencyName;
        this.amount = amount;
    }

    public Account(String currencyName, Long amount, Client client) {
        this.currencyName = currencyName;
        this.amount = amount;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", currencyName='" + currencyName + '\'' +
                ", amount=" + amount +
                ", client=" + client +
                '}';
    }
}
