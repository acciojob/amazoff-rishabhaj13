package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String, Order> orderMap = new HashMap<>();
    HashMap<String, DeliveryPartner> partnerMap = new HashMap<>();
    HashMap<String, HashSet<String>> partnerOrderMap = new HashMap<>();
    HashMap<String, String> orderPartnerMap = new HashMap<>();

    public void addOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
      partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){

            HashSet<String> currentOrders = new HashSet<String>();
            if(partnerOrderMap.containsKey(partnerId))
                currentOrders = partnerOrderMap.get(partnerId);
            currentOrders.add(orderId);
            partnerOrderMap.put(partnerId, currentOrders);

            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(currentOrders.size());
            orderPartnerMap.put(orderId, partnerId);
        }
    }

    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        Integer orderCount = 0;
        if(partnerMap.containsKey(partnerId)){
            orderCount = partnerMap.get(partnerId).getNumberOfOrders();
        }
        return orderCount;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        HashSet<String> orderList = new HashSet<>();
        if(partnerOrderMap.containsKey(partnerId)) orderList = partnerOrderMap.get(partnerId);
        return new ArrayList<>(orderList);
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public Integer getCountOfUnassignedOrders() {
        Integer countOfOrders = 0;
        List<String> orders =  new ArrayList<>(orderMap.keySet());
        for(String orderId: orders){
            if(!orderPartnerMap.containsKey(orderId)){
                countOfOrders += 1;
            }
        }
        return countOfOrders;
    }

    public void deleteOrderById(String orderId) {
        if(orderPartnerMap.containsKey(orderId)){
            String partnerId = orderPartnerMap.get(orderId);
            HashSet<String> orders = partnerOrderMap.get(partnerId);
            orders.remove(orderId);
            partnerOrderMap.put(partnerId, orders);

            //change order count of partner
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());
        }

        if(orderMap.containsKey(orderId)){
            orderMap.remove(orderId);
        }
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        Integer time = 0;

        if(partnerOrderMap.containsKey(partnerId)){
            HashSet<String> orders = partnerOrderMap.get(partnerId);
            for(String order: orders){
                if(orderMap.containsKey(order)){
                    Order currOrder = orderMap.get(order);
                    time = Math.max(time, currOrder.getDeliveryTime());
                }
            }
        }

        Integer hour = time/60;
        Integer minutes = time%60;

        String hourInString = String.valueOf(hour);
        String minInString = String.valueOf(minutes);
        if(hourInString.length() == 1){
            hourInString = "0" + hourInString;
        }
        if(minInString.length() == 1){
            minInString = "0" + minInString;
        }

        return  hourInString + ":" + minInString;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String timeS, String partnerId) {
        Integer hour = Integer.valueOf(timeS.substring(0, 2));
        Integer minutes = Integer.valueOf(timeS.substring(3));
        Integer time = hour*60 + minutes;

        Integer countOfOrders = 0;
        if(partnerOrderMap.containsKey(partnerId)){
            HashSet<String> orders = partnerOrderMap.get(partnerId);
            for(String order: orders){
                if(orderMap.containsKey(order)){
                    Order currOrder = orderMap.get(order);
                    if(time < currOrder.getDeliveryTime()){
                        countOfOrders += 1;
                    }
                }
            }
        }
        return countOfOrders;
    }

    public void deletePartnerById(String partnerId) {
        HashSet<String> orders = new HashSet<>();
        if(partnerOrderMap.containsKey(partnerId)){
            orders = partnerOrderMap.get(partnerId);
            for(String order: orders){
                if(orderPartnerMap.containsKey(order)){

                    orderPartnerMap.remove(order);
                }
            }
            partnerOrderMap.remove(partnerId);
        }

        if(partnerMap.containsKey(partnerId)){
            partnerMap.remove(partnerId);
        }
    }
}
