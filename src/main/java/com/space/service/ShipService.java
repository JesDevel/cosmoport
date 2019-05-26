package com.space.service;

import com.space.model.Ship;

import java.util.List;
import java.util.Map;

public interface ShipService {
    List<Ship> getByAllCriteria(Map<String, String> params);
    Integer getByAllCriteriaCount(Map<String, String> params);
    Ship createShip(Ship newShip);
    Ship getById(Long id);
    Ship updateShip(Ship updateShip, Map<String, String> params);
    void deleteShip(Ship deletedShip);
}
