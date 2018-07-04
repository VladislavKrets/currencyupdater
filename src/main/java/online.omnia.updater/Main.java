package online.omnia.updater;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.config.BeanConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ws.rs.core.Application;

/**
 * Created by lollipop on 22.09.2017.
 */

public class Main{
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //USD/USD
        //RUB/USD
        //RUB/(EUR * 10)
        //USD/EUR
        Main main = new Main();
        
        main.update();
    }
    private void update() throws ParserConfigurationException, SAXException, IOException {
        List<CurrencyEntity> currencyEntities = MySQLDaoImpl.getInstance().getCurrencies();

        //yahoo(currencyEntities);
        HttpMethodsUtils methodsUtils = new HttpMethodsUtils();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String answer = methodsUtils.getMethod("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + simpleDateFormat.format(new Date()), new HashMap<>());
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(answer.getBytes());
        Document document = documentBuilder.parse(byteArrayInputStream);
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        NodeList childNodes;
        double usdValue = 1;
        String charcode = null;
        double value = 0;
        List<ExchangeEntity> exchangeEntities = new ArrayList<>();
        ExchangeEntity exchangeEntity;
        for (int i = 0; i < nodeList.getLength(); i++) {
            childNodes = nodeList.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                if (childNodes.item(j).getNodeName().equals("CharCode")) {
                    charcode = childNodes.item(j).getTextContent();

                }
                else if (childNodes.item(j).getNodeName().equals("Value")) {
                    value = Double.parseDouble(childNodes.item(j).getTextContent().replaceAll(",", "."));
                }
            }
            if (charcode != null && charcode.equals("USD")) {
                usdValue = value;
            }
            else {
                exchangeEntity = new ExchangeEntity();
                exchangeEntity.setCurrency(charcode);
                exchangeEntity.setRate(value);
                exchangeEntities.add(exchangeEntity);
            }
        }
        exchangeEntity = new ExchangeEntity();
        for (ExchangeEntity entity : exchangeEntities) {
            if (entity.getCurrency().equals("USD")) continue;
            for (CurrencyEntity currencyEntity : currencyEntities) {
                if (currencyEntity.getCode().equals(entity.getCurrency())) {
                    value = entity.getRate();
                    entity.setRate(1 / value * usdValue);
                    entity.setCurrencyId(currencyEntity.getId());
                    entity.setTime(new Date());
                    MySQLDaoImpl.getInstance().addExchange(entity);
                }
                else if (currencyEntity.getCode().equals("RUB")) {
                    exchangeEntity.setCurrency("RUB");
                    exchangeEntity.setRate(usdValue);
                    exchangeEntity.setCurrencyId(currencyEntity.getId());
                    exchangeEntity.setTime(new Date());
                }
            }
        }
        MySQLDaoImpl.getInstance().addExchange(exchangeEntity);

        MySQLDaoImpl.getMasterDbSessionFactory().close();

    }

    private void yahoo(List<CurrencyEntity> currencyEntities) throws ParserConfigurationException, SAXException, IOException {
        StringBuilder urlBuilder = new StringBuilder("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(");
        for (CurrencyEntity currencyEntity : currencyEntities) {
            urlBuilder.append("%22USD").append(currencyEntity.getCode()).append("%22,%20");
        }
        urlBuilder = new StringBuilder(urlBuilder.substring(0, urlBuilder.length() - 4));
        urlBuilder.append(")&env=store://datatables.org/alltableswithkeys");
        System.out.println(urlBuilder.toString());
        HttpMethodsUtils methodsUtils = new HttpMethodsUtils();
        String answer = methodsUtils.getMethod(urlBuilder.toString(), new HashMap<>());
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(answer.getBytes());
        Document document = documentBuilder.parse(byteArrayInputStream);
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes().item(0).getChildNodes();
        NodeList valuteList;
        ExchangeEntity entity;
        String value = "0.0";

        for (int i = 0; i < nodeList.getLength(); i++) {
            valuteList = nodeList.item(i).getChildNodes();
            for (CurrencyEntity currencyEntity : currencyEntities) {
                for (int j = 0; j < valuteList.getLength(); j++) {
                    if (valuteList.item(j).getNodeName().equals("Rate"))
                        value = valuteList.item(j).getTextContent();

                    if (valuteList.item(j).getNodeName().equals("Name") && !valuteList.item(j).getTextContent().equals("N/A")
                            && valuteList.item(j).getTextContent().replaceAll("USD/", "")
                            .equals(currencyEntity.getCode())) {
                        entity = new ExchangeEntity();
                        entity.setCurrencyId(currencyEntity.getId());
                        entity.setRate(Double.parseDouble(value));
                        entity.setCurrency(currencyEntity.getCode());
                        entity.setTime(new Date());
                        MySQLDaoImpl.getInstance().addExchange(entity);
                    }
                }
            }
        }
    }


}
