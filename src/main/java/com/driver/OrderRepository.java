package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

     HashMap<String, Order> orderMap = new HashMap<>();
     HashMap<String, DeliveryPartner> partnerMap = new HashMap<>();
     HashMap<String, DeliveryPartner> orderPartnerMap = new HashMap<>();
     HashMap<String, List<Order>> partnerOrderMap = new HashMap<>();


    public void addOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, deliveryPartner);
        partnerOrderMap.put(partnerId, new ArrayList<>());
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        Order order = orderMap.get(orderId);
        partnerOrderMap.get(partnerId).add(order);
        DeliveryPartner deliveryPartner = partnerMap.get(partnerId);
        orderPartnerMap.put(orderId, deliveryPartner);
        deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()+1);
    }

    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerMap.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        return partnerOrderMap.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<Order> orders = partnerOrderMap.get(partnerId);
        List<String> list = new ArrayList<>();
        for(Order order:orders){
            list.add(order.getId());
        }
        return list;
    }

    public List<String> getAllOrders() {
        List<String> list = new ArrayList<>();
        for(String s : orderMap.keySet()){
            list.add(s);
        }
        return list;
    }

    public int getCountOfUnassignedOrders() {
        return orderMap.size()-orderPartnerMap.size();
    }

    public void deleteOrderById(String orderId) {
        DeliveryPartner deliveryPartner = orderPartnerMap.get(orderId);
        deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()-1);
        String partnerId = deliveryPartner.getId();
        Order order = orderMap.get(orderId);
        List<Order> orders = partnerOrderMap.get(partnerId);
        orders.remove(order);
        partnerOrderMap.put(partnerId, orders);
        orderPartnerMap.remove(orderId);
        orderMap.remove(orderId);
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        List<Order> list = partnerOrderMap.get(partnerId);
        int lastTime=0;
        for(Order order:list){
            if(order.getDeliveryTime()>lastTime){
                lastTime = order.getDeliveryTime();
            }
        }
        String lastDeliveryTime = "";
        if(lastTime/60<10){lastDeliveryTime+="0";}
        lastDeliveryTime+=String.valueOf(lastTime/60);
        lastDeliveryTime+=":";
        if(lastTime%60<10){lastDeliveryTime+="0";}
        lastDeliveryTime+=String.valueOf(lastTime%60);
        return lastDeliveryTime;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        List<Order> list = partnerOrderMap.get(partnerId);
        int undeliveredOrder = 0;
        int hh=Integer.parseInt(time.substring(0,2));
        int mm=Integer.parseInt(time.substring(3,time.length()));
        int Time = hh*60 + mm;
        for(Order order:list){
            if(order.getDeliveryTime()>Time){
                undeliveredOrder++;
            }
        }
        return undeliveredOrder;
    }

    public void deletePartnerById(String partnerId) {
        partnerMap.remove(partnerId);
        List<Order> orders = partnerOrderMap.get(partnerId);
        List<String> list = new ArrayList<>();
        for(String s:orderPartnerMap.keySet()){
            if(partnerId.equals(orderPartnerMap.get(s).getId())){
                list.add(s);
            }
        }
        for(String s:list){
            orderPartnerMap.remove(s);
        }
        partnerOrderMap.remove(partnerId);
    }
}