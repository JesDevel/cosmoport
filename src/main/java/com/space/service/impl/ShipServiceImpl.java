package com.space.service.impl;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.exception.BadRequestException;
import com.space.exception.ElementNotFoundException;
import com.space.model.*;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private EntityManagerFactory entityManager;

    @Override
    public void deleteShip(Ship deletedShip) {
        shipRepository.delete(deletedShip);
    }

    @Override
    public Ship updateShip(Ship updateShip, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue().equals("")) throw new BadRequestException();
            switch (entry.getKey()) {
                case "name":
                    updateShip.setName(entry.getValue());
                    break;
                case "planet":
                    updateShip.setPlanet(entry.getValue());
                    break;
                case "shipType":
                    updateShip.setShipType(ShipType.valueOf(entry.getValue()));
                    break;
                case "prodDate":
                    Date date = Calendar.getInstance().getTime();
                    date.setTime(Long.parseLong(entry.getValue()));
                    updateShip.setProdDate(date);
                    break;
                case "isUsed":
                    updateShip.setUsed(Boolean.parseBoolean(entry.getValue()));
                    break;
                case "speed":
                    Double speed = Double.parseDouble(entry.getValue());
                    updateShip.setSpeed(speed);
                    break;
                case "crewSize":
                    updateShip.setCrewSize(Integer.parseInt(entry.getValue()));
                    break;
            }
        }
        updateShip.setRating(Model.getRating(updateShip.getSpeed(), updateShip.getUsed(), updateShip.getProdDate()));
        if (!checkShip(updateShip)) throw new BadRequestException();
        shipRepository.saveAndFlush(updateShip);
        return updateShip;
    }

    @Override
    public Ship getById(Long id) {
        try {
            return shipRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ElementNotFoundException();
        }
    }

    private Boolean checkShip(Ship ship) {
        /*--------------------------------------------*/
        Calendar minDate = Calendar.getInstance();
        minDate.set(2800, 0, 1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(3019, 11, 31);
        /*--------------------------------------------*/
        String name = ship.getName();
        String planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        Date prodDate = ship.getProdDate();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();
        if (       name == null || name.equals("") || name.length() > 50
                || planet == null || planet.equals("") || planet.length() > 50
                || shipType == null
                || prodDate == null || prodDate.before(minDate.getTime()) || prodDate.after(maxDate.getTime())
                || speed == null || speed < 0.01 || speed > 0.99
                || crewSize == null || crewSize < 1 || crewSize > 9999) return false;
        else {
            ship.setSpeed(Math.round(ship.getSpeed() * 100) / 100.0d);
            return true;
        }
    }

    @Override
    public Ship createShip(Ship newShip) {
        if (!checkShip(newShip)) throw new BadRequestException();
        if (newShip.getUsed() == null) newShip.setUsed(false);
        newShip.setRating(Model.getRating(newShip.getSpeed(), newShip.getUsed(), newShip.getProdDate()));
        newShip.setId(null);
        return shipRepository.saveAndFlush(newShip);
    }

    @Override
    public List<Ship> getByAllCriteria(Map<String, String> params) {
        int pageNumber = params.containsKey("pageNumber") ? Integer.parseInt(params.get("pageNumber")) : 0;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize")) : 3;
        TypedQuery<Ship> tq = getTypedQuery(params);
        tq.setFirstResult((pageSize * pageNumber)).setMaxResults(pageSize);
        return tq.getResultList();
    }

    @Override
    public Integer getByAllCriteriaCount(Map<String, String> params) {
        TypedQuery<Ship> tq = getTypedQuery(params);
        return tq.getResultList().size();
    }

    private TypedQuery<Ship> getTypedQuery(Map<String, String> params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ship> cq = cb.createQuery(Ship.class);
        Root<Ship> root = cq.from(Ship.class);
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    predicates.add(cb.like(root.get(entry.getKey()), "%" + entry.getValue() + "%"));
                    break;
                case "planet":
                    predicates.add(cb.like(root.get(entry.getKey()), "%" + entry.getValue() + "%"));
                    break;
                case "shipType":
                    predicates.add(cb.equal(root.get(entry.getKey()), ShipType.valueOf(entry.getValue())));
                    break;
                case "after":
                    predicates.add(cb.greaterThanOrEqualTo(root.get("prodDate"), new Date(Long.parseLong(entry.getValue()))));
                    break;
                case "before":
                    predicates.add(cb.lessThanOrEqualTo(root.get("prodDate"), new Date(Long.parseLong(entry.getValue()))));
                    break;
                case "isUsed":
                    predicates.add(cb.equal(root.get(entry.getKey()), Boolean.parseBoolean(entry.getValue())));
                    break;
                case "minSpeed":
                    predicates.add(cb.greaterThanOrEqualTo(root.get("speed"), Double.parseDouble(entry.getValue())));
                    break;
                case "maxSpeed":
                    predicates.add(cb.lessThanOrEqualTo(root.get("speed"), Double.parseDouble(entry.getValue())));
                    break;
                case "minCrewSize":
                    predicates.add(cb.greaterThanOrEqualTo(root.get("crewSize"), Integer.parseInt(entry.getValue())));
                    break;
                case "maxCrewSize":
                    predicates.add(cb.lessThanOrEqualTo(root.get("crewSize"), Integer.parseInt(entry.getValue())));
                    break;
                case "minRating":
                    predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), Double.parseDouble(entry.getValue())));
                    break;
                case "maxRating":
                    predicates.add(cb.lessThanOrEqualTo(root.get("rating"), Double.parseDouble(entry.getValue())));
                    break;
                case "order":
                    cq.orderBy(cb.asc(root.get(ShipOrder.valueOf(entry.getValue()).getFieldName())));
                    break;
            }
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createEntityManager().createQuery(cq);
    }
}
