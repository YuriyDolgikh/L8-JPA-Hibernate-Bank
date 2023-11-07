package biz.itehnika;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "clients")
@NamedQuery(name = "getAllClients", query = "SELECT c FROM Client c")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "client", cascade =CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Account> accounts = new ArrayList<>();

    public Client() {
    }

    public Client(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void addAccount(Account account){
        if(!accounts.contains(account)){
            accounts.add(account);
            account.setClient(this);
        }
    }

    public void delAccount(Account account){
        if(accounts.contains(account)){
            accounts.remove(account);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
