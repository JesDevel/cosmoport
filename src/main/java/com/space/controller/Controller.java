package com.space.controller;

import com.space.exception.BadRequestException;
import com.space.model.Ship;
import com.space.service.ShipService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @Resource
    private ShipService shipService;

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getShipsList(@RequestParam Map<String, String> allRequestParams) {
        return shipService.getByAllCriteria(allRequestParams);
    }

    @RequestMapping(value = "rest/ships/count", method = RequestMethod.GET)
    public Integer getShipsCount(@RequestParam Map<String, String> allRequestParams) {
        return shipService.getByAllCriteriaCount(allRequestParams);
    }

    @RequestMapping(value = "rest/ships", method = RequestMethod.POST)
    public Ship createShip(@RequestBody Ship newShip) {
        return shipService.createShip(newShip);
    }

    @RequestMapping(value = "rest/ships/{id:.+}", method = RequestMethod.GET)
    public Ship getShipById(@PathVariable("id") Long id) {
        if (id <= 0) throw new BadRequestException();
        return shipService.getById(id);
    }

    @RequestMapping(value = "rest/ships/{id:.+}", method = RequestMethod.POST)
    public Ship updateShip(@PathVariable("id") Long id, @RequestBody Map<String, String> params) {
        Ship updateShip = getShipById(id);
        return shipService.updateShip(updateShip, params);
    }

    @RequestMapping(value = "rest/ships/{id:.+}", method = RequestMethod.DELETE)
    public void deleteShip(@PathVariable("id") Long id) {
        Ship deletedShip = getShipById(id);
        shipService.deleteShip(deletedShip);
    }
}