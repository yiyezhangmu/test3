package com.coolcollege.intelligent.service.passengerflow;

/**
 * @author byd
 * @date 2025-09-09 15:01
 */
public interface JieFengApiService {

    String addStoreNode(String storeName, String storeAddress, String storeLongitude, String storeLatitude);

    String editStore(String id, String storeName, String storeAddress,
                     String longitude, String latitude);

    String deleteStore(String nodeId);

    String queryPassengerFlow(String deviceSn, String storeId,
                              String startTime, String endTime);

    void addAllStoreNode(String eid);

    void getAllPassengerFlow(String eid, String beginTime, String endTime);
}
