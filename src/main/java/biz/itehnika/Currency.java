package biz.itehnika;


import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Entity
@Table(name = "currencys")
@NamedQuery(name = "getAllCurrencys", query = "SELECT c FROM Currency c")

public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Double sellRate;
    @Column(nullable = false)
    private Double buyRate;

    public Currency() {
    }

    public Currency(String name, Double sellRate, Double buyRate) {
        this.name = name;
        this.sellRate = sellRate;
        this.buyRate = buyRate;
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

    public Double getSellRate() {
        return sellRate;
    }

    public void setSellRate(Double saleRate) {
        this.sellRate = saleRate;
    }

    public Double getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(Double buyRate) {
        this.buyRate = buyRate;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sellRate=" + sellRate +
                ", buyRate=" + buyRate +
                '}';
    }

    public void getActualRates(String currName){
        String fromPrivat = "";
        try{
            URL url = new URL("https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            try(InputStream inputStream = httpURLConnection.getInputStream()){
                fromPrivat = new String(inputStream.readAllBytes());
                String[] allCurrencyInfo = fromPrivat.substring(2,fromPrivat.length()-2).split("\\},\\{");
                String currencyForParsing = currName.equals("EUR")?allCurrencyInfo[0]:allCurrencyInfo[1];
                String[] thisCurrency = currencyForParsing.replace(':', ',').split(",");
                buyRate = Double.parseDouble(thisCurrency[5].replace("\"", ""));
                sellRate = Double.parseDouble(thisCurrency[7].replace("\"", ""));
                name = currName;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
